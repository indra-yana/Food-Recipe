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
            val dao = db.getArticleCategoryDao()
            val cache = dao.all()

            if (!cache.isNullOrEmpty()) {
                ModelMapper.articleCategoryMapper(cache)
            } else {
                val apiResult = api.getArticleCategory()
                dao.insert(apiResult.articleCategories)

                apiResult
            }
        }
    }

    suspend fun getArticleByCategory(key: String): ResponseStatus<ArticleResponse> = safeApiCall { api.getArticleByCategory(key) }

    suspend fun getArticleDetail(tag: String, key: String): ResponseStatus<ArticleDetailResponse> {
        return safeApiCall {
            val dao = db.getArticleDetailDao()
            val cache = dao.find(key)

            if (cache != null) {
                ModelMapper.articleDetailMapper(cache)
            } else {
                val apiResult = api.getArticleDetail(tag, key)
                dao.insert(apiResult.articleDetail.copy(key = key))

                apiResult
            }
        }
    }

}