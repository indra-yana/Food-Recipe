package com.training.foodrecipe.view.fragment

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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.view.adapter.RecipeAdapter
import com.training.foodrecipe.databinding.FragmentFavouriteBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.*
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel
import java.util.*

class FavouriteFragment : BaseFragment<FragmentFavouriteBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = FavouriteFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var recipeAdapter: RecipeAdapter

    // Indicator state
    private var isLoading = false
    private var currentSearchQuery: String? = null

    private var timerTask: Timer? = null

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildRecipeAdapter()
        // fetchData()
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

        // Recipe
        buildRecipeRV()
        observeRecipeFavourite()

        fetchData(currentSearchQuery)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository()
    }

    private fun buildRecipeAdapter() {
        recipeAdapter = RecipeAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    gotoDetail(data)
                }
            }
        }
    }

    private fun buildRecipeRV() {
        with(viewBinding) {
            rvRecipe.setHasFixedSize(true)
            viewBinding.rvRecipe.adapter = recipeAdapter
            viewBinding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeRecipeFavourite() {
        viewModel.recipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "observeRecipeFavourite: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeAdapter.clearData()
                    recipeAdapter.bindData(it.value.recipes)

                    Log.d(TAG, "observeRecipeFavourite: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData(currentSearchQuery) }

                    Log.d(TAG, "observeRecipeFavourite:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "observeRecipeFavourite: State is unknown!")
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

                            fetchData(currentSearchQuery)
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
                    showInputKey(etInputSearch, true)
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

            srlRefresh.setOnRefreshListener {
                isLoading = false
                currentSearchQuery = null
                etInputSearch.text = null

                fetchData(currentSearchQuery)
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_favorite)
            layoutHeader.ivHeaderCreate.visible(true)
            layoutHeader.ivHeaderMenu.visible(false)
            layoutHeader.btnBack.visible(true)
            layoutHeader.btnBack.setOnClickListener {
                showInputKey(etInputSearch, false)
                findNavController().navigateUp()
            }

            layoutHeader.ivHeaderCreate.setOnClickListener {
                // TODO: Create own recipe
                requireView().shortSnackBar("Buat resepmu sendiri!")
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerRecipeContainer.showShimmer(isLoading)

        if (isLoading) {
            // Recipe
            viewBinding.shimmerRecipePlaceholder.visible(true)
            viewBinding.layoutRecipeContainer.visible(false)
        } else {
            // Recipe
            viewBinding.shimmerRecipeContainer.stopShimmer()
            viewBinding.shimmerRecipeContainer.hideShimmer()

            viewBinding.shimmerRecipePlaceholder.visible(false)
            viewBinding.layoutRecipeContainer.visible(true)
        }
    }

    private fun gotoDetail(data: Any) {
        data as Recipe
        val bundle = Bundle().apply {
            putParcelable("recipe", data)
        }

        findNavController().navigate(R.id.action_favouriteFragment2_to_recipeDetailFragment, bundle)
    }

    private fun doSearch(q: String?) {
        if (!q.isNullOrEmpty()) {
            if (currentSearchQuery == q) return

            currentSearchQuery = q
            showInputKey(viewBinding.etInputSearch, false)
            fetchData(currentSearchQuery)
        }
    }

    private fun fetchData(key: String?) {
        viewModel.getRecipeFavourite(key)
    }
}