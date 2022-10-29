package com.enesuzumcu.shoppingapp.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.shoppingapp.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel  @Inject constructor(private val dataStoreManager: DataStoreManager) : ViewModel(){

    private val _uiEvent = MutableSharedFlow<SplashViewEvent>(replay = 0)
    val uiEvent: SharedFlow<SplashViewEvent> = _uiEvent

    init {
        checkOnBoardingVisibleStatus()
    }

    private fun checkOnBoardingVisibleStatus(){
        viewModelScope.launch {
            dataStoreManager.getOnBoardingVisible.collect{
                if(it){
                    _uiEvent.emit(SplashViewEvent.NavigateToLogin)
                } else {
                    _uiEvent.emit(SplashViewEvent.NavigateToOnBoarding)
                }
            }
        }
    }
}

sealed class SplashViewEvent {
    object NavigateToLogin : SplashViewEvent()
    object NavigateToOnBoarding : SplashViewEvent()
}