package com.training.foodrecipe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.training.foodrecipe.BaseApplication
import com.training.foodrecipe.datasource.remote.response.RecipeCategoryResponse
import com.training.foodrecipe.datasource.remote.response.RecipeDetailResponse
import com.training.foodrecipe.datasource.remote.response.RecipeResponse
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.model.RecipeDetail
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.base.BaseRecipeViewModel
import kotlinx.coroutines.launch

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.06
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeViewModel(private val repository: RecipeRepository) : BaseRecipeViewModel() {

    private val _latestRecipe: MutableLiveData<ResponseStatus<RecipeResponse>> = MutableLiveData()
    private val _recipe: MutableLiveData<ResponseStatus<RecipeResponse>> = MutableLiveData()
    private val _searchRecipe: MutableLiveData<ResponseStatus<RecipeResponse>> = MutableLiveData()
    private val _recipeDetail: MutableLiveData<ResponseStatus<RecipeDetailResponse>> = MutableLiveData()
    private val _recipeCategory: MutableLiveData<ResponseStatus<RecipeCategoryResponse>> = MutableLiveData()
    private val _recipeByCategory: MutableLiveData<ResponseStatus<RecipeResponse>> = MutableLiveData()
    private val _isFavourite: MutableLiveData<ResponseStatus<Boolean>> = MutableLiveData()

    val latestRecipe: LiveData<ResponseStatus<RecipeResponse>> get() = _latestRecipe
    val recipe: LiveData<ResponseStatus<RecipeResponse>> get() = _recipe
    val searchRecipe: LiveData<ResponseStatus<RecipeResponse>> get() = _searchRecipe
    val recipeDetail: LiveData<ResponseStatus<RecipeDetailResponse>> get() = _recipeDetail
    val recipeCategory: LiveData<ResponseStatus<RecipeCategoryResponse>> get() = _recipeCategory
    val recipeByCategory: LiveData<ResponseStatus<RecipeResponse>> get() = _recipeByCategory
    val isFavourite: LiveData<ResponseStatus<Boolean>> get() = _isFavourite

    override fun getLatestRecipe() = viewModelScope.launch {
        _latestRecipe.value = ResponseStatus.Loading
        _latestRecipe.value = repository.getLatestRecipe()
    }

    override fun getRecipeByPage(page: Int) = viewModelScope.launch {
        _recipe.value = ResponseStatus.Loading
        _recipe.value = repository.getRecipeByPage(page)
    }

    override fun searchRecipe(query: String) = viewModelScope.launch {
        _searchRecipe.value = ResponseStatus.Loading
        _searchRecipe.value = repository.searchRecipe(query)
    }

    override fun getRecipeDetail(key: String) = viewModelScope.launch {
        _recipeDetail.value = ResponseStatus.Loading
        _recipeDetail.value = repository.getRecipeDetail(key)
    }

    override fun getCategory() = viewModelScope.launch {
        _recipeCategory.value = ResponseStatus.Loading
        _recipeCategory.value = repository.getCategory()
    }

    override fun getRecipeByCategory(key: String) = viewModelScope.launch {
        _recipeByCategory.value = ResponseStatus.Loading
        _recipeByCategory.value = repository.getRecipeByCategory(key)
    }

    override fun getRecipeFavourite(key: String?) = viewModelScope.launch {
        _recipe.value = ResponseStatus.Loading
        _recipe.value = repository.getRecipeFavourite(key)
    }

    override fun setFavourite(key: String, isFavourite: Boolean) = viewModelScope.launch {
        _isFavourite.value = ResponseStatus.Loading
        _isFavourite.value = repository.setFavourite(key, isFavourite)
    }

    fun insertRecipeDetail(recipeDetail: RecipeDetail) = viewModelScope.launch {
        val db = BaseApplication.recipeDB

        db.apply {
            getRecipeDetailDao().insert(recipeDetail)

            val result = getRecipeDetailDao().all()
            Log.d("TAG", "insertDetail: ${result.map { return@map it.key }}")
        }
    }

}