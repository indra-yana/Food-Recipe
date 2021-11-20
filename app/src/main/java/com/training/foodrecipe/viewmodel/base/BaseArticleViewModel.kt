package com.training.foodrecipe.viewmodel.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 20.00
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseArticleViewModel : ViewModel() {

    abstract fun getLatestArticle(): Job
    abstract fun getArticleCategory(): Job
    abstract fun getArticleByCategory(key: String?): Job
    abstract fun getArticleDetail(tag: String, key: String): Job

}