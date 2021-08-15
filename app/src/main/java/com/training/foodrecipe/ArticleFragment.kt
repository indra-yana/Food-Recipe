package com.training.foodrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.foodrecipe.adapter.ArticleAdapter
import com.training.foodrecipe.adapter.CategoryAdapter
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.databinding.FragmentArticleBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.model.Article
import com.training.foodrecipe.model.ArticleCategory
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.viewmodel.ArticleViewModel

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/08/2021 13.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleFragment : BaseFragment<FragmentArticleBinding, ArticleViewModel, ArticleRepository>() {

    companion object {
        private val TAG = ArticleFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var articleAdapter: ArticleAdapter

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false

    // Last selected category
    private var key: String? = null

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getArticleCategory()
        viewModel.getArticleByCategory(key)
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

        // Category
        buildCategoryAdapter()
        buildCategoryRecyclerView()
        getArticleCategory()

        // Article
        buildArticleAdapter()
        buildArticleRecyclerView()
        getArticle()

        with(viewBinding) {
            srlRefresh.setOnRefreshListener {
                isNetworkError = false
                isLoading = false

                viewModel.getArticleCategory()
                viewModel.getArticleByCategory(key)
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_article_title)
        }
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

    private fun buildCategoryAdapter() {
        categoryAdapter = CategoryAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    data as ArticleCategory

                    if (key != data.key) {
                        key = data.key
                        viewModel.getArticleByCategory(data.key)
                    }
                }
            }
        }
    }

    private fun buildCategoryRecyclerView() {
        with(viewBinding) {
            rvCategory.setHasFixedSize(true)
            rvCategory.adapter = categoryAdapter
            rvCategory.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
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
                    categoryAdapter.bindData(item)

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

    private fun buildArticleAdapter() {
        articleAdapter = ArticleAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    data as Article
                    val bundle = Bundle().apply {
                        putParcelable("article", data)
                    }

                    // TODO: Goto article detail fragment
                    // findNavController().navigate(R.id.action_searchFragment_to_recipeDetailFragment, bundle)
                }
            }
        }
    }

    private fun buildArticleRecyclerView() {
        with(viewBinding) {
            rvArticle.setHasFixedSize(true)
            rvArticle.adapter = articleAdapter
            rvArticle.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getArticle() {
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
                    articleAdapter.bindData(item)

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
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerArticleContainer.showShimmer(isLoading || isNetworkError)
        viewBinding.shimmerCategoryContainer.showShimmer(isLoading || isNetworkError)

        if (isLoading || isNetworkError) {
            // Article
            viewBinding.layoutArticlePlaceholder.visibility = View.VISIBLE
            viewBinding.layoutArticleResult.visibility = View.GONE

            // Category
            viewBinding.layoutCategoryPlaceholder.visibility = View.VISIBLE
            viewBinding.layoutCategory.visibility = View.GONE
        } else {
            // Article
            viewBinding.shimmerArticleContainer.stopShimmer()
            viewBinding.shimmerArticleContainer.hideShimmer()

            viewBinding.layoutArticlePlaceholder.visibility = View.GONE
            viewBinding.layoutArticleResult.visibility = View.VISIBLE

            // Category
            viewBinding.shimmerCategoryContainer.stopShimmer()
            viewBinding.shimmerCategoryContainer.hideShimmer()

            viewBinding.layoutCategoryPlaceholder.visibility = View.GONE
            viewBinding.layoutCategory.visibility = View.VISIBLE
        }
    }

    private fun retry() {
        viewModel.getArticleCategory()
        viewModel.getArticleByCategory(key)
    }

}