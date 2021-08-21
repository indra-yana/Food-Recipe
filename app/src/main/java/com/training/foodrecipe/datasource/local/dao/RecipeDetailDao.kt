package com.training.foodrecipe.datasource.local.dao

import androidx.room.*
import com.training.foodrecipe.model.RecipeDetail

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.23
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface RecipeDetailDao {

    @Query("SELECT * FROM recipe_details WHERE `key` = :key")
    suspend fun find(key: String): RecipeDetail?

    @Query("SELECT * FROM recipe_details")
    suspend fun all(): List<RecipeDetail>

    @Query("SELECT * FROM recipe_details WHERE is_favourite = 1")
    suspend fun getRecipeFavourite(): List<RecipeDetail>

    @Query("SELECT * FROM recipe_details WHERE `key` LIKE '%' || :key || '%' OR `title` LIKE '%' || :key || '%' AND is_favourite = 1")
    suspend fun getRecipeFavourite(key: String): List<RecipeDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: RecipeDetail)

    @Delete
    suspend fun delete(value: RecipeDetail)

    @Query("SELECT EXISTS (SELECT 1 FROM recipe_details WHERE `key` = :key)")
    suspend fun exist(key: String): Boolean

    @Query("UPDATE recipe_details SET `is_favourite` = :isFavourite WHERE `key` = :key")
    suspend fun setFavourite(key: String, isFavourite: Boolean): Int

    @Query("SELECT is_favourite FROM recipe_details WHERE `key` = :key")
    suspend fun isFavourite(key: String): Int

}