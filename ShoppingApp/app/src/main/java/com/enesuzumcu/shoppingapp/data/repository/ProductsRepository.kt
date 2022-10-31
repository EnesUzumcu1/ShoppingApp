package com.enesuzumcu.shoppingapp.data.repository

import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.data.model.ProductsResponse
import com.enesuzumcu.shoppingapp.utils.DataState
import kotlinx.coroutines.flow.Flow


interface ProductsRepository {
    suspend fun getAllProducts(): Flow<DataState<ProductsResponse>>
    suspend fun getProductDetail(productId: String) : Flow<DataState<Product>>
    suspend fun getAllCategories(): Flow<DataState<CategoriesResponse>>
    suspend fun getSpecificCategory(category: String) : Flow<DataState<ProductsResponse>>

}