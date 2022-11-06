package com.enesuzumcu.shoppingapp.features.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.domain.repository.ProductsRepository
import com.enesuzumcu.shoppingapp.domain.usecase.productdetail.ProductDetailUseCase
import com.enesuzumcu.shoppingapp.domain.usecase.productdetail.ProductDetailUseCaseParams
import com.enesuzumcu.shoppingapp.domain.usecase.productdetail.ProductDetailUseCaseState
import com.enesuzumcu.shoppingapp.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val firebaseService: FirebaseService,
    private val productDetailUseCase: ProductDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Empty)
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<ProductDetailViewEvent>(replay = 0)
    val uiEvent: SharedFlow<ProductDetailViewEvent> = _uiEvent

    private val _uiEventBasket = MutableSharedFlow<BasketViewEvent>(replay = 0)
    val uiEventBasket: SharedFlow<BasketViewEvent> = _uiEventBasket

    fun getDetail(productId: String) {
        viewModelScope.launch {
            productsRepository.getProductDetail(productId).collect {
                when (it) {
                    is DataState.Success -> {
                        _uiEvent.emit(ProductDetailViewEvent.ShowData(it.data))
                        _uiState.value = ProductDetailUiState.Empty
                    }
                    is DataState.Loading -> {
                        _uiState.value = ProductDetailUiState.Loading
                    }
                    is DataState.Error -> {
                        _uiEvent.emit(ProductDetailViewEvent.ShowError(it.errorMessage))
                        _uiState.value = ProductDetailUiState.Empty
                    }
                }
            }
        }
    }

    fun addToBasket(product: Product, quantity: Int) {
        viewModelScope.launch {
            firebaseService.getUid()?.let {
                productDetailUseCase.invoke(ProductDetailUseCaseParams(product.id.toString(), ProductDTO(
                    id = product.id?.toLong(),
                    title = product.title,
                    quantity = quantity.toLong(),
                    price = product.price,
                    image = product.image
                ))).collect {
                    when (it) {
                        is ProductDetailUseCaseState.Loading -> {

                        }
                        is ProductDetailUseCaseState.Success -> {
                            _uiEventBasket.emit(BasketViewEvent.ShowData(it.message))
                        }
                        is ProductDetailUseCaseState.Error -> {
                            _uiEventBasket.emit(BasketViewEvent.ShowError(it.error))
                        }

                    }
                }
            }

        }
    }

}

sealed class ProductDetailUiState {
    object Empty : ProductDetailUiState()
    object Loading : ProductDetailUiState()
}

sealed class ProductDetailViewEvent {
    class ShowData(val data: Product) : ProductDetailViewEvent()
    class ShowError(val error: String) : ProductDetailViewEvent()
}

sealed class BasketViewEvent {
    class ShowData(val message: String?) : BasketViewEvent()
    class ShowError(val error: String?) : BasketViewEvent()
}