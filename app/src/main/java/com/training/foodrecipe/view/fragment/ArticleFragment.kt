package com.training.foodrecipe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.R
import com.training.foodrecipe.view.adapter.ArticleAdapter
import com.training.foodrecipe.view.adapter.CategoryAdapter
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.databinding.FragmentArticleBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.listener.IOnFabClickListener
import com.training.foodrecipe.model.Article
import com.training.foodrecipe.model.ArticleCategory
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.viewmodel.ArticleViewModel
import timber.log.Timber

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

        fetchData()
    }

    /**
     * Init all variable here that consume view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect to activity
        (activity as MainActivity).apply {
            showFabAction()
            showBottomNavigation()
            iOnFabClickListener = object : IOnFabClickListener {
                override fun onFabClicked(view: View) {
                    findNavController().navigate(R.id.action_articleFragment_to_nav_favourite)
                }
            }
        }

        prepareUI()

        // Category
        buildCategoryAdapter()
        buildCategoryRV()
        observeArticleCategory()

        // Article
        buildArticleAdapter()
        buildArticleRV()
        observeArticle()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentArticleBinding {
        return FragmentArticleBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<ArticleViewModel> {
        return ArticleViewModel::class.java
    }

    override fun getRepository(): ArticleRepository {
        return ArticleRepository()
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

    private fun buildCategoryRV() {
        with(viewBinding) {
            rvCategory.setHasFixedSize(true)
            rvCategory.adapter = categoryAdapter
            rvCategory.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
    }

    private fun observeArticleCategory() {
        viewModel.articleCategory.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeArticleCategory: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.articleCategories
                    categoryAdapter.bindData(item)

                    Timber.d(TAG, "observeArticleCategory: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Timber.d(TAG, "observeArticleCategory:State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeArticleCategory: State is unknown!")
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

                    findNavController().navigate(R.id.action_articleFragment_to_articleDetailFragment, bundle)
                }
            }
        }
    }

    private fun buildArticleRV() {
        with(viewBinding) {
            rvArticle.setHasFixedSize(true)
            rvArticle.adapter = articleAdapter
            rvArticle.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeArticle() {
        viewModel.latestArticle.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeArticle: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.articles
                    articleAdapter.bindData(item)

                    Timber.d(TAG, "observeArticle: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Timber.d(TAG, "observeArticle:State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeArticle: State is unknown!")
                }
            }
        })
    }

    private fun prepareUI() {
        with(viewBinding) {
            srlRefresh.setOnRefreshListener {
                isNetworkError = false
                isLoading = false

                fetchData()
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_article_title)
            layoutHeader.btnBack.visible(false)
            layoutHeader.ivHeaderCreate.visible(false)
            layoutHeader.ivHeaderMenu.visible(false)
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerArticleContainer.showShimmer(isLoading || isNetworkError)
        viewBinding.shimmerCategoryContainer.showShimmer(isLoading || isNetworkError)

        if (isLoading || isNetworkError) {
            // Article
            viewBinding.layoutArticlePlaceholder.visible(true)
            viewBinding.layoutArticleResult.visible(false)

            // Category
            viewBinding.layoutCategoryPlaceholder.visible(true)
            viewBinding.layoutCategory.visible(false)
        } else {
            // Article
            viewBinding.shimmerArticleContainer.stopShimmer()
            viewBinding.shimmerArticleContainer.hideShimmer()

            viewBinding.layoutArticlePlaceholder.visible(false)
            viewBinding.layoutArticleResult.visible(true)

            // Category
            viewBinding.shimmerCategoryContainer.stopShimmer()
            viewBinding.shimmerCategoryContainer.hideShimmer()

            viewBinding.layoutCategoryPlaceholder.visible(false)
            viewBinding.layoutCategory.visible(true)
        }
    }

    private fun fetchData() {
        viewModel.getArticleCategory()
        viewModel.getArticleByCategory(key)
    }

}