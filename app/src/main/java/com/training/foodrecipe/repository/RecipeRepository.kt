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

    override suspend fun getLatestRecipe(): ResponseStatus<RecipeResponse> = safeApiCall { api.getLatestRecipe() }
    override suspend fun getRecipeByPage(page: Int): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByPage(page) }
    override suspend fun searchRecipe(query: String): ResponseStatus<RecipeResponse> = safeApiCall { api.searchRecipe(query) }
    override suspend fun getRecipeDetail(key: String): ResponseStatus<RecipeDetailResponse> = safeApiCall { api.getRecipeDetail(key) }
    override suspend fun getCategory(): ResponseStatus<RecipeCategoryResponse> = safeApiCall { api.getCategory() }
    override suspend fun getRecipeByCategory(key: String): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByCategory(key) }

}