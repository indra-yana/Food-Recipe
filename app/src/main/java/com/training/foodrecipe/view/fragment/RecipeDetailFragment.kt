package com.training.foodrecipe.view.fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.view.adapter.NeededItemAdapter
import com.training.foodrecipe.view.adapter.SimpleTextAdapter
import com.training.foodrecipe.databinding.FragmentRecipeDetailBinding
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.helper.handleRequestError
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.model.NeedItem
import com.training.foodrecipe.model.Recipe
import com.training.foodrecipe.model.RecipeDetail
import com.training.foodrecipe.repository.RecipeRepository
import com.training.foodrecipe.viewmodel.RecipeViewModel
import timber.log.Timber

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/08/2021 22.02
 * https://gitlab.com/indra-yana
 ****************************************************/
class RecipeDetailFragment : BaseFragment<FragmentRecipeDetailBinding, RecipeViewModel, RecipeRepository>() {

    companion object {
        private val TAG = RecipeDetailFragment::class.java.simpleName
    }

    // Adapter
    private lateinit var neededItemAdapter: NeededItemAdapter
    private lateinit var detailTodoAdapter: SimpleTextAdapter

    // Data
    private lateinit var recipe: Recipe
    private var recipeDetail: RecipeDetail? = null

    // Indicator state
    private var isLoading = false
    private var isNetworkError = false
    private var isFavourite = false

    /**
     * Init all variable here that need once time initialization
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = arguments?.getParcelable("recipe")!!

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

        buildNeededItemAdapter()
        buildNeededItemRV()
        buildTodoAdapter()
        updateUI()

        observeRecipeDetail()
        observeFavourite()
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRecipeDetailBinding {
        return FragmentRecipeDetailBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): Class<RecipeViewModel> {
        return RecipeViewModel::class.java
    }

    override fun getRepository(): RecipeRepository {
        return RecipeRepository()
    }

    private fun observeRecipeDetail() {
        viewModel.recipeDetail.observe(viewLifecycleOwner, Observer {
            isLoading = it is ResponseStatus.Loading
            isNetworkError = it is ResponseStatus.Failure

            toggleLoading(isLoading)

            when (it) {
                is ResponseStatus.Loading -> {
                    Timber.d(TAG, "observeRecipeDetail: State is loading!")
                }
                is ResponseStatus.Success -> {
                    recipeDetail = it.value.recipeDetail
                    updateUI()

                    Timber.d(TAG, "observeRecipeDetail: State is success! ${it.value.recipeDetail}")
                }
                is ResponseStatus.Failure -> {
                    handleRequestError(it) { fetchData() }

                    Timber.d(TAG, "observeRecipeDetail:State is failure! ${it.exception}")
                }
                else -> {
                    Timber.d(TAG, "observeRecipeDetail: State is unknown!")
                }
            }
        })
    }

    private fun prepareUI() {
        with(viewBinding) {
            srlRefresh.setOnRefreshListener {
                fetchData()
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnAddFavourite.setOnClickListener {
                isFavourite = !isFavourite
                setFavourite(isFavourite)
            }
        }
    }

    private fun updateUI() {
        with(viewBinding) {
            tvLevel.text = recipe.dificulty ?: (recipe.difficulty ?: "-")
            tvPortion.text = recipe.portion ?: (recipe.serving ?: "-")
            tvTime.text = recipe.times

            Glide.with(requireView().context)
                .load(recipe.thumb)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.image_placeholder))
                .apply(RequestOptions().override(450, 250))
                .into(ivItemThumbnail)

            recipeDetail?.apply {
                tvAuthor.text = ("By ${author.user}, ${author.datePublished}")
                tvItemTitle.text = title
                tvItemDescription.text = desc

                neededItemAdapter.bindData(needItem)

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

                btnIngredients.setOnClickListener {
                    openTodoBottomSheet("Ingredients", this)
                }

                btnSteps.setOnClickListener {
                    openTodoBottomSheet("Steps", this)
                }

                isFavourite = favourite
                toggleFavourite(isFavourite)
            }
        }
    }

    private fun buildNeededItemAdapter() {
        neededItemAdapter = NeededItemAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    data as NeedItem
                    Toast.makeText(requireContext(), data.itemName, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildNeededItemRV() {
        with(viewBinding) {
            rvNeededItem.setHasFixedSize(true)
            rvNeededItem.adapter = neededItemAdapter
            rvNeededItem.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openTodoBottomSheet(title: String, data: Any) {
        val recipeDetail = data as RecipeDetail
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_todo_detail_recipe, requireView().findViewById<LinearLayout>(R.id.layoutBottomSheetContainer), false)

        view?.apply {
            minimumHeight = Resources.getSystem().displayMetrics.heightPixels

            (findViewById<ImageButton>(R.id.btnBottomSheetClose)).setOnClickListener { bottomSheetDialog.dismiss() }
            (findViewById<TextView>(R.id.tvBottomSheetTitle)).text = title

            buildTodoRV(findViewById(R.id.rvRecipeDetailTodo))

            if (title == "Ingredients") {
                detailTodoAdapter.bindData(recipeDetail.ingredient)
            } else if (title == "Steps") {
                detailTodoAdapter.bindData(recipeDetail.step)
            }
        }

        bottomSheetDialog.setContentView(view)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.apply {
            peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            state = BottomSheetBehavior.STATE_SETTLING
        }

        bottomSheetDialog.show()
    }

    private fun buildTodoAdapter() {
        detailTodoAdapter = SimpleTextAdapter().apply {
            iOnItemClickListener = object : IOnItemClickListener {
                override fun onItemClicked(data: Any) {
                    // TODO: Not yet implemented
                }
            }
        }
    }

    private fun buildTodoRV(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = detailTodoAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun toggleLoading(isLoading: Boolean) {
        viewBinding.srlRefresh.isRefreshing = isLoading
        viewBinding.shimmerFramelayout.showShimmer(isLoading || isNetworkError)

        if (isLoading || isNetworkError) {
            viewBinding.shimmerRecipeDetailPlaceholder.visible(true)
            viewBinding.mainRecipeDetailContainer.visible(false)
        } else {
            viewBinding.shimmerFramelayout.stopShimmer()
            viewBinding.shimmerFramelayout.hideShimmer()

            viewBinding.shimmerRecipeDetailPlaceholder.visible(false)
            viewBinding.mainRecipeDetailContainer.visible(true)
        }
    }

    private fun toggleFavourite(isFavourite: Boolean) {
        if (isFavourite) {
            viewBinding.btnAddFavourite.setImageResource(R.drawable.ic_favourite_filled)
            viewBinding.btnAddFavourite.drawable.setTint(ContextCompat.getColor(requireContext(), R.color.colorDanger))
        } else {
            viewBinding.btnAddFavourite.setImageResource(R.drawable.ic_favourite_border)
        }
    }

    private fun setFavourite(isFavourite: Boolean) {
        viewModel.setFavourite(recipe.key, isFavourite)
    }

    private fun observeFavourite() {
        viewModel.isFavourite.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResponseStatus.Success -> {
                    toggleFavourite(it.value)
                    when (it.value) {
                        true -> Toast.makeText(requireContext(), "Added to favourite", Toast.LENGTH_SHORT).show()
                        false -> Toast.makeText(requireContext(), "Removed from favourite", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> { }
            }
        })
    }

    private fun fetchData() {
        viewModel.getRecipeDetail(recipe.key)
    }
}