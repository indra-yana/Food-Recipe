package com.training.foodrecipe.model


import com.google.gson.annotations.SerializedName

data class ArticleDetail(
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumb: String,
    @SerializedName("author") val author: String,
    @SerializedName("date_published") val datePublished: String,
    @SerializedName("description") val description: String
)