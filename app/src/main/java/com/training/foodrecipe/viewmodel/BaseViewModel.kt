package com.training.foodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.00
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseViewModel : ViewModel() {

    abstract fun getLatestRecipe(): Job
    abstract fun getRecipeByPage(page: Int): Job
    abstract fun searchRecipe(query: String): Job
    abstract fun getRecipeDetail(key: String): Job
    abstract fun getCategory(): Job
    abstract fun getRecipeByCategory(key: String): Job

}