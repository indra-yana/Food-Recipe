package com.training.foodrecipe.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.model.RecipeCategory
import com.training.foodrecipe.model.RecipeDetail

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.05
 * https://gitlab.com/indra-yana
 ****************************************************/

@Database(entities = [Recipe::class, RecipeDetail::class, RecipeCategory::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    companion object {
        private var db : RecipeDatabase? = null

        fun dbInstance(context: Context) : RecipeDatabase {
            if (db == null) {
                db = Room.databaseBuilder(context, RecipeDatabase::class.java, "food_recipe_db").build()
            }

            return db!!
        }
    }

    abstract fun getRecipeDao() : RecipeDao

}