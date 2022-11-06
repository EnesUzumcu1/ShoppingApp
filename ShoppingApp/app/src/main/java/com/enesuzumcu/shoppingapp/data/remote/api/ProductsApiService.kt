package com.enesuzumcu.shoppingapp.data.remote.api

import com.enesuzumcu.shoppingapp.data.model.CategoriesResponse
import com.enesuzumcu.shoppingapp.data.model.Product
import com.enesuzumcu.shoppingapp.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsApiService {
    @GET("products")
    suspend fun getAllProducts(): Response<ProductsResponse>

    @GET("products/{product_id}")
    suspend fun getProductDetail(@Path("product_id") productId: String) : Response<Product>

    @GET("products/categories")
    suspend fun getAllCategories(): Response<CategoriesResponse>

    @GET("products/category/{category_name}")
    suspend fun getSpecificCategory(@Path("category_name") category: String): Response<ProductsResponse>


}