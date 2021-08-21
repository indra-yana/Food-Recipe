package com.training.foodrecipe.repository

import com.training.foodrecipe.datasource.local.RecipeDatabase
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.*
import com.training.foodrecipe.helper.ModelMapper

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.19
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleRepository(private val db: RecipeDatabase, private val api: IRecipeApi) : BaseRepository() {

    suspend fun getLatestArticle(): ResponseStatus<ArticleResponse> = safeApiCall { api.getLatestArticle() }

    suspend fun getArticleCategory(): ResponseStatus<ArticleCategoryResponse> {
        return safeApiCall {
            val cache = db.getArticleCategoryDao().all()

            if (!cache.isNullOrEmpty()) {
                ModelMapper.articleCategoryMapper(cache)
            } else {
                val apiResult = api.getArticleCategory()
                db.getArticleCategoryDao().insert(apiResult.articleCategories)

                apiResult
            }
        }
    }

    suspend fun getArticleByCategory(key: String): ResponseStatus<ArticleResponse> = safeApiCall { api.getArticleByCategory(key) }

    suspend fun getArticleDetail(tag: String, key: String): ResponseStatus<ArticleDetailResponse> {
        return safeApiCall {
            val cache = db.getArticleDetailDao().find(key)

            if (cache != null) {
                ModelMapper.articleDetailMapper(cache)
            } else {
                val apiResult = api.getArticleDetail(tag, key)
                db.getArticleDetailDao().insert(apiResult.articleDetail.copy(key = key))

                apiResult
            }
        }
    }

}