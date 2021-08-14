package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.19
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleRepository(private val api: IRecipeApi) : BaseRepository {

    suspend fun getLatestArticle(): ResponseStatus<ArticleResponse> = safeApiCall { api.getLatestArticle() }
    suspend fun getArticleCategory(): ResponseStatus<ArticleCategoryResponse> = safeApiCall { api.getArticleCategory() }
    suspend fun getArticleByCategory(key: String): ResponseStatus<ArticleResponse> = safeApiCall { api.getArticleByCategory(key) }
    suspend fun getArticleDetail(key: String): ResponseStatus<ArticleDetailResponse> = safeApiCall { api.getArticleDetail(key) }

}