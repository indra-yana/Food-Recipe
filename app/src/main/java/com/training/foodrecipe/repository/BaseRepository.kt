package com.training.foodrecipe.repository

import com.training.foodrecipe.BaseApplication
import com.training.foodrecipe.datasource.local.RecipeDatabase
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.02
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseRepository {

    protected val db: RecipeDatabase by lazy { BaseApplication.recipeDB }
    protected val api: IRecipeApi by lazy { BaseApplication.recipeApi }

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResponseStatus<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResponseStatus.Success(apiCall.invoke())
            } catch (exception: Exception) {
                ResponseStatus.Failure(exception)
            }
        }
    }

}