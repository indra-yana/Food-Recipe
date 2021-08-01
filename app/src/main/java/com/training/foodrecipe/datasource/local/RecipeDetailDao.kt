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
interface RecipeDetailDao {

    @Query("SELECT * FROM recipes WHERE `key` = :key")
    suspend fun getRecipeDetail(key: String): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeDetail(recipeDetail: RecipeDetail)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT EXISTS (SELECT 1 FROM recipe_details WHERE `key` = :key)")
    suspend fun isRecipesExist(key: String): Boolean
}