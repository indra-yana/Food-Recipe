package com.training.foodrecipe.datasource.local

import androidx.room.*
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.model.RecipeDetail

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.23
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY ID DESC")
    suspend fun getAllRecipes(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(vararg recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT EXISTS (SELECT 1 FROM recipes WHERE `key` = :key)")
    suspend fun isRecipesExist(key: String): Boolean

}