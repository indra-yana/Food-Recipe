package com.training.foodrecipe.datasource.remote

import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 13.59
 * https://gitlab.com/indra-yana
 ****************************************************/

interface IRecipeApi {

    @GET("/api/recipes")
    suspend fun getLatestRecipe(): RecipeResponse

    @GET("/api/recipes/{page}")
    suspend fun getRecipeByPage(@Path("page") page: Int): RecipeResponse

    @GET("/api/search")
    suspend fun searchRecipe(@Query("q") query: String): RecipeResponse

    @GET("/api/recipe/{key}")
    suspend fun getRecipeDetail(@Path("key") key: String): RecipeDetailResponse

    @GET("/api/categorys/recipes")
    suspend fun getCategory(): RecipeCategoryResponse

    @GET("/api/categorys/recipes/{key}")
    suspend fun getRecipeByCategory(@Path("key") key: String): RecipeResponse

}