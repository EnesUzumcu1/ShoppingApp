package com.enesuzumcu.shoppingapp.data.di

import com.enesuzumcu.shoppingapp.data.remote.service.FirebaseService
import com.enesuzumcu.shoppingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RemoteDataModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory,
        baseApiUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseApiUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideBaseUrl() = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideGsonConvertorFactory() = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseService(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): FirebaseService = FirebaseService(firebaseAuth,firebaseFirestore)

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseFirestore = FirebaseFirestore.getInstance()
}