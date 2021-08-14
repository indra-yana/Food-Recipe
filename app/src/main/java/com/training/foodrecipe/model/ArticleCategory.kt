package com.training.foodrecipe.model


import com.google.gson.annotations.SerializedName

data class ArticleCategory(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String
)