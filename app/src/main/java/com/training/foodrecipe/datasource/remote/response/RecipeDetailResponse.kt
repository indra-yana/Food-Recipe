package com.training.foodrecipe.datasource.remote.response


import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.RecipeDetail

data class RecipeDetailResponse(
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: Boolean,
    @SerializedName("results") val recipeDetail: RecipeDetail
)