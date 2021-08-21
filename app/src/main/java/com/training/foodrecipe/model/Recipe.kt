package com.training.foodrecipe.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/
@Parcelize
@Entity(tableName = "recipes", indices = [Index(value = ["id", "key"], unique = true)])
data class Recipe(
    @ColumnInfo(name = "id")
    val id: Int?,

    @PrimaryKey
    @ColumnInfo(name = "key")
    @SerializedName("key")
    val key: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "dificulty")
    @SerializedName("dificulty")
    val dificulty: String?,

    @ColumnInfo(name = "difficulty")
    @SerializedName("difficulty")
    val difficulty: String?,

    @ColumnInfo(name = "portion")
    @SerializedName("portion")
    val portion: String?,

    @ColumnInfo(name = "serving")
    @SerializedName("serving")
    val serving: String?,

    @ColumnInfo(name = "times")
    @SerializedName("times")
    val times: String,

    @ColumnInfo(name = "thumb")
    @SerializedName("thumb")
    val thumb: String
) : Parcelable