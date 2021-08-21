package com.training.foodrecipe

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.foodrecipe.adapter.CategoryAdapter
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.adapter.RecipeAdapter
import com.training.foodrecipe.databinding.FragmentSearchBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
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
    private var currentSearchQuery: String = ""

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
        return RecipeRepository(recipeDB, apiClient.crete(IRecipeApi::class.java))
    }

    private fun showInputKey(view: View, show: Boolean) {
        when (show) {
            true -> {
                view.requestFocus().run {
                    val input = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    input.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            false -> {
                view.requestFocus().run {
                    val input = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    input.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
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
            showInputKey(viewBinding.etInputSearch, it is ResponseStatus.Success)

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

                override fun onButtonFavouriteClicked(data: Any) {
                    super.onButtonFavouriteClicked(data)
                    data as Recipe

                    Toast.makeText(requireContext(), "${data.title} Added to favourite!", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonShareClicked(data: Any) {
                    super.onButtonShareClicked(data)
                    data as Recipe

                    Toast.makeText(requireContext(), "${data.title} Shared to social media!", Toast.LENGTH_SHORT).show()
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
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        timerTask?.cancel()
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // TODO: Run search query with s param
                        val q = s.toString().trim()

                        if (q.isNotEmpty()) {
                            ivClearInputSearch.visibility = View.VISIBLE

                            if (currentSearchQuery == q) {
                                timerTask?.cancel()
                                return
                            }

                            timerTask = Timer()
                            timerTask?.schedule(object : TimerTask() {
                                override fun run() {
                                    Handler(Looper.getMainLooper()).post {
                                        clearSearchResult()
                                        showInputKey(this@apply, false)
                                        viewModel.searchRecipe(q)

                                        currentSearchQuery = q
                                    }
                                }
                            }, 1000)
                        } else {
                            currentSearchQuery = ""
                            ivClearInputSearch.visibility = View.GONE
                            clearSearchResult()
                        }
                    }
                })

                /*
                setOnKeyListener { v, keyCode, event ->
                    val query = etInputSearch.editableText.toString()

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        Log.d(TAG, query)

                        // TODO: Run search query with textResult param
                        Handler().postDelayed({
                            viewModel.searchRecipe(query)
                            showSoftKey(v, false)
                        }, 1000)

                        return@setOnKeyListener true
                    }

                    false
                }
                */

                setOnClickListener {
                    showInputKey(it, true)
                }

                requestFocus()
            }

            ivClearInputSearch.setOnClickListener {
                etInputSearch.text = null
                it.visibility = View.GONE
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_search_title)
            layoutHeader.ivHeaderMenu.visible(false)
            layoutHeader.ivHeaderFavourite.visible(false)
            layoutHeader.btnBack.setOnClickListener {
                findNavController().navigateUp()
                showInputKey(it, false)
            }

            srlRefresh.setOnRefreshListener {
                isNetworkError = false
                isLoading = false

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
            viewBinding.layoutSearchResultPlaceholder.visibility = View.VISIBLE
            viewBinding.layoutSearchResult.visibility = View.GONE

            // Category
            viewBinding.layoutCategoryPlaceholder.visibility = View.VISIBLE
            viewBinding.layoutCategory.visibility = View.GONE
        } else {
            // Search result
            viewBinding.shimmerFramelayout.stopShimmer()
            viewBinding.shimmerFramelayout.hideShimmer()

            viewBinding.layoutSearchResultPlaceholder.visibility = View.GONE
            viewBinding.layoutSearchResult.visibility = View.VISIBLE

            // Category
            viewBinding.shimmerCategoryFramelayout.stopShimmer()
            viewBinding.shimmerCategoryFramelayout.hideShimmer()

            viewBinding.layoutCategoryPlaceholder.visibility = View.GONE
            viewBinding.layoutCategory.visibility = View.VISIBLE
        }
    }

    private fun fetchData() {
        viewModel.getCategory()
    }

    private fun clearSearchResult() {
        recipeAdapter.clearData()
    }

}