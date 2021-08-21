package com.training.foodrecipe.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article_details", indices = [Index(value = ["id", "key"], unique = true)])
data class ArticleDetail(
    @ColumnInfo(name = "id")
    val id: Int?,

    @PrimaryKey
    @ColumnInfo(name = "key")
    @SerializedName("key")
    val key: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    val thumb: String,

    @ColumnInfo(name = "author")
    @SerializedName("author")
    val author: String,

    @ColumnInfo(name = "date_published")
    @SerializedName("date_published")
    val datePublished: String,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String
)