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
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.model.RecipeCategory
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel
import java.util.*

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

    private var currentQuery: Editable? = null

    private var timerTask: Timer? = null

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getCategory()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Connect to activity
        (activity as MainActivity).apply {
            hideFabAction()
            hideBottomNavigation()
        }

        buildCategoryAdapter()
        buildCategoryRecyclerView()
        getCategory()

        buildRecipeAdapter()
        buildRecipeRecyclerView()
        getSearchRecipe()

        with(viewBinding) {
            currentQuery?.let {
                // TODO: Set the current query on edit text
            }

            etInputSearch.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        timerTask?.cancel()
                    }
                    override fun afterTextChanged(s: Editable?) {
                        // TODO: Run search query with s param
                        if (s.toString().trim().isNotEmpty()) {
                            timerTask = Timer()
                            timerTask?.schedule(object : TimerTask() {
                                override fun run() {
                                    Handler(Looper.getMainLooper()).post {
                                        viewModel.searchRecipe(s.toString().trim())
                                        showSoftKey(this@apply, false)
                                    }
                                }
                            }, 800)
                        } else {
                            recipeAdapter.clearData()
                        }

                        ivClearInputSearch.visibility = if (s.toString().trim().isEmpty()) View.GONE else View.VISIBLE
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

                requestFocus()
            }

            ivClearInputSearch.setOnClickListener {
                etInputSearch.text = null
                it.visibility = View.GONE
            }

            layoutHeader.btnBack.setOnClickListener {
                findNavController().navigateUp()
                showSoftKey(it, false)
            }

            srlRefresh.setOnRefreshListener {
                isNetworkError = false
                isLoading = false

                viewModel.getCategory()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository(apiClient.crete(IRecipeApi::class.java))
    }

    private fun showSoftKey(view: View, show: Boolean) {
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

                    Toast.makeText(requireContext(), data.category, Toast.LENGTH_SHORT).show()
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

    private fun getCategory() {
        viewModel.recipeCategory.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)
            showSoftKey(viewBinding.etInputSearch, !isNetworkError)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getRecipeDetail: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.recipeCategories
                    categoryAdapter.bindData(item)

                    Log.d(TAG, "getRecipeDetail: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { retry() }

                    Log.d(TAG, "getRecipeDetail:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getRecipeDetail: State is unknown!")
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
                        putString("args", "This is my args!")
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

    private fun buildRecipeRecyclerView() {
        with(viewBinding) {
            rvRecipe.setHasFixedSize(true)
            rvRecipe.adapter = recipeAdapter
            rvRecipe.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getSearchRecipe() {
        viewModel.searchRecipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getRecipeByPage: State is loading!")
                }
                is ResponseStatus.Success -> {
                    val item = it.value.recipes
                    recipeAdapter.bindData(item)

                    Log.d(TAG, "getRecipeByPage: State is success! $item")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { retry() }

                    Log.d(TAG, "getRecipeByPage:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getRecipeByPage: State is unknown!")
                }
            }
        })
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading

        if (isLoading || isNetworkError) {
            viewBinding.mainRecipeContainer.visibility = View.GONE
        } else {
            viewBinding.mainRecipeContainer.visibility = View.VISIBLE
        }
    }

    private fun retry() {
        viewModel.getCategory()
    }

}