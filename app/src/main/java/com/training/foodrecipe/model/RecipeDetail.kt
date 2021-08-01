package com.training.foodrecipe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipe_details", indices = [Index(value = ["id"], unique = true)])
data class RecipeDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "author")
    @SerializedName("author")
    val author: Author,

    @ColumnInfo(name = "desc")
    @SerializedName("desc")
    val desc: String,

    @ColumnInfo(name = "dificulty")
    @SerializedName("dificulty")
    val dificulty: String,

    @ColumnInfo(name = "ingredient")
    @SerializedName("ingredient")
    val ingredient: List<String>,

    @ColumnInfo(name = "needItem")
    @SerializedName("needItem")
    val needItem: List<NeedItem>,

    @ColumnInfo(name = "servings")
    @SerializedName("servings")
    val servings: String,

    @ColumnInfo(name = "step")
    @SerializedName("step")
    val step: List<String>,

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    val thumb: String,

    @ColumnInfo(name = "times")
    @SerializedName("times")
    val times: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String
)