package com.training.foodrecipe.datasource.remote.response

import com.google.gson.annotations.SerializedName
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.59
 * https://gitlab.com/indra-yana
 ****************************************************/

data class RecipeResponse(
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: Boolean,
    @SerializedName("results") val recipes: List<Recipe>
)