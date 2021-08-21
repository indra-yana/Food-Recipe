package com.training.foodrecipe.datasource.local.dao

import androidx.room.*
import com.training.foodrecipe.model.ArticleCategory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 20/08/2021 20.41
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface ArticleCategoryDao {

    @Query("SELECT * FROM article_categories ORDER BY `key` ASC")
    suspend fun all(): List<ArticleCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: ArticleCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: List<ArticleCategory>)

    @Delete
    suspend fun delete(value: ArticleCategory)

    @Query("DELETE FROM article_categories")
    suspend fun delete()
}