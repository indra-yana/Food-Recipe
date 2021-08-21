package com.training.foodrecipe.datasource.local.dao

import androidx.room.*
import com.training.foodrecipe.model.RecipeCategory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 20/08/2021 20.41
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface RecipeCategoryDao {

    @Query("SELECT * FROM recipe_categories ORDER BY `key` ASC")
    suspend fun all(): List<RecipeCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: RecipeCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: List<RecipeCategory>)

    @Delete
    suspend fun delete(value: RecipeCategory)

    @Query("DELETE FROM recipe_categories")
    suspend fun delete()
}