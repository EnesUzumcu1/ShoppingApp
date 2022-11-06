package com.enesuzumcu.shoppingapp.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.domain.repository.ProductsRepository
import com.enesuzumcu.shoppingapp.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
) : ViewModel() {

    var clickStatus = false

    private val _uiState = MutableStateFlow<SearchViewState>(
        SearchViewState.Success(
            mutableListOf(),
            mutableListOf()
        )
    )
    val uiState: StateFlow<SearchViewState> = _uiState

    private val _uiEvent = MutableSharedFlow<SearchViewEvent>(replay = 0)
    val uiEvent: SharedFlow<SearchViewEvent> = _uiEvent


    private val _uiStateCategory = MutableStateFlow<CategoryViewState>(CategoryViewState.Success(CategoriesResponse()))
    val uiStateCategory: StateFlow<CategoryViewState> = _uiStateCategory


    init {
        getAllProducts("")
        getAllCategory()
    }

    fun getAllProducts(categoryName: String) {
        viewModelScope.launch {
            if(categoryName == "") {
                productsRepository.getAllProducts().collect {
                    when (it) {
                        is DataState.Success -> {
                            _uiState.value =
                                SearchViewState.Success(it.data.toMutableList(), mutableListOf())
                        }
                        is DataState.Error -> {
                            _uiEvent.emit(SearchViewEvent.ShowError(it.errorMessage))
                        }
                        is DataState.Loading -> {
                            _uiState.value = SearchViewState.Loading
                        }
                    }
                }
            }else{
                getSpecificCategory(categoryName)
            }
            clickStatus = clickStatus.not()
        }
    }

    private fun getAllCategory() {
        viewModelScope.launch {
            productsRepository.getAllCategories().collect {
                when (it) {
                    is DataState.Success -> {
                        _uiStateCategory.value =
                            CategoryViewState.Success(it.data)
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(SearchViewEvent.ShowError(it.errorMessage))
                    }
                    is DataState.Loading -> {
                        _uiStateCategory.value = CategoryViewState.Loading
                    }
                }
            }
        }
    }



    private fun getSpecificCategory(categoryName: String) {
        viewModelScope.launch {
            productsRepository.getSpecificCategory(categoryName).collect{
                when(it){
                    is DataState.Success -> {
                        _uiState.value =
                            SearchViewState.Success(it.data.toMutableList(), mutableListOf())
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(SearchViewEvent.ShowError(it.errorMessage))
                    }
                    is DataState.Loading -> {
                        _uiState.value = SearchViewState.Loading
                    }
                }
            }
        }
    }



    fun searchProduct(query: String) {
        viewModelScope.launch {
            val updateQuery = query.lowercase(Locale.getDefault())

            val currentData = (_uiState.value as SearchViewState.Success).allProducts
            if (updateQuery != "") {
                currentData?.let {
                    val filteredList = it.filter {
                        it.title?.lowercase(Locale.getDefault())?.contains(updateQuery) ?: false
                    }
                    _uiState.value =
                        SearchViewState.Success(currentData, filteredList.toMutableList())
                }
            } else {
                _uiState.value =
                    SearchViewState.Success(currentData, mutableListOf())
            }
        }
    }

}

sealed class SearchViewEvent {
    data class ShowError(val message: String?) : SearchViewEvent()
}

sealed class SearchViewState {
    class Success(val allProducts: MutableList<Product>?, val filteredData: MutableList<Product>?) :
        SearchViewState()

    object Loading : SearchViewState()
}

sealed class CategoryViewState {
    class Success(val allCategories: CategoriesResponse?) : CategoryViewState()
    object Loading : CategoryViewState()
}