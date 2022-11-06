package com.enesuzumcu.shoppingapp.domain.usecase.basket

import com.enesuzumcu.shoppingapp.data.model.ProductDTO
import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BasketUseCase @Inject constructor(
    private val firebaseService: FirebaseService
) : BaseUseCase<BasketUseCaseParams, BasketUseCaseState> {

    override fun invoke(params: BasketUseCaseParams): Flow<BasketUseCaseState> {
        return flow {
            emit(BasketUseCaseState.Loading)
            getProductsInBasket(
                params.processType,
                productsId = params.productsId,
                params.newQuantity
            ).collect {
                emit(it)
            }
        }

    }

    //0-> getAllProductInBasket 1->DeleteProduct 2->UpdateProduct 3->DeleteAllProductInBasket
    private fun getProductsInBasket(processType: Int, productsId: String, newQuantity: Int) =
        callbackFlow {
            when (processType) {
                0 -> {
                    val callback = firebaseService.getUid()?.let {
                        firebaseService.getProductsInBasket(it).get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    if (task.result.isEmpty.not()) {
                                        val list = task.result.documents.map {
                                            ProductDTO(
                                                id = it.get("id") as Long?,
                                                price = it.get("price") as Double?,
                                                image = it.get("image") as String?,
                                                title = it.get("title") as String?,
                                                quantity = it.get("quantity") as Long?

                                            )
                                        }
                                        trySendBlocking(
                                            BasketUseCaseState.Success(list.toMutableList())
                                        )
                                    }

                                } else {
                                    trySendBlocking(BasketUseCaseState.Error(task.exception?.message.toString()))
                                }

                            }
                    }
                    awaitClose { callback?.isCanceled() }
                }
                1 -> {
                    val callback = if (productsId != "") {
                        deleteProductInBasket(productsId)
                        trySendBlocking(BasketUseCaseState.Success(mutableListOf()))
                    } else {
                        trySendBlocking(BasketUseCaseState.Error("Delete failed"))
                    }
                    awaitClose { callback.isClosed }

                }
                2 -> {
                    val callback = if (productsId != "" && newQuantity != 0) {
                        updateProductQuantity(productsId, newQuantity)
                        trySendBlocking(BasketUseCaseState.Success(mutableListOf()))
                    } else {
                        trySendBlocking(BasketUseCaseState.Error("Delete failed"))
                    }
                    awaitClose { callback.isClosed }
                }
                3 -> {
                    val callback = firebaseService.getUid()?.let {
                        firebaseService.deleteAllProductsInBasket(it)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    trySendBlocking(BasketUseCaseState.Error(e.message.toString()))
                                }
                                snapshot?.forEach {
                                    it.reference.delete()
                                        .addOnSuccessListener { void ->
                                            trySendBlocking(BasketUseCaseState.Success(mutableListOf()))

                                        }
                                        .addOnFailureListener {
                                            trySendBlocking(BasketUseCaseState.Error(it.message.toString()))
                                        }
                                }
                                updateUserData()
                            }
                    }
                    awaitClose { callback?.remove() }
                }

            }

        }

    private fun deleteProductInBasket(productId: String) {
        firebaseService.getUid()?.let {
            firebaseService.getProductsInBasket(it).document(productId).delete()
        }
    }

    private fun updateProductQuantity(productsId: String, newQuantity: Int) {
        firebaseService.getUid()?.let {
            firebaseService.addProductInBasket(it, productsId).update("quantity", newQuantity)
        }
    }

    private fun updateUserData() {
        firebaseService.getUid()?.let { it ->
            firebaseService.getUserInformation(it).get().addOnSuccessListener { document ->
                var completedOrder = document.get("completedOrder").toString().toInt()
                completedOrder++
                firebaseService.getUserInformation(it).update("completedOrder", completedOrder)
            }
        }
    }
}

data class BasketUseCaseParams(
    val processType: Int,
    val productsId: String,
    val newQuantity: Int
)

sealed class BasketUseCaseState {
    object Loading : BasketUseCaseState()
    class Success(val data: MutableList<ProductDTO>) : BasketUseCaseState()
    data class Error(val error: String?) : BasketUseCaseState()
}

