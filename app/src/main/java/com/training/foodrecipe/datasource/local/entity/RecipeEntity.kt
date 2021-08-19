package com.training.foodrecipe.datasource.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/
@Parcelize
@Entity(tableName = "recipes", indices = [Index(value = ["id", "key"], unique = true)])
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "dificulty")
    val dificulty: String?,

    @ColumnInfo(name = "difficulty")
    val difficulty: String?,

    @ColumnInfo(name = "portion")
    val portion: String?,

    @ColumnInfo(name = "serving")
    val serving: String?,

    @ColumnInfo(name = "times")
    val times: String,

    @ColumnInfo(name = "thumb")
    val thumb: String
) : Parcelable