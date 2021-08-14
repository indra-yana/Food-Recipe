package com.training.foodrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.training.foodrecipe.databinding.FragmentArticleBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.viewmodel.ArticleViewModel


class ArticleFragment : BaseFragment<FragmentArticleBinding, ArticleViewModel, ArticleRepository>() {

    companion object {
        private val TAG = ArticleFragment::class.java.simpleName
    }

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getArticleCategory()
        viewModel.getLatestArticle()
    }

    /**
     * Init all variable here that consume view
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Connect to activity
        (activity as MainActivity).apply {
            hideFabAction()
        }

        getArticleCategory()
        getLatestArticle()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentArticleBinding {
        return FragmentArticleBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<ArticleViewModel> {
        return ArticleViewModel::class.java
    }

    override fun getRepository(): ArticleRepository {
        return ArticleRepository(apiClient.crete(IRecipeApi::class.java))
    }

    private fun getArticleCategory() {
        viewModel.recipeCategory.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getArticleCategory: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.articleCategories

                    Log.d(TAG, "getArticleCategory: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { retry() }

                    Log.d(TAG, "getArticleCategory:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getArticleCategory: State is unknown!")
                }
            }
        })
    }

    private fun getLatestArticle() {
        viewModel.latestArticle.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getLatestArticle: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.articles

                    Log.d(TAG, "getLatestArticle: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { retry() }

                    Log.d(TAG, "getLatestArticle:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getLatestArticle: State is unknown!")
                }
            }
        })
    }

    private fun toggleLoading(isLoading: Boolean) {
        // TODO: Not implemented yet
    }

    private fun retry() {
        viewModel.getArticleCategory()
        viewModel.getLatestArticle()
    }

}