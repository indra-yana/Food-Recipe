package com.training.foodrecipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.adapter.viewholder.NeededItemViewHolder
import com.training.foodrecipe.model.NeedItem

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 12/05/2021 09.11
 * https://gitlab.com/indra-yana
 ****************************************************/
class NeededItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listNeededItem: List<NeedItem> = listOf()

    var iOnItemClickListener: IOnItemClickListener? = null
    var vHolder: RecyclerView.ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NeededItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(listNeededItem[position], iOnItemClickListener)
        vHolder = holder
    }

    override fun getItemCount(): Int = listNeededItem.size

    fun bindData(neededItem: List<NeedItem>) {
        this.listNeededItem = neededItem
        notifyDataSetChanged()
    }

}