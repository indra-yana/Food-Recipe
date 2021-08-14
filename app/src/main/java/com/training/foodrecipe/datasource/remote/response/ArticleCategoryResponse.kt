package com.training.foodrecipe.datasource.remote.response


import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.ArticleCategory

data class ArticleCategoryResponse(
    @SerializedName("method") val method: String,
    @SerializedName("results") val articleCategories: List<ArticleCategory>,
    @SerializedName("status") val status: Boolean
)