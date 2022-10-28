package com.enesuzumcu.shoppingapp.data.di

import com.enesuzumcu.shoppingapp.data.remote.api.ProductsApiService
import com.enesuzumcu.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.enesuzumcu.shoppingapp.data.remote.source.impl.ProductsRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ProductsModule {

    @Singleton
    @Provides
    fun provideProductsService(retrofit: Retrofit) = retrofit.create(ProductsApiService::class.java)

    @Singleton
    @Provides
    fun provideProductsRemoteDataSource(productsService: ProductsApiService): ProductsRemoteDataSource =
        ProductsRemoteDataSourceImpl(productsService)
}