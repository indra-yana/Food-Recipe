package com.training.foodrecipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.adapter.viewholder.SimpleTextViewHolder

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Wednesday, 12/05/2021 09.11
 * https://gitlab.com/indra-yana
 ****************************************************/
class SimpleTextAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: List<String> = listOf()

    var iOnItemClickListener: IOnItemClickListener? = null
    var vHolder: RecyclerView.ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SimpleTextViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bind(itemList[position], iOnItemClickListener)
        vHolder = holder
    }

    override fun getItemCount(): Int = itemList.size

    fun bindData(itemList: List<String>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

}