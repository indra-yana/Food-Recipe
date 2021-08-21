package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.remote.response.ArticleCategoryResponse
import com.training.foodrecipe.datasource.remote.response.ArticleDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.model.ArticleCategory
import com.training.foodrecipe.model.ArticleDetail
import com.training.foodrecipe.model.RecipeCategory
import com.training.foodrecipe.model.RecipeDetail

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Thursday, 19/08/2021 22.52
 * https://gitlab.com/indra-yana
 ****************************************************/

class MapperEntity {

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
    }
}