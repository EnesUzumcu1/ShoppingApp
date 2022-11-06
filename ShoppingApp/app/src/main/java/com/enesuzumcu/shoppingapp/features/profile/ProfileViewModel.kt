package com.enesuzumcu.shoppingapp.features.profile

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.local.DataStoreManager
import com.enesuzumcu.shoppingapp.data.model.UserInfo
import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.domain.usecase.profile.ProfileUseCase
import com.enesuzumcu.shoppingapp.domain.usecase.profile.ProfileUseCaseParams
import com.enesuzumcu.shoppingapp.domain.usecase.profile.ProfileUseCaseState
import com.enesuzumcu.shoppingapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val firebaseService: FirebaseService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val alertDialog : SingleLiveEvent<AlertDialog> = SingleLiveEvent()


    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Empty)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<ProfileViewEvent>(replay = 0)
    val uiEvent: SharedFlow<ProfileViewEvent> = _uiEvent

    fun getUserInfo() {
        viewModelScope.launch {
            profileUseCase.invoke(ProfileUseCaseParams()).collect {
                when (it) {
                    is ProfileUseCaseState.Success -> {
                        _uiEvent.emit(ProfileViewEvent.ShowData(it.data))
                        _uiState.value = ProfileUiState.Empty
                    }
                    is ProfileUseCaseState.Error -> {
                        _uiEvent.emit(ProfileViewEvent.ShowError(it.error.toString()))
                        _uiState.value = ProfileUiState.Empty
                    }
                    is ProfileUseCaseState.Loading -> {
                        _uiState.value = ProfileUiState.Loading
                    }
                }
            }
        }
    }

    fun signout() {

        viewModelScope.launch {
            firebaseService.getUid()?.let {
                dataStoreManager.setUserName("")
            }
            firebaseService.signOut()
        }
    }
}

sealed class ProfileUiState {
    object Empty : ProfileUiState()
    object Loading : ProfileUiState()
}

sealed class ProfileViewEvent {
    class ShowData(val data: UserInfo) : ProfileViewEvent()
    class ShowError(val error: String) : ProfileViewEvent()
}