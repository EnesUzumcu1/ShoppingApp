package com.enesuzumcu.shoppingapp.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.domain.usecase.signin.SignInUseCase
import com.enesuzumcu.shoppingapp.domain.usecase.signin.SignInUseCaseParams
import com.enesuzumcu.shoppingapp.domain.usecase.signin.SignInUseCaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val signInUseCase: SignInUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Empty)
    val uiState: StateFlow<SignInUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<SignInViewEvent>(replay = 0)
    val uiEvent: SharedFlow<SignInViewEvent> = _uiEvent

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isValidFields(email, password)?.let {
                _uiEvent.emit(SignInViewEvent.ShowError(it))
            } ?: kotlin.run {
                signInUseCase.invoke(SignInUseCaseParams(email, password)).collect {
                    when (it) {
                        is SignInUseCaseState.Loading -> {
                            _uiState.value = SignInUiState.Loading
                        }
                        is SignInUseCaseState.Error -> {
                            _uiState.value = SignInUiState.Empty
                            _uiEvent.emit(SignInViewEvent.ShowError(it.error.toString()))
                        }
                        is SignInUseCaseState.Success -> {
                            _uiState.value = SignInUiState.Empty
                            _uiEvent.emit(SignInViewEvent.NavigateToMain)
                        }
                    }
                }
            }
        }
    }

    private fun isValidFields(
        email: String,
        password: String
    ): String? {
        fun isValidEmptyField() =
            email.isNotEmpty() && password.isNotEmpty()

        fun isValidPasswordLength() = password.length >= 6
        fun isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (isValidEmptyField().not()) {
            return "Please fill all fields"
        } else if (isValidEmail().not()) {
            return "Please enter a valid email address"
        } else if (isValidPasswordLength().not()) {
            return "Password must be at least 6 characters"
        }
        return null
    }
}

sealed class SignInViewEvent {
    object NavigateToMain : SignInViewEvent()
    class ShowError(val error: String) : SignInViewEvent()
}

sealed class SignInUiState {
    object Empty : SignInUiState()
    object Loading : SignInUiState()
}