package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.local.RecipeDatabase
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeResponse
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.ModelMapper

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.19
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeRepository(private val db: RecipeDatabase, private val api: IRecipeApi) : BaseRepository() {

    companion object {
        val TAG = RecipeRepository::class.java.simpleName
    }

    suspend fun getLatestRecipe(): ResponseStatus<RecipeResponse> = safeApiCall { api.getLatestRecipe() }
    suspend fun getRecipeByPage(page: Int): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByPage(page) }
    suspend fun searchRecipe(query: String): ResponseStatus<RecipeResponse> = safeApiCall { api.searchRecipe(query) }

    suspend fun getRecipeDetail(key: String): ResponseStatus<RecipeDetailResponse> {
        return safeApiCall {
            val cache = db.getRecipeDetailDao().find(key)

            if (cache != null) {
                ModelMapper.recipeDetailCategoryMapper(cache)
            } else {
                val apiResult = api.getRecipeDetail(key)
                db.getRecipeDetailDao().insert(apiResult.recipeDetail.copy(key = key))

                apiResult
            }
        }
    }

    suspend fun getCategory(): ResponseStatus<RecipeCategoryResponse> {
        return safeApiCall {
            val cache = db.getRecipeCategoryDao().all()

            if (!cache.isNullOrEmpty()) {
                ModelMapper.recipeCategoryMapper(cache)
            } else {
                val apiResult = api.getCategory()
                db.getRecipeCategoryDao().insert(apiResult.recipeCategories)

                apiResult
            }
        }
    }

    suspend fun getRecipeByCategory(key: String): ResponseStatus<RecipeResponse> = safeApiCall { api.getRecipeByCategory(key) }

    suspend fun setFavourite(key: String, isFavourite: Boolean): ResponseStatus<Boolean> {
        return safeApiCall {
            db.getRecipeDetailDao().setFavourite(key, isFavourite)

            ModelMapper.booleanMapper(db.getRecipeDetailDao().isFavourite(key))
        }
    }

    suspend fun getRecipeFavourite(key: String?): ResponseStatus<RecipeResponse> {
        return safeApiCall {
            val result = if (key == null) db.getRecipeDetailDao().getRecipeFavourite() else db.getRecipeDetailDao().getRecipeFavourite(key)

            ModelMapper.recipeDetailsToRecipeListMapper(result)
        }
    }

}