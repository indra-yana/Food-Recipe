package com.training.foodrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.adapter.RecipeAdapter
import com.training.foodrecipe.databinding.FragmentFavouriteBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel

class FavouriteFragment : BaseFragment<FragmentFavouriteBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = FavouriteFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var recipeAdapter: RecipeAdapter

    // Indicator state
    private var isLoading = false

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

        fetchData()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository(recipeDB, apiClient.crete(IRecipeApi::class.java))
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
                    handleRequestError(it) { fetchData() }

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
            layoutSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }

            srlRefresh.setOnRefreshListener {
                isLoading = false

                fetchData()
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_favorite)
            layoutHeader.ivHeaderFavourite.visible(false)
            layoutHeader.ivHeaderMenu.visible(false)
            layoutHeader.btnBack.visible(true)
            layoutHeader.btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerRecipeContainer.showShimmer(isLoading)

        if (isLoading) {
            // Recipe
            viewBinding.shimmerRecipePlaceholder.visibility = View.VISIBLE
            viewBinding.layoutRecipeContainer.visibility = View.GONE
        } else {
            // Recipe
            viewBinding.shimmerRecipeContainer.stopShimmer()
            viewBinding.shimmerRecipeContainer.hideShimmer()

            viewBinding.shimmerRecipePlaceholder.visibility = View.GONE
            viewBinding.layoutRecipeContainer.visibility = View.VISIBLE
        }
    }

    private fun gotoDetail(data: Any) {
        data as Recipe
        val bundle = Bundle().apply {
            putParcelable("recipe", data)
        }

        findNavController().navigate(R.id.action_favouriteFragment2_to_recipeDetailFragment, bundle)
    }

    private fun fetchData() {
        viewModel.getRecipeFavourite()
    }
}