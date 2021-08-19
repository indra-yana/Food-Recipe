package com.training.foodrecipe.datasource.local.entity

import androidx.room.*
import java.util.*

@Entity(tableName = "recipe_details", indices = [Index(value = ["id", "key"], unique = true)])
data class RecipeDetailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "author")
    val author: AuthorEntity,

    @ColumnInfo(name = "desc")
    val desc: String,

    @ColumnInfo(name = "dificulty")
    val dificulty: String,

    @ColumnInfo(name = "ingredient")
    val ingredient: List<String>,

    @ColumnInfo(name = "needItem")
    val needItem: List<NeedItemEntity>,

    @ColumnInfo(name = "servings")
    val servings: String,

    @ColumnInfo(name = "step")
    val step: List<String>,

    @ColumnInfo(name = "thumb")
    val thumb: String,

    @ColumnInfo(name = "times")
    val times: String,

    @ColumnInfo(name = "title")
    val title: String
)