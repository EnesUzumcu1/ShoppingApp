package com.enesuzumcu.shoppingapp.data.model


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("category")
    val category: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("rating")
    val rating: ProductRating?,
    @SerializedName("title")
    val title: String?
)

data class ProductDTO(
    val id: Long?,
    val price: Double?,
    val image: String?,
    val title: String?,
    var quantity: Long?
)