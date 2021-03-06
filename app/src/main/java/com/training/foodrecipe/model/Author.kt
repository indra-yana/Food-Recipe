package com.training.foodrecipe.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "authors", indices = [Index(value = ["id"], unique = true)])
data class Author(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "datePublished")
    @SerializedName("datePublished")
    val datePublished: String,

    @ColumnInfo(name = "user")
    @SerializedName("user")
    val user: String
)