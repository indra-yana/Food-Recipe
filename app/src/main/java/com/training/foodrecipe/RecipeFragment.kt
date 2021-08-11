package com.training.foodrecipe

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.training.foodrecipe.adapter.BannerAdapter
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.adapter.RecipeAdapter
import com.training.foodrecipe.adapter.ViewHolderType
import com.training.foodrecipe.databinding.FragmentRecipeBinding
import com.training.foodrecipe.datasource.remote.IRecipeApi
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.OverlapSliderTransformation
import com.training.foodrecipe.helper.handleRequestError
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

    private var bottomSheetAbout: BottomSheetDialog? = null

    // Adapter
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private var viewHolderType: ViewHolderType = ViewHolderType.LIST

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false
    private var isRequestNextPage = false

    // Api paging
    private var initialPage = 1
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

        (activity as MainActivity).apply {
            showFabAction()
            showBottomNavigation()
        }

        buildRecyclerView()
        buildBanner()

        getLatestRecipe()
        getRecipeByPage()

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

            srlRefresh.setOnRefreshListener {
                isLoading = false
                isNetworkError = false
                isRequestNextPage = false

                viewModel.getLatestRecipe()
                viewModel.getRecipeByPage(nextPage)
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
                override fun onItemClicked(data: Any) {
                    val recipe = data as Recipe
                    val bundle = Bundle().apply {
                        putString("args", "This is my args!")
                        putParcelable("recipe", recipe)
                    }

                    findNavController().navigate(R.id.action_homeFragment_to_recipeDetailFragment, bundle)
                }

                override fun onButtonFavouriteClicked(data: Any) {
                    super.onButtonFavouriteClicked(data)

                    val recipe = data as Recipe
                    Toast.makeText(requireContext(), "${recipe.title} Added to favourite!", Toast.LENGTH_SHORT).show()
                }

                override fun onButtonShareClicked(data: Any) {
                    super.onButtonShareClicked(data)

                    val recipe = data as Recipe
                    Toast.makeText(requireContext(), "${recipe.title} Shared to social media!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildRecyclerView() {
        with(viewBinding) {

            setListMode()

            rvRecipe.setHasFixedSize(true)
            rvRecipe.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    /*
                    * TODO: Not implemented yet
                    when(recyclerView.scrollState) {
                        RecyclerView.SCROLL_STATE_IDLE -> Log.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_IDLE")
                        RecyclerView.SCROLL_STATE_DRAGGING -> Log.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_DRAGGING")
                        RecyclerView.SCROLL_STATE_SETTLING -> Log.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_SETTLING")
                    }
                     */
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (visibleItem > -1) {
                        lastVisibleItem = visibleItem
//                        Log.d(TAG, "onScrolled: visibleItemAnchorPoint: $visibleItemAnchorPoint")
                    }

                    // Scroll down
                    if (!recyclerView.canScrollVertically(1)) {
                        if (!isLoading) {
                            isRequestNextPage = true

                            if (!isNetworkError) {
                                nextPage++
                            }

                            viewModel.getRecipeByPage(nextPage)
                            Log.d(TAG, "onScrolled: nextPage: $nextPage")
                        }
                    }

                    // Scroll up
                    if (recyclerView.canScrollVertically(-1)) {
//                        isNetworkError = false
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

            when (it) {
                is ResponseStatus.Loading -> {
                    Log.d(TAG, "getRecipeByPage: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeAdapter.bindData(it.value.recipes)

                    Log.d(TAG, "getRecipeByPage: State is success! ${it.value.recipes}")
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

    private fun getLatestRecipe() {
//        viewModel.getLatestRecipe()
        viewModel.latestRecipe.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

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
                    handleRequestError(it) { retry() }

                    Log.d(TAG, "getLatestRecipe: State is failure! ${it.exception}")
                }
                else -> {
                    Log.d(TAG, "getLatestRecipe: State is unknown!")
                }
            }
        })
    }

    private fun retry() {
        viewModel.getLatestRecipe()
        viewModel.getRecipeByPage(nextPage)
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerFramelayout.showShimmer((isLoading || isNetworkError) && !isRequestNextPage)

        if ((isLoading || isNetworkError) && !isRequestNextPage) {
            viewBinding.shimmerPlaceholder.visibility = View.VISIBLE
            viewBinding.mainRecipeContainer.visibility = View.GONE
        } else {
            viewBinding.shimmerFramelayout.stopShimmer()
            viewBinding.shimmerFramelayout.hideShimmer()

            viewBinding.shimmerPlaceholder.visibility = View.GONE
            viewBinding.mainRecipeContainer.visibility = View.VISIBLE
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
                    R.id.menu_act_about -> {
                        openBottomSheetAbout()
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
                override fun onItemClicked(data: Any) {
                    val recipe = data as Recipe

                    Toast.makeText(requireContext(), "${recipe.title} Item clicked!", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openBottomSheetAbout() {
        if (bottomSheetAbout == null) {
            val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.layout_about_bottom_sheet, requireView().findViewById<LinearLayout>(R.id.layoutAboutContainer), true) //View.inflate(this, R.layout.layout_about_bottom_sheet, findViewById(R.id.layout_about_container))
            with(view) {
                (findViewById<ImageButton>(R.id.btnBottomSheetClose)).setOnClickListener { bottomSheetAbout?.dismiss() }
                (findViewById<ImageView>(R.id.ivAbout)).setImageDrawable(getDrawable(requireContext(), R.drawable.ic_user_smile))
                minimumHeight = Resources.getSystem().displayMetrics.heightPixels
            }

            bottomSheetAbout = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            bottomSheetAbout?.setContentView(view)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_SETTLING
        }

        bottomSheetAbout?.show()
    }
}