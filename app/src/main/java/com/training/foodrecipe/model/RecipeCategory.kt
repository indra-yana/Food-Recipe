package com.training.foodrecipe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/

@Entity(tableName = "recipe_categories", indices = [Index(value = ["id", "key"], unique = true)])
data class RecipeCategory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "key")
    @SerializedName("key")
    val key: String,

    @ColumnInfo(name = "category")
    @SerializedName("category")
    val category: String,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    val url: String
)