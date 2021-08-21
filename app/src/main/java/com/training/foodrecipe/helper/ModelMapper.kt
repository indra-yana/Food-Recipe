package com.training.foodrecipe.helper

import com.training.foodrecipe.datasource.remote.response.*
import com.training.foodrecipe.model.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Thursday, 19/08/2021 22.52
 * https://gitlab.com/indra-yana
 ****************************************************/

class ModelMapper {

    companion object {
        @JvmStatic
        fun recipeDetailCategoryMapper(data: RecipeDetail): RecipeDetailResponse {
            return RecipeDetailResponse(method = "DB", status = true, recipeDetail = data)
        }

        @JvmStatic
        fun recipeCategoryMapper(data: List<RecipeCategory>): RecipeCategoryResponse {
            return RecipeCategoryResponse(method = "DB", status = true, recipeCategories = data)
        }

        @JvmStatic
        fun articleCategoryMapper(data: List<ArticleCategory>): ArticleCategoryResponse {
            return ArticleCategoryResponse(method = "DB", status = true, articleCategories = data)
        }

        @JvmStatic
        fun articleDetailMapper(data: ArticleDetail): ArticleDetailResponse {
            return ArticleDetailResponse(method = "DB", status = true, articleDetail = data)
        }

        @JvmStatic
        fun recipeDetailsToRecipeListMapper(data: List<RecipeDetail>): RecipeResponse {
            val recipe = data.map {
                Recipe(
                    id = it.id,
                    key = it.key,
                    title = it.title,
                    dificulty = it.dificulty,
                    difficulty = it.dificulty,
                    portion = it.servings,
                    serving = it.servings,
                    times = it.times ?: "",
                    thumb = it.thumb ?: ""
                )
            }

            return RecipeResponse(method = "DB", status = true, recipes = recipe)
        }

        @JvmStatic
        fun booleanMapper(data: Int): Boolean {
            return data == 1
        }
    }
}