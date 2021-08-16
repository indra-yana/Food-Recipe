package com.training.foodrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.training.foodrecipe.datasource.remote.response.ArticleCategoryResponse
import com.training.foodrecipe.datasource.remote.response.ArticleDetailResponse
import com.training.foodrecipe.datasource.remote.response.ArticleResponse
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.viewmodel.base.BaseArticleViewModel
import kotlinx.coroutines.launch

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.06
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleViewModel(private val repository: ArticleRepository) : BaseArticleViewModel() {

    private val _latestArticle: MutableLiveData<ResponseStatus<ArticleResponse>> = MutableLiveData()
    private val _articleCategory: MutableLiveData<ResponseStatus<ArticleCategoryResponse>> = MutableLiveData()
    private val _articleDetail: MutableLiveData<ResponseStatus<ArticleDetailResponse>> = MutableLiveData()

    val latestArticle: LiveData<ResponseStatus<ArticleResponse>> get() = _latestArticle
    val recipeCategory: LiveData<ResponseStatus<ArticleCategoryResponse>> get() = _articleCategory
    val articleDetail: LiveData<ResponseStatus<ArticleDetailResponse>> get() = _articleDetail

    override fun getLatestArticle() = viewModelScope.launch {
        _latestArticle.value = ResponseStatus.Loading
        _latestArticle.value = repository.getLatestArticle()
    }

    override fun getArticleCategory() = viewModelScope.launch {
        _articleCategory.value = ResponseStatus.Loading
        _articleCategory.value = repository.getArticleCategory()
    }

    override fun getArticleByCategory(key: String?) = viewModelScope.launch {
        if (key == null) {
            getLatestArticle()
        } else {
            _latestArticle.value = ResponseStatus.Loading
            _latestArticle.value = repository.getArticleByCategory(key)
        }
    }

    override fun getArticleDetail(tag: String, key: String) = viewModelScope.launch {
        _articleDetail.value = ResponseStatus.Loading
        _articleDetail.value = repository.getArticleDetail(tag, key)
    }

}