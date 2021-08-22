package com.training.foodrecipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.databinding.FragmentArticleDetailBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.enable
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.model.Article
import com.training.foodrecipe.model.ArticleDetail
import com.training.foodrecipe.repository.ArticleRepository
import com.training.foodrecipe.viewmodel.ArticleViewModel

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 16/08/2021 22.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleDetailFragment : BaseFragment<FragmentArticleDetailBinding, ArticleViewModel, ArticleRepository>() {

    companion object {
        private val TAG = ArticleDetailFragment::class.java.simpleName
    }

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false

    // Data
    private lateinit var article: Article

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        article = arguments?.getParcelable("article")!!

        fetchData()
    }

    /**
     * Init all variable here that consume view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect to activity
        (activity as MainActivity).apply {
            hideFabAction()
            hideBottomNavigation()
        }

        prepareUI()
        observeArticle()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentArticleDetailBinding {
        return FragmentArticleDetailBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<ArticleViewModel> {
        return ArticleViewModel::class.java
    }

    override fun getRepository(): ArticleRepository {
        return ArticleRepository(recipeDB, apiClient.crete(IRecipeApi::class.java))
    }

    private fun observeArticle() {
        viewModel.articleDetail.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "observeArticle: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.articleDetail
                    updateUI(item)

                    Log.d(TAG, "observeArticle: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Log.d(TAG, "observeArticle:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "observeArticle: State is unknown!")
                }
            }
        })
    }

    private fun toggleLoading(isLoading: Boolean) {
        with(viewBinding) {
            srlRefresh.isRefreshing = isLoading
            shimmerArticleContainer.showShimmer(isLoading || isNetworkError)

            if (isLoading || isNetworkError) {
                layoutArticleDetailPlaceholder.visible(true)
                layoutArticleDetailContainer.visible(false)
            } else {
                shimmerArticleContainer.stopShimmer()
                shimmerArticleContainer.hideShimmer()

                layoutArticleDetailPlaceholder.visible(false)
                layoutArticleDetailContainer.visible(true)
            }

            btnGotoWebsite.enable(!isLoading)
        }
    }

    private fun prepareUI() {
        with(viewBinding) {
            srlRefresh.setOnRefreshListener {
                fetchData()
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnGotoWebsite.setOnClickListener {
                gotoWebsite(article.url)
            }
        }
    }

    private fun updateUI(item: ArticleDetail) {
        with(viewBinding) {
            with(item) {
                Glide.with(requireView().context)
                    .load(thumb)
                    .apply(RequestOptions().override(450, 250))
                    .into(ivItemThumbnail)

                tvAuthor.text = ("By $author | $datePublished")
                tvItemTitle.text = title
                tvItemDescription.text = description

                tvReadMore.setOnClickListener {
                    if (tvReadMore.text.toString() == "Read More") {
                        tvItemDescription.maxLines = Int.MAX_VALUE
                        tvItemDescription.ellipsize = null
                        tvReadMore.text = getString(R.string.text_read_less)
                    } else {
                        tvItemDescription.maxLines = 4
                        tvItemDescription.ellipsize = TextUtils.TruncateAt.END
                        tvReadMore.text = getString(R.string.text_read_more)
                    }
                }
            }
        }
    }

    private fun gotoWebsite(url: String?) {
        if (url == null) {
            Toast.makeText(requireContext(), "N/A", Toast.LENGTH_SHORT).show()
            return
        }

        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            startActivity(this)
        }
    }

    private fun fetchData() {
        viewModel.getArticleDetail(article.tags ?: "", article.key)
    }
}