package com.enesuzumcu.shoppingapp.domain.usecase.signin

import com.enesuzumcu.shoppingapp.domain.usecase.base.BaseUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) : BaseUseCase<SignInUseCaseParams, SignInUseCaseState> {
    override fun invoke(params: SignInUseCaseParams): Flow<SignInUseCaseState> {
        return flow {
            emit(SignInUseCaseState.Loading)
            sign(params.email,params.password).collect{
                emit(it)
            }
        }
    }
    private fun sign(email: String, password: String) = callbackFlow {
        val callback = firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySendBlocking(SignInUseCaseState.Success)
            }.addOnFailureListener {
                trySendBlocking(SignInUseCaseState.Error(it.message))
            }
        awaitClose { callback.isCanceled() }
    }

}

data class SignInUseCaseParams(
    val email: String,
    val password: String
)

sealed class SignInUseCaseState {
    object Success : SignInUseCaseState()
    data class Error(val error: String?) : SignInUseCaseState()
    object Loading : SignInUseCaseState()
}