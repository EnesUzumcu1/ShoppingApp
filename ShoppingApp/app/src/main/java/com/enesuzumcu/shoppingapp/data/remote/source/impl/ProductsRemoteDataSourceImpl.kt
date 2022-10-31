package com.enesuzumcu.shoppingapp.data.remote.source.impl

import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.data.model.ProductsResponse
import com.enesuzumcu.shoppingapp.data.remote.api.ProductsApiService
import com.enesuzumcu.shoppingapp.data.remote.source.ProductsRemoteDataSource
import com.enesuzumcu.shoppingapp.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsRemoteDataSourceImpl @Inject constructor(private val productsService: ProductsApiService) : BaseRemoteDataSource(),
    ProductsRemoteDataSource {
    override suspend fun getAllProducts(): Flow<DataState<ProductsResponse>> {
        return getResult { productsService.getAllProducts() }
    }

    override suspend fun getProductDetail(productId: String): Flow<DataState<Product>> {
        return getResult { productsService.getProductDetail(productId) }
    }

    override suspend fun getAllCategories(): Flow<DataState<CategoriesResponse>> {
        return getResult { productsService.getAllCategories() }
    }

    override suspend fun getSpecificCategory(category: String): Flow<DataState<ProductsResponse>> {
        return getResult { productsService.getSpecificCategory(category) }
    }
}