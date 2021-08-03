package com.training.foodrecipe

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.training.foodrecipe.adapter.BannerAdapter
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.adapter.RecipeAdapter
import com.training.foodrecipe.adapter.ViewHolderType
import com.training.foodrecipe.databinding.FragmentRecipeBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.OverlapSliderTransformation
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 22.02
 * https://gitlab.com/indra-yana
 ****************************************************/
class RecipeFragment : BaseFragment<FragmentRecipeBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = RecipeFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private var viewHolderType: ViewHolderType = ViewHolderType.LIST

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false

    // Api paging
    private val initialPage = 1
    private var nextPage = initialPage

    private var lastVisibleItem: Int = RecyclerView.NO_POSITION

    // Slider banner
    private val bannerInterval: Long = 5000
    private val bannerHandler = Handler()
    private lateinit var bannerRunnable: Runnable

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildRecipeAdapter()
        buildBannerAdapter()

        viewModel.getLatestRecipe()
        viewModel.getRecipeByPage(initialPage)
    }

    /**
     * Init all variable here that consume view
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buildRecyclerView()
        buildBanner()

        getLatestRecipe()
        getRecipeByPage()

        toggleRetry()

        with(viewBinding) {
            etInputSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // TODO: Run search query with s param

                    ivClearInputSearch.visibility = if (s.toString().trim().isEmpty()) View.GONE else View.VISIBLE
                }
            })

            ivClearInputSearch.setOnClickListener {
                etInputSearch.text = null
                it.visibility = View.GONE
            }

            ivMenu.setOnClickListener {
                showPopupMenu(it)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        bannerHandler.postDelayed(bannerRunnable, bannerInterval)
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacks(bannerRunnable)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRecipeBinding {
        return FragmentRecipeBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository(apiClient.crete(IRecipeApi::class.java))
    }

    private fun buildRecipeAdapter() {
        recipeAdapter = RecipeAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Recipe) {
                    Toast.makeText(requireContext(), "${data.title} Item clicked!", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonFavouriteClicked(data: Recipe) {
                    super.onButtonFavouriteClicked(data)
                    Toast.makeText(requireContext(), "${data.title} Added to favourite!", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonShareClicked(data: Recipe) {
                    super.onButtonShareClicked(data)
                    Toast.makeText(requireContext(), "${data.title} Shared to social media!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildRecyclerView() {
        with(viewBinding) {

            setListMode()

            rvRecipe.setHasFixedSize(true)
            rvRecipe.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (visibleItem > -1) {
                        lastVisibleItem = visibleItem
//                        Log.d(TAG, "onScrolled: visibleItemAnchorPoint: $visibleItemAnchorPoint")
                    }

                    // Scroll bottom
                    if (!recyclerView.canScrollVertically(1)) {
                        if (!isLoading && !isNetworkError) {
                            viewModel.getRecipeByPage(nextPage)

//                            Log.d(TAG, "onScrolled: nextPage: $nextPage")
                        }
                    }

                    // Scroll up
                    if (recyclerView.canScrollVertically(-1)) {
                        isNetworkError = false
                        toggleNetworkError(isNetworkError)
                    }
                }
            })
        }
    }

    private fun getRecipeByPage() {
//        viewModel.getRecipeByPage(initialPage)
        viewModel.recipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)
            toggleNetworkError(isNetworkError)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getRecipeByPage: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeAdapter.bindData(it.value.recipes)

                    nextPage++
                    Log.d(TAG, "getRecipeByPage: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    Log.d(TAG, "getRecipeByPage:State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getRecipeByPage: State is unknown!")
                }
            }
        })
    }

    private fun getLatestRecipe() {
//        viewModel.getLatestRecipe()
        viewModel.latestRecipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)
            toggleNetworkError(isNetworkError)

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getLatestRecipe: State is loading!")
                }
                is ResponseStatus.Success -> {
                    bannerAdapter.bindData(it.value.recipes)
                    viewBinding.sliderIndicator.refreshDots()

                    Log.d(TAG, "getLatestRecipe: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    Log.d(TAG, "getLatestRecipe: State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getLatestRecipe: State is unknown!")
                }
            }
        })
    }

    private fun toggleLoading(show: Boolean) {
        viewBinding.pbRecipeList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggleNetworkError(show: Boolean) {
        viewBinding.networkStatusContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggleRetry() {
        viewBinding.btnRetryNetwork.setOnClickListener { v ->
            viewModel.getRecipeByPage(nextPage)
            viewModel.getLatestRecipe()
        }
    }

    private fun setListMode() {
        recipeAdapter.holderType = viewHolderType
        viewBinding.rvRecipe.adapter = recipeAdapter
        viewBinding.rvRecipe.layoutManager = when (viewHolderType) {
            ViewHolderType.CARD -> LinearLayoutManager(requireContext())
            ViewHolderType.LIST -> LinearLayoutManager(requireContext())
            ViewHolderType.GRID -> GridLayoutManager(requireContext(), 2)
        }

        viewBinding.rvRecipe.scrollToPosition(lastVisibleItem)
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private fun showPopupMenu(view: View) {
        // inflate menu
        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_act_cardview -> {
                        viewHolderType = ViewHolderType.CARD
                        setListMode()

                        Toast.makeText(view.context, "Changed to card", Toast.LENGTH_SHORT).show()
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_act_list -> {
                        viewHolderType = ViewHolderType.LIST
                        setListMode()

                        Toast.makeText(view.context, "Changed to list", Toast.LENGTH_SHORT).show()
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_act_grid -> {
                        viewHolderType = ViewHolderType.GRID
                        setListMode()

                        Toast.makeText(view.context, "Changed to grid", Toast.LENGTH_SHORT).show()
                        return@OnMenuItemClickListener true
                    }
                }
                return@OnMenuItemClickListener false
            })
            show()
        }
    }

    private fun buildBannerAdapter() {
        bannerAdapter = BannerAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Recipe) {
                    Toast.makeText(requireContext(), "${data.title} Item clicked!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildBanner() {
        viewBinding.vp2Banner.apply {
            adapter = bannerAdapter
            offscreenPageLimit = 3
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            val recyclerViewChild = children.firstOrNull() as RecyclerView
            recyclerViewChild.clipChildren = false
            recyclerViewChild.clipToPadding = false
            recyclerViewChild.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            recyclerViewChild.setPadding(50, 0, 50, 0)

            setPageTransformer(OverlapSliderTransformation(ViewPager2.ORIENTATION_HORIZONTAL))
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bannerHandler.removeCallbacks(bannerRunnable)
                    bannerHandler.postDelayed(bannerRunnable, bannerInterval)

                    viewBinding.sliderIndicator.refreshDots()
                }
            })

            bannerRunnable = Runnable {
                if (bannerAdapter.itemCount > 0) {
                    if (currentItem + 1 == bannerAdapter.itemCount) {
                        currentItem = 0
                    } else {
                        currentItem += 1
                    }
                }
            }

            viewBinding.sliderIndicator.setViewPager2(this)
        }

    }

}