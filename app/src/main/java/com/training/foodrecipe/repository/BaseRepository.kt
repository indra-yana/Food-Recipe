package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeResponse
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.02
 * https://gitlab.com/indra-yana
 ****************************************************/

interface BaseRepository {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResponseStatus<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResponseStatus.Success(apiCall.invoke())
            } catch (exception: Exception) {
                ResponseStatus.Failure(exception)
            }
        }
    }

    suspend fun getLatestRecipe(): ResponseStatus<RecipeResponse>
    suspend fun getRecipeByPage(page: Int): ResponseStatus<RecipeResponse>
    suspend fun searchRecipe(query: String): ResponseStatus<RecipeResponse>
    suspend fun getRecipeDetail(key: String): ResponseStatus<RecipeDetailResponse>
    suspend fun getCategory(): ResponseStatus<RecipeCategoryResponse>
    suspend fun getRecipeByCategory(key: String): ResponseStatus<RecipeResponse>

}