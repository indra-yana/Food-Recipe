package com.training.foodrecipe.datasource.local.dao

import androidx.room.*
import com.training.foodrecipe.model.ArticleDetail

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Tuesday, 06/04/2021 21.23
 * https://gitlab.com/indra-yana
 ****************************************************/

@Dao
interface ArticleDetailDao {

    @Query("SELECT * FROM article_details WHERE `key` = :key")
    suspend fun find(key: String): ArticleDetail?

    @Query("SELECT * FROM article_details")
    suspend fun all(): List<ArticleDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: ArticleDetail)

    @Delete
    suspend fun delete(value: ArticleDetail)

    @Query("SELECT EXISTS (SELECT 1 FROM article_details WHERE `key` = :key)")
    suspend fun exist(key: String): Boolean
}