package com.training.foodrecipe.datasource.remote.response


import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.ArticleDetail

data class ArticleDetailResponse(
    @SerializedName("method")
    val method: String,
    @SerializedName("results")
    val articleDetail: ArticleDetail,
    @SerializedName("status")
    val status: Boolean
)