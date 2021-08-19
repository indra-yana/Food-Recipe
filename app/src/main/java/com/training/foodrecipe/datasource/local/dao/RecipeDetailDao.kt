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
    suspend fun find(key: String): RecipeDetail

    @Query("SELECT * FROM recipe_details")
    suspend fun all(): List<RecipeDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeDetail: RecipeDetail)

    @Delete
    suspend fun delete(recipeDetail: RecipeDetail)

    @Query("SELECT EXISTS (SELECT 1 FROM recipe_details WHERE `key` = :key)")
    suspend fun checkIfExist(key: String): Boolean
}