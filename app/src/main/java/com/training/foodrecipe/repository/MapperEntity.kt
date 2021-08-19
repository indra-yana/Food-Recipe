package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
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
    }
}