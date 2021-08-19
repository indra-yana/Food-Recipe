package com.training.foodrecipe.datasource.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "need_items", indices = [Index(value = ["id"], unique = true)])
data class NeedItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "item_name")
    val itemName: String,

    @ColumnInfo(name = "thumb_item")
    val thumbItem: String
)