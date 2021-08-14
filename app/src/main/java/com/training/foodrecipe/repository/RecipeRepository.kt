package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeResponse
import com.training.foodrecipe.datasource.remote.response.ResponseStatus

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.19
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeRepository(private val api: IRecipeApi) : BaseRepository {

    suspend fun getLatestRecipe(): ResponseStatus<RecipeResponse> = safeApiCall { api.getLatestRecipe() }
    suspend fun getRecipeByPage(page: Int): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByPage(page) }
    suspend fun searchRecipe(query: String): ResponseStatus<RecipeResponse> = safeApiCall { api.searchRecipe(query) }
    suspend fun getRecipeDetail(key: String): ResponseStatus<RecipeDetailResponse> = safeApiCall { api.getRecipeDetail(key) }
    suspend fun getCategory(): ResponseStatus<RecipeCategoryResponse> = safeApiCall { api.getCategory() }
    suspend fun getRecipeByCategory(key: String): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByCategory(key) }

}