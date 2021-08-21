package com.training.foodrecipe.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.training.foodrecipe.datasource.local.dao.*
import com.training.foodrecipe.model.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.05
 * https://gitlab.com/indra-yana
 ****************************************************/

@Database(
    entities = [
        Recipe::class,
        RecipeDetail::class,
        RecipeCategory::class,
        NeedItem::class,
        Author::class,
        ArticleCategory::class,
        ArticleDetail::class
    ],
    version = 11,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class RecipeDatabase : RoomDatabase() {

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        @JvmStatic
        val dbInstance get() = INSTANCE

        fun initDatabase(context: Context): RecipeDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "food_recipe_db").fallbackToDestructiveMigration().build()
                INSTANCE = instance

                // return instance
                instance
            }
        }
    }

    abstract fun getRecipeDao(): RecipeDao
    abstract fun getRecipeDetailDao(): RecipeDetailDao
    abstract fun getRecipeCategoryDao(): RecipeCategoryDao
    abstract fun getArticleCategoryDao(): ArticleCategoryDao
    abstract fun getArticleDetailDao(): ArticleDetailDao

}