package com.training.foodrecipe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.foodrecipe.adapter.CategoryAdapter
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.adapter.RecipeAdapter
import com.training.foodrecipe.databinding.FragmentSearchBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.showInputKey
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.model.RecipeCategory
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel
import java.util.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 10/08/2021 22.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class SearchFragment : BaseFragment<FragmentSearchBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var recipeAdapter: RecipeAdapter

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false
    private var currentSearchQuery: String? = null

    private var timerTask: Timer? = null

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect to activity
        (activity as MainActivity).apply {
            hideFabAction()
            hideBottomNavigation()
        }

        prepareUI()

        // Category
        buildCategoryAdapter()
        buildCategoryRV()
        observeCategory()

        // Recipe
        buildRecipeAdapter()
        buildRecipeRV()
        observeSearchRecipe()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository()
    }

    private fun buildCategoryAdapter() {
        categoryAdapter = CategoryAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    data as RecipeCategory

                    with(viewBinding.etInputSearch) {
                        setText(data.key)
                        setSelection(data.key.length)
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

    private fun observeCategory() {
        viewModel.recipeCategory.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)
            showInputKey(viewBinding.etInputSearch, it is ResponseStatus.Success && viewBinding.etInputSearch.text.trim().isEmpty())

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "observeCategory: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.recipeCategories
                    categoryAdapter.bindData(item)

                    Log.d(TAG, "observeCategory: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Log.d(TAG, "observeCategory:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "observeCategory: State is unknown!")
                }
            }
        })
    }

    private fun buildRecipeAdapter() {
        recipeAdapter = RecipeAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    data as Recipe
                    val bundle = Bundle().apply {
                        putParcelable("recipe", data)
                    }

                    findNavController().navigate(R.id.action_searchFragment_to_recipeDetailFragment, bundle)
                }
            }
        }
    }

    private fun buildRecipeRV() {
        with(viewBinding) {
            rvRecipe.setHasFixedSize(true)
            rvRecipe.adapter = recipeAdapter
            rvRecipe.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeSearchRecipe() {
        viewModel.searchRecipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "observeSearchRecipe: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.recipes
                    recipeAdapter.bindData(item)

                    Log.d(TAG, "observeSearchRecipe: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Log.d(TAG, "observeSearchRecipe:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "observeSearchRecipe: State is unknown!")
                }
            }
        })
    }

    private fun prepareUI() {
        with(viewBinding) {
            etInputSearch.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { timerTask?.cancel() }
                    override fun afterTextChanged(s: Editable?) {
                        val q = s.toString().trim()

                        if (q.isNotEmpty()) {
                            ivClearInputSearch.visible(true)

                            if (currentSearchQuery == q) {
                                timerTask?.cancel()
                                return
                            }

                            timerTask = Timer()
                            timerTask?.schedule(object : TimerTask() {
                                override fun run() {
                                    Handler(Looper.getMainLooper()).post {
                                        doSearch(q)
                                    }
                                }
                            }, 2000)
                        } else {
                            currentSearchQuery = null
                            ivClearInputSearch.visible(false)
                            clearSearchResult()
                        }
                    }
                })

                setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        val q = editableText.trim().toString()

                        timerTask?.cancel()
                        doSearch(q)
                        return@setOnKeyListener true
                    }

                    return@setOnKeyListener false
                }

                setOnClickListener {
                    showInputKey(it, true)
                }

                currentSearchQuery?.let {
                    setText(it)
                    setSelection(it.length)
                }
            }

            ivClearInputSearch.setOnClickListener {
                etInputSearch.text = null
                it.visible(false)
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_search_title)
            layoutHeader.ivHeaderMenu.visible(false)
            layoutHeader.ivHeaderCreate.visible(false)
            layoutHeader.btnBack.setOnClickListener {
                showInputKey(etInputSearch, false)
                findNavController().navigateUp()
            }

            srlRefresh.setOnRefreshListener {
                isNetworkError = false
                isLoading = false
                currentSearchQuery = null
                etInputSearch.text = null

                fetchData()
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerFramelayout.showShimmer(isLoading || isNetworkError)
        viewBinding.shimmerCategoryFramelayout.showShimmer(isLoading || isNetworkError)

        if (isLoading || isNetworkError) {
            // Search result
            viewBinding.layoutSearchResultPlaceholder.visible(true)
            viewBinding.layoutSearchResult.visible(false)

            // Category
            viewBinding.layoutCategoryPlaceholder.visible(true)
            viewBinding.layoutCategory.visible(false)
        } else {
            // Search result
            viewBinding.shimmerFramelayout.stopShimmer()
            viewBinding.shimmerFramelayout.hideShimmer()

            viewBinding.layoutSearchResultPlaceholder.visible(false)
            viewBinding.layoutSearchResult.visible(true)

            // Category
            viewBinding.shimmerCategoryFramelayout.stopShimmer()
            viewBinding.shimmerCategoryFramelayout.hideShimmer()

            viewBinding.layoutCategoryPlaceholder.visible(false)
            viewBinding.layoutCategory.visible(true)
        }
    }

    private fun fetchData() {
        viewModel.getCategory()
    }

    private fun doSearch(q: String?) {
        if (!q.isNullOrEmpty()) {
            if (currentSearchQuery == q) return

            currentSearchQuery = q
            clearSearchResult()
            showInputKey(viewBinding.etInputSearch, false)
            viewModel.searchRecipe(currentSearchQuery)
        }
    }

    private fun clearSearchResult() {
        recipeAdapter.clearData()
    }

}