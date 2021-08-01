package com.training.foodrecipe.datasource.remote.response

import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.RecipeCategory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/

data class RecipeCategoryResponse(
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: Boolean,
    @SerializedName("results") val recipeCategories: List<RecipeCategory>
)