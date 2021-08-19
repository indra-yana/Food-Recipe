package com.training.foodrecipe.datasource.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.training.foodrecipe.model.Author
import com.training.foodrecipe.model.NeedItem
import java.util.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 18/08/2021 19.11
 * https://gitlab.com/indra-yana
 ****************************************************/

class TypeConverter {

    @TypeConverter
    fun fromAuthor(value: Author?): String? {
        return value?.let {
            Gson().toJson(value)
        }
    }

    @TypeConverter
    fun toAuthor(value: String?): Author? {
        return value?.let {
            Gson().fromJson(value, Author::class.java)
        }
    }

    @TypeConverter
    fun fromIngredient(value: List<String>?): String? {
        return value?.let {
            Gson().toJson(value)
        }
    }

    @TypeConverter
    fun toIngredient(value: String?): List<String>? {
        val type = object : TypeToken<List<String>?>() {}.type

        return value?.let {
            Gson().fromJson(value, type)
        }
    }

    @TypeConverter
    fun fromNeedItem(value: List<NeedItem>?): String? {
        return value?.let {
            Gson().toJson(value)
        }
    }

    @TypeConverter
    fun toNeedItem(value: String?): List<NeedItem>? {
        val type = object : TypeToken<List<NeedItem>?>() {}.type

        return value?.let {
            Gson().fromJson(value, type)
        }
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.let { date.time }
    }
}