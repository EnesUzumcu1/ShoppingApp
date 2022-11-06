package com.enesuzumcu.shoppingapp.domain.usecase.productdetail

import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductDetailUseCase @Inject constructor(private val firebaseService: FirebaseService) :
    BaseUseCase<ProductDetailUseCaseParams, ProductDetailUseCaseState> {
    override fun invoke(params: ProductDetailUseCaseParams): Flow<ProductDetailUseCaseState> {
        return flow {
            emit(ProductDetailUseCaseState.Loading)
            addProductInBasket(params.productId,params.productDTO).collect {
                emit(it)
            }
        }
    }

    private fun addProductInBasket(productId: String, productDTO: ProductDTO) = callbackFlow {
        val callback = firebaseService.getUid()?.let {
            firebaseService.addProductInBasket(it, productId).set(productDTO).addOnCompleteListener { task->
                if (task.isSuccessful){
                    trySendBlocking(ProductDetailUseCaseState.Success("Add Successful"))
                } else if(task.isCanceled){
                    trySendBlocking(ProductDetailUseCaseState.Error(task.exception?.message.toString()))
                }

            }
        }
        awaitClose { callback?.isCanceled()}
    }
}

data class ProductDetailUseCaseParams(
    val productId: String,
    val productDTO : ProductDTO
)

sealed class ProductDetailUseCaseState {
    object Loading : ProductDetailUseCaseState()
    class Success(val message: String) : ProductDetailUseCaseState()
    data class Error(val error: String?) : ProductDetailUseCaseState()
}