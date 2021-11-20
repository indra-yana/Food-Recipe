package com.training.foodrecipe.view.fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.training.foodrecipe.R
import com.training.foodrecipe.databinding.FragmentRecipeBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.OverlapSliderTransformation
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.snackBar
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.listener.IOnFabClickListener
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.view.adapter.*
import com.training.foodrecipe.view.fragment.base.BaseFragment
import com.training.foodrecipe.viewmodel.RecipeViewModel
import timber.log.Timber

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 22.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeFragment : BaseFragment<FragmentRecipeBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = this::class.java.simpleName
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

        buildBannerAdapter()
        buildRecipeAdapter()
        fetchData(initialPage)
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
                override fun onFabFavouriteClicked(view: View) {
                    findNavController().navigate(R.id.action_homeFragment_to_nav_favourite)
                }
            }
        }

        prepareUI()

        // Banner
        buildBanner()
        observeLatestRecipe()

        // Recipe
        buildRecipeRV()
        observeRecipeByPage()
    }

    override fun onResume() {
        super.onResume()
        bannerHandler.postDelayed(bannerRunnable, bannerInterval)
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacks(bannerRunnable)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentRecipeBinding.inflate(inflater, container, false)
    override fun getViewModel() = RecipeViewModel::class.java
    override fun getRepository() = RecipeRepository()

    private fun buildRecipeAdapter() {
        recipeAdapter = RecipeAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any, position: Int) {
                    gotoDetail(data)
                }
            }
        }
    }

    private fun buildRecipeRV() = with(viewBinding) {

            setListMode()

            rvRecipe.setHasFixedSize(true)
            rvRecipe.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    /*
                    * TODO: Not implemented yet
                    when(recyclerView.scrollState) {
                        RecyclerView.SCROLL_STATE_IDLE -> Timber.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_IDLE")
                        RecyclerView.SCROLL_STATE_DRAGGING -> Timber.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_DRAGGING")
                        RecyclerView.SCROLL_STATE_SETTLING -> Timber.d(TAG, "onScrollStateChanged: State is SCROLL_STATE_SETTLING")
                    }
                     */
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (visibleItem > -1) {
                        lastVisibleItem = visibleItem
//                        Timber.d(TAG, "onScrolled: visibleItemAnchorPoint: $visibleItemAnchorPoint")
                    }

                    // Scroll down
                    if (!recyclerView.canScrollVertically(1)) {
                        if (!isLoading) {
                            isRequestNextPage = true

                            if (!isNetworkError) {
                                nextPage++
                            }

                            viewModel.getRecipeByPage(nextPage)
                            Timber.d(TAG, "onScrolled: nextPage: $nextPage")
                        }
                    }

                    // Scroll up
                    if (recyclerView.canScrollVertically(-1)) {
//                        isNetworkError = false
                    }
                }
            })
    }

    private fun observeRecipeByPage() {
        viewModel.recipe.observe(viewLifecycleOwner, {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeRecipeByPage: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeAdapter.bindData(it.value.recipes)

                    Timber.d(TAG, "observeRecipeByPage: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData(nextPage) }

                    Timber.d(TAG, "observeRecipeByPage:State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeRecipeByPage: State is unknown!")
                }
            }
        })
    }

    private fun buildBannerAdapter() {
        bannerAdapter = BannerAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any, position: Int) {
                    gotoDetail(data)
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

                    Handler().post {
                        viewBinding.sliderIndicator.refreshDots()
                    }
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

    private fun observeLatestRecipe() {
        viewModel.latestRecipe.observe(viewLifecycleOwner, {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeLatestRecipe: State is loading!")
                }
                is ResponseStatus.Success -> {
                    bannerAdapter.bindData(it.value.recipes)
                    viewBinding.sliderIndicator.refreshDots()

                    Timber.d(TAG, "observeLatestRecipe: State is success! ${it.value.recipes}")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData(nextPage) }

                    Timber.d(TAG, "observeLatestRecipe: State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeLatestRecipe: State is unknown!")
                }
            }
        })
    }

    override fun prepareUI() = with(viewBinding) {
            layoutSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_nav_search)
            }

            tvInputSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_nav_search)
            }

            srlRefresh.setOnRefreshListener {
                isLoading = false
                isNetworkError = false
                isRequestNextPage = false

                fetchData(nextPage)
            }

            layoutHeader.tvHeaderTitle.text = getString(R.string.text_welcome_user)
            layoutHeader.btnBack.visible(false)
            layoutHeader.ivHeaderCreate.visible(false)
            layoutHeader.ivHeaderMenu.setOnClickListener {
                showPopupMenu(it)
            }
    }

    override fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerRecipeContainer.showShimmer((isLoading || isNetworkError) && !isRequestNextPage)
        viewBinding.shimmerBannerContainer.showShimmer((isLoading || isNetworkError) && !isRequestNextPage)

        if ((isLoading || isNetworkError) && !isRequestNextPage) {
            // Banner
            viewBinding.shimmerBannerPlaceholder.visible(true)
            viewBinding.layoutBannerContainer.visible(false)

            // Recipe
            viewBinding.shimmerRecipePlaceholder.visible(true)
            viewBinding.layoutRecipeContainer.visible(false)
        } else {
            // Banner
            viewBinding.shimmerBannerContainer.stopShimmer()
            viewBinding.shimmerBannerContainer.hideShimmer()

            viewBinding.shimmerBannerPlaceholder.visible(false)
            viewBinding.layoutBannerContainer.visible(true)

            // Recipe
            viewBinding.shimmerRecipeContainer.stopShimmer()
            viewBinding.shimmerRecipeContainer.hideShimmer()

            viewBinding.shimmerRecipePlaceholder.visible(false)
            viewBinding.layoutRecipeContainer.visible(true)
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

    private fun showPopupMenu(view: View) {
        // inflate menu
        val anchor = (activity as MainActivity).binding.fabCreate

        PopupMenu(view.context, view).apply {
            menuInflater.inflate(R.menu.overflow_menu, menu)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_act_cardview -> {
                        viewHolderType = ViewHolderType.CARD
                        setListMode()

                        requireView().snackBar("Changed to card", anchor = anchor)
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_act_list -> {
                        viewHolderType = ViewHolderType.LIST
                        setListMode()

                        requireView().snackBar("Changed to list", anchor = anchor)
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_act_grid -> {
                        viewHolderType = ViewHolderType.GRID
                        setListMode()

                        requireView().snackBar("Changed to grid", anchor = anchor)
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

    private fun gotoDetail(data: Any) {
        data as Recipe
        val bundle = bundleOf("recipe" to data)

        findNavController().navigate(R.id.action_homeFragment_to_recipeDetailFragment, bundle)
    }

    override fun fetchData(page: Int) {
        viewModel.getLatestRecipe()
        viewModel.getRecipeByPage(page)
    }
}