package com.enesuzumcu.shoppingapp.domain.di

import com.enesuzumcu.shoppingapp.domain.usecase.signin.SignInUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @ViewModelScoped
    @Provides
    fun provideSignInUseCase(firebaseAuth: FirebaseAuth) = SignInUseCase(firebaseAuth)
}