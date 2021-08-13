package com.training.foodrecipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.adapter.viewholder.BannerViewHolder
import com.training.foodrecipe.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.helper.DiffUtils
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 12/05/2021 09.11
 * https://gitlab.com/indra-yana
 ****************************************************/
class BannerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: MutableList<Recipe> = mutableListOf()

    var iOnItemClickListener: IOnItemClickListener? = null
    var vHolder: RecyclerView.ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BannerViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(itemList[position], iOnItemClickListener)
        vHolder = holder
    }

    override fun getItemCount(): Int = itemList.size

    fun bindData(itemList: List<Recipe>) {
        val diffCallback = DiffUtils(this.itemList, itemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.itemList.clear()
        this.itemList.addAll(itemList)

        diffResult.dispatchUpdatesTo(this)
    }

    /*
    fun bindData(itemList: List<Recipe>) {
        val oldCount: Int = itemCount

        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyItemRangeInserted(oldCount, itemCount)
    }
    */
}