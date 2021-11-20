package com.training.foodrecipe.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.foodrecipe.R
import com.training.foodrecipe.databinding.FragmentFavouriteBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.showInputKey
import com.training.foodrecipe.helper.snackBar
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.view.adapter.RecipeAdapter
import com.training.foodrecipe.view.fragment.base.BaseFragment
import com.training.foodrecipe.viewmodel.RecipeViewModel
import timber.log.Timber
import java.util.*

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 25/08/2021 13.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class FavouriteFragment : BaseFragment<FragmentFavouriteBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = this::class.java.simpleName
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

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentFavouriteBinding.inflate(inflater, container, false)
    override fun getViewModel() = RecipeViewModel::class.java
    override fun getRepository() = RecipeRepository()

    private fun buildRecipeAdapter() {
        recipeAdapter = RecipeAdapter().apply {
            enableRemove = true
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any, position: Int) {
                    gotoDetail(data)
                }

                override fun onToggleDeleteItemClicked(data: Any, position: Int) {
                    super.onToggleDeleteItemClicked(data, position)
                    data as Recipe

                    recipeAdapter.onItemRemoved(position) {
                        viewModel.setFavourite(it.key, false)
                        requireView().snackBar("${it.title.substring(0, 16)}... removed from favourite!")
                    }
                }
            }
        }
    }

    private fun buildRecipeRV() = with(viewBinding) {
        rvRecipe.setHasFixedSize(true)
        viewBinding.rvRecipe.adapter = recipeAdapter
        viewBinding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeRecipeFavourite() {
        viewModel.recipe.observe(viewLifecycleOwner, {
            isLoading = it is ResponseStatus.Loading

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeRecipeFavourite: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeAdapter.clearData()
                    recipeAdapter.bindData(it.value.recipes)

                    Timber.d(TAG, "observeRecipeFavourite: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData(currentSearchQuery) }

                    Timber.d(TAG, "observeRecipeFavourite:State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeRecipeFavourite: State is unknown!")
                }
            }
        })
    }

    override fun prepareUI() = with(viewBinding) {
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
            requireView().snackBar("Buat resepmu sendiri!")
        }
    }

    override fun toggleLoading(isLoading: Boolean) {
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
        val bundle = bundleOf("recipe" to data)

        findNavController().navigate(R.id.action_favouriteFragment_to_recipeDetailFragment, bundle)
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