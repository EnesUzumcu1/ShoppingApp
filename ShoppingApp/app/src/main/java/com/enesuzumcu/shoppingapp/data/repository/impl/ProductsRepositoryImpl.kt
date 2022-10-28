package com.enesuzumcu.shoppingapp.data.repository.impl

import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.data.model.ProductsResponse
import com.enesuzumcu.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.enesuzumcu.shoppingapp.data.remote.utils.DataState
import com.enesuzumcu.shoppingapp.data.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(private val productsRemoteDataSource: ProductsRemoteDataSource) :
    ProductsRepository {
    override suspend fun getAllProducts(): Flow<DataState<ProductsResponse>> {
        return productsRemoteDataSource.getAllProducts()
    }

    override suspend fun getProductDetail(productId: String): Flow<DataState<Product>> {
        return productsRemoteDataSource.getProductDetail(productId)
    }

    override suspend fun getAllCategories(): Flow<DataState<CategoriesResponse>> {
        return productsRemoteDataSource.getAllCategories()
    }

    override suspend fun getSpecificCategory(category: String): Flow<DataState<ProductsResponse>> {
        return productsRemoteDataSource.getSpecificCategory(category)
    }

}