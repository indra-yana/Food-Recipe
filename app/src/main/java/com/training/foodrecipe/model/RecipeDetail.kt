package com.training.foodrecipe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipe_details", indices = [Index(value = ["id", "key"], unique = true)])
data class RecipeDetail(
    @ColumnInfo(name = "id")
    val id: Int?,

    @PrimaryKey
    @ColumnInfo(name = "key")
    @SerializedName("key")
    var key: String,

    @ColumnInfo(name = "author")
    @SerializedName("author")
    val author: Author,

    @ColumnInfo(name = "desc")
    @SerializedName("desc")
    val desc: String,

    @ColumnInfo(name = "dificulty")
    @SerializedName("dificulty")
    val dificulty: String?,

    @ColumnInfo(name = "ingredient")
    @SerializedName("ingredient")
    val ingredient: List<String>,

    @ColumnInfo(name = "needItem")
    @SerializedName("needItem")
    val needItem: List<NeedItem>,

    @ColumnInfo(name = "servings")
    @SerializedName("servings")
    val servings: String?,

    @ColumnInfo(name = "step")
    @SerializedName("step")
    val step: List<String>,

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    var thumb: String?,

    @ColumnInfo(name = "times")
    @SerializedName("times")
    val times: String?,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "is_favourite")
    var favourite: Boolean = false
)