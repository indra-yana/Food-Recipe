package com.training.foodrecipe.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("tags") val tags: String?,
    @SerializedName("url") val url: String
) : Parcelable