package com.training.foodrecipe.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article_categories", indices = [Index(value = ["id", "key"], unique = true)])
data class ArticleCategory(
    @ColumnInfo(name = "id")
    val id: Int?,

    @PrimaryKey
    @ColumnInfo(name = "key")
    @SerializedName("key")
    val key: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String
)