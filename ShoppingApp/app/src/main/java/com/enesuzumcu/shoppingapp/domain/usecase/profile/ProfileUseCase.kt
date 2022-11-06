package com.enesuzumcu.shoppingapp.domain.usecase.profile

import com.enesuzumcu.shoppingapp.data.model.UserInfo
import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.domain.usecase.base.BaseUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val firebaseService: FirebaseService,
    private val firebaseAuth: FirebaseAuth
) :
    BaseUseCase<ProfileUseCaseParams, ProfileUseCaseState> {


    override fun invoke(params: ProfileUseCaseParams): Flow<ProfileUseCaseState> {
        return flow {
            emit(ProfileUseCaseState.Loading)
            getUserData().collect {
                emit(it)
            }
        }
    }

    private fun getUserData() = callbackFlow {
        val callback = firebaseService.getUid()?.let {
            firebaseService.getUserInformation(it).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySendBlocking(
                        ProfileUseCaseState.Success(
                            UserInfo(
                                username = task.result.data?.get("username").toString(),
                                completedOrder = task.result.data?.get("completedOrder").toString(),
                                usermail = firebaseAuth.currentUser?.email
                            )
                        )
                    )
                } else {
                    trySendBlocking(ProfileUseCaseState.Error(task.exception?.message.toString()))
                }
            }
        }
        awaitClose { callback?.isCanceled() }
    }
}

class ProfileUseCaseParams()

sealed class ProfileUseCaseState {
    object Loading : ProfileUseCaseState()
    class Success(val data: UserInfo) : ProfileUseCaseState()
    data class Error(val error: String?) : ProfileUseCaseState()
}