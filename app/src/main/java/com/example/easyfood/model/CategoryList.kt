package com.example.easyfood.model


import com.google.gson.annotations.SerializedName

data class CategoryList(
    @SerializedName("categories")
    val categories: List<Category>
)