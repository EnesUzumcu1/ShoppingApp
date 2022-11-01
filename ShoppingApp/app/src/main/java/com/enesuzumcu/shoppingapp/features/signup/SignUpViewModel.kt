package com.enesuzumcu.shoppingapp.features.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.local.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Empty)
    val uiState: StateFlow<SignUpUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<SignUpViewEvent>(replay = 0)
    val uiEvent: SharedFlow<SignUpViewEvent> = _uiEvent

    fun register(email: String, password: String, confirmPassword: String, userName: String) {
        viewModelScope.launch {
            isValidFields(email, password, confirmPassword, userName)?.let {
                _uiEvent.emit(SignUpViewEvent.ShowError(it))
            } ?: kotlin.run {
                _uiState.value = SignUpUiState.Loading
                firebaseAuth.createUserWithEmailAndPassword(
                    email, password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setUserName(userName, task.result.user?.uid)
                        _uiState.value = SignUpUiState.Empty
                    } else {
                        viewModelScope.launch {
                            _uiEvent.emit(SignUpViewEvent.ShowError(task.exception?.message.toString()))
                            _uiState.value = SignUpUiState.Empty
                        }
                    }
                }
            }
        }
    }

    private fun setUserName(userName: String, uuid: String?) {
        viewModelScope.launch {
            dataStoreManager.setUserName(userName)
            fireStore.collection("users").add(mapOf("username" to userName, "uuid" to uuid))
                .addOnSuccessListener { documentReference ->
                    viewModelScope.launch { _uiEvent.emit(SignUpViewEvent.NavigateToMain) }
                }.addOnFailureListener { error ->
                    viewModelScope.launch {
                        _uiEvent.emit(SignUpViewEvent.ShowError(error.message.toString()))
                    }
                }
        }
    }

    private fun isValidFields(
        email: String,
        password: String,
        confirmPassword: String,
        userName: String
    ): String? {
        fun isValidEmptyField() =
            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && userName.isNotEmpty()

        fun isValidConfirmPassword() = password == confirmPassword
        fun isValidPasswordLength() = password.length >= 6
        fun isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (isValidEmptyField().not()) {
            return "Please fill all fields"
        } else if (isValidEmail().not()) {
            return "Please enter a valid email address"
        } else if (isValidConfirmPassword().not()) {
            return "Passwords do not match"
        } else if (isValidPasswordLength().not()) {
            return "Password must be at least 6 characters"
        }
        return null
    }
}

sealed class SignUpViewEvent {
    object NavigateToMain : SignUpViewEvent()
    class ShowError(val error: String) : SignUpViewEvent()
}

sealed class SignUpUiState {
    object Empty : SignUpUiState()
    object Loading : SignUpUiState()
}