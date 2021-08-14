package com.training.foodrecipe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.repository.BaseRepository
import com.training.foodrecipe.repository.RecipeRepository
import java.lang.IllegalArgumentException

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.59
 * https://gitlab.com/indra-yana
 ****************************************************/

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> RecipeViewModel(repository as RecipeRepository) as T
            modelClass.isAssignableFrom(ArticleViewModel::class.java) -> ArticleViewModel(repository as ArticleRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found!")
        }
    }
}