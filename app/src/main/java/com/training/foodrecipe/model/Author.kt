package com.training.foodrecipe.model


import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("datePublished") val datePublished: String,
    @SerializedName("user") val user: String
)