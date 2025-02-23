package com.example.easyfood.model


import com.google.gson.annotations.SerializedName

data class MealsByCategory(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)