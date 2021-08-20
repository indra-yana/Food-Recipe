package com.training.foodrecipe.datasource.local.dao

import androidx.room.*
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.23
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE `key` = :key")
    suspend fun find(key: String): Recipe?

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    suspend fun all(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: Recipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(value: List<Recipe>)

    @Update
    suspend fun update(value: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()

    @Query("SELECT EXISTS (SELECT 1 FROM recipes WHERE `key` = :key)")
    suspend fun isRecipesExist(key: String): Boolean

}