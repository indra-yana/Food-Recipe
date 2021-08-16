package com.training.foodrecipe.viewmodel.base

import kotlinx.coroutines.Job

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.00
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseArticleViewModel : BaseViewModel() {

    abstract fun getLatestArticle(): Job
    abstract fun getArticleCategory(): Job
    abstract fun getArticleByCategory(key: String?): Job
    abstract fun getArticleDetail(tag: String, key: String): Job

}