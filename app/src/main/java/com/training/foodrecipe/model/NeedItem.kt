package com.training.foodrecipe.model


import com.google.gson.annotations.SerializedName

data class NeedItem(
    @SerializedName("item_name") val itemName: String,
    @SerializedName("thumb_item") val thumbItem: String
)