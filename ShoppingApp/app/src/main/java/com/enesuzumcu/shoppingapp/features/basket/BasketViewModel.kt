package com.enesuzumcu.shoppingapp.features.basket

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.domain.usecase.basket.BasketUseCase
import com.enesuzumcu.shoppingapp.domain.usecase.basket.BasketUseCaseParams
import com.enesuzumcu.shoppingapp.domain.usecase.basket.BasketUseCaseState
import com.enesuzumcu.shoppingapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(private val basketUseCase: BasketUseCase) : ViewModel() {

    val alertDialog : SingleLiveEvent<AlertDialog> = SingleLiveEvent()

    private val _uiState = MutableStateFlow<BasketUiState>(BasketUiState.Empty)
    val uiState: StateFlow<BasketUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<BasketViewEvent>(replay = 0)
    val uiEvent: SharedFlow<BasketViewEvent> = _uiEvent

    private val _uiEventDelete = MutableSharedFlow<BasketDeleteViewEvent>(replay = 0)
    val uiEventDelete: SharedFlow<BasketDeleteViewEvent> = _uiEventDelete

    init {
        getProductsInBasket()
    }

    fun getProductsInBasket() {
        viewModelScope.launch {
            basketUseCase.invoke(BasketUseCaseParams(0, "",0)).collect {
                when (it) {
                    is BasketUseCaseState.Loading -> {
                        _uiState.value = BasketUiState.Loading
                    }
                    is BasketUseCaseState.Success -> {
                        _uiEvent.emit(BasketViewEvent.ShowData(it.data))
                        _uiState.value = BasketUiState.Empty
                    }
                    is BasketUseCaseState.Error -> {
                        it.error?.let { safeMessage ->
                            _uiEvent.emit(BasketViewEvent.ShowError(safeMessage))
                        } ?: kotlin.run {
                            _uiEvent.emit(BasketViewEvent.ShowError("Fail"))
                        }
                        _uiState.value = BasketUiState.Empty
                    }
                }
            }
        }
    }

    fun deleteProductInBasket(productId: String) {
        viewModelScope.launch {
            basketUseCase.invoke(BasketUseCaseParams(1, productId, 0)).collect {
                when (it) {
                    is BasketUseCaseState.Loading -> {
                        _uiState.value = BasketUiState.Loading
                    }
                    is BasketUseCaseState.Success -> {
                        getProductsInBasket()
                        //It prevents it from updating when the last item in the cart is deleted.
                        _uiEvent.emit(BasketViewEvent.ShowData(it.data))
                        _uiEventDelete.emit(BasketDeleteViewEvent.ShowData("Successfully deleted"))
                        _uiState.value = BasketUiState.Empty
                    }
                    is BasketUseCaseState.Error -> {
                        it.error?.let { safeMessage ->
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError(safeMessage))
                        } ?: kotlin.run {
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError("Fail"))
                        }
                        _uiState.value = BasketUiState.Empty
                    }
                }
            }
        }
    }

    fun updateProductQuantity(productId: String, newQuantity: Int){
        viewModelScope.launch {
            basketUseCase.invoke(BasketUseCaseParams(2,productId,newQuantity)).collect{
                when (it) {
                    is BasketUseCaseState.Loading -> {
                        _uiState.value = BasketUiState.Loading
                    }
                    is BasketUseCaseState.Success -> {
                        getProductsInBasket()
                        _uiEventDelete.emit(BasketDeleteViewEvent.ShowData("Quantity updated"))
                        _uiState.value = BasketUiState.Empty
                    }
                    is BasketUseCaseState.Error -> {
                        it.error?.let { safeMessage ->
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError(safeMessage))
                        } ?: kotlin.run {
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError("Fail"))
                        }
                        _uiState.value = BasketUiState.Empty
                    }
                }
            }
        }
    }

    fun deleteAllProductsInBasket(){
        viewModelScope.launch {
            basketUseCase.invoke(BasketUseCaseParams(3,"",0)).collect{
                when (it) {
                    is BasketUseCaseState.Loading -> {
                        _uiState.value = BasketUiState.Loading
                    }
                    is BasketUseCaseState.Success -> {
                        getProductsInBasket()
                        _uiEvent.emit(BasketViewEvent.ShowData(it.data))
                        _uiEventDelete.emit(BasketDeleteViewEvent.ShowData("Your Cart Has Been Approved."))
                        _uiState.value = BasketUiState.Empty
                    }
                    is BasketUseCaseState.Error -> {
                        it.error?.let { safeMessage ->
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError(safeMessage))
                        } ?: kotlin.run {
                            _uiEventDelete.emit(BasketDeleteViewEvent.ShowError("Fail"))
                        }
                        _uiState.value = BasketUiState.Empty
                    }
                }
            }
        }
    }

    fun calculateTotalPrice(list: MutableList<ProductDTO>) :Double{
        var totalPrice = 0.0
        list.forEach { productDTO ->
            productDTO.price?.let { price->
                productDTO.quantity?.let { quantity->
                    var productPrice = price * quantity
                    productPrice = String.format("%.2f", productPrice).toDouble()
                    totalPrice+=productPrice
                }
            }
        }
        totalPrice = String.format("%.2f", totalPrice).toDouble()
        return totalPrice
    }
}

sealed class BasketUiState {
    object Empty : BasketUiState()
    object Loading : BasketUiState()
}

sealed class BasketViewEvent {
    class ShowData(val data: MutableList<ProductDTO>) : BasketViewEvent()
    class ShowError(val error: String) : BasketViewEvent()
}

sealed class BasketDeleteViewEvent {
    class ShowData(val message: String) : BasketDeleteViewEvent()
    class ShowError(val error: String) : BasketDeleteViewEvent()
}