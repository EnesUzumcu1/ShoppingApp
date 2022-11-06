package com.enesuzumcu.shoppingapp.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.domain.repository.ProductsRepository
import com.enesuzumcu.shoppingapp.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<HomeViewState>(HomeViewState.Success(mutableListOf()))
    val uiState: StateFlow<HomeViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<HomeViewEvent>(replay = 0)
    val uiEvent: SharedFlow<HomeViewEvent> = _uiEvent

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            productsRepository.getAllProducts().collect {
                when (it) {
                    is DataState.Success -> {
                        _uiState.value = HomeViewState.Success(it.data.toMutableList())
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(HomeViewEvent.ShowError(it.errorMessage))
                    }
                    is DataState.Loading -> {
                        _uiState.value = HomeViewState.Loading
                    }
                }
            }
        }
    }
}

sealed class HomeViewEvent {
    data class ShowError(val message: String?) : HomeViewEvent()
}

sealed class HomeViewState {
    class Success(val allProducts: MutableList<Product>?) : HomeViewState()
    object Loading : HomeViewState()
}