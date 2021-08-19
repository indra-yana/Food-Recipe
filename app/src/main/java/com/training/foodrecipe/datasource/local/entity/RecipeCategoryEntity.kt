package com.training.foodrecipe.datasource.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/

@Entity(tableName = "recipe_categories", indices = [Index(value = ["id", "key"], unique = true)])
data class RecipeCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "url")
    val url: String
)