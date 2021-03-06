package com.training.foodrecipe.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "need_items", indices = [Index(value = ["id"], unique = true)])
data class NeedItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "item_name")
    @SerializedName("item_name")
    val itemName: String,

    @ColumnInfo(name = "thumb_item")
    @SerializedName("thumb_item")
    val thumbItem: String
)