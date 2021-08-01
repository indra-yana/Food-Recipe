package com.training.foodrecipe.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.adapter.viewholder.RecipeCardViewHolder
import com.training.foodrecipe.adapter.viewholder.RecipeGridViewHolder
import com.training.foodrecipe.adapter.viewholder.RecipeListViewHolder
import com.training.foodrecipe.model.Recipe
import kotlin.math.log


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 12/05/2021 09.11
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listRecipe: ArrayList<Recipe> = arrayListOf()

    var iOnItemClickListener: IOnItemClickListener? = null
    var holderType = ViewHolderType.LIST
    var vHolder: RecyclerView.ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (holderType) {
            ViewHolderType.CARD -> RecipeCardViewHolder(parent)
            ViewHolderType.LIST -> RecipeListViewHolder(parent)
            ViewHolderType.GRID -> RecipeGridViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(listRecipe[position], iOnItemClickListener)
        vHolder = holder
    }

    override fun getItemCount(): Int = listRecipe.size

    fun bindData(listRecipe: List<Recipe>) {
        val oldCount: Int = itemCount

        this.listRecipe.addAll(listRecipe)
        notifyItemRangeInserted(oldCount, itemCount)
    }

}