package com.training.foodrecipe.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.view.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.view.adapter.viewholder.RecipeCardViewHolder
import com.training.foodrecipe.view.adapter.viewholder.RecipeGridViewHolder
import com.training.foodrecipe.view.adapter.viewholder.RecipeListViewHolder
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 12/05/2021 09.11
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: MutableList<Recipe> = mutableListOf()

    var iOnItemClickListener: IOnItemClickListener? = null
    var holderType = ViewHolderType.LIST
    var vHolder: RecyclerView.ViewHolder? = null
    var enableRemove: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (holderType) {
            ViewHolderType.CARD -> RecipeCardViewHolder(parent)
            ViewHolderType.LIST -> RecipeListViewHolder(parent)
            ViewHolderType.GRID -> RecipeGridViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BaseViewHolder

        holder.enableBtnItemRemove = enableRemove
        holder.bind(itemList[position], iOnItemClickListener)

        vHolder = holder
    }

    override fun getItemCount(): Int = itemList.size

    fun bindData(itemList: List<Recipe>) {
        val oldCount: Int = itemCount
        val filtered = itemList.filterNot {
            this.itemList.contains(it)
        }

        this.itemList.addAll(filtered)
        notifyItemRangeInserted(oldCount, itemCount)
    }

    fun clearData() {
        this.itemList.clear()
        notifyDataSetChanged()
    }

    fun onItemRemoved(position: Int, callback: ((recipe: Recipe) -> Unit)? = null) {
        if (position != RecyclerView.NO_POSITION) {
            callback?.let {
                it(itemList[position])
            }

            this.itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }
}