package com.training.foodrecipe.datasource.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "authors", indices = [Index(value = ["id"], unique = true)])
data class AuthorEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "datePublished")
    val datePublished: String,

    @ColumnInfo(name = "user")
    val user: String
)