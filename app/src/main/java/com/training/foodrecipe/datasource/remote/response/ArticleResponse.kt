package com.training.foodrecipe.datasource.remote.response


import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.Article

data class ArticleResponse(
    @SerializedName("method") val method: String,
    @SerializedName("results") val articles: List<Article>,
    @SerializedName("status") val status: Boolean
)