package com.training.foodrecipe.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.databinding.ItemCardArticleBinding
import com.training.foodrecipe.view.adapter.viewholder.ArticleViewHolder
import com.training.foodrecipe.view.adapter.viewholder.BaseViewHolder
import com.training.foodrecipe.helper.DiffUtils
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Article

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/08/2021 13.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: MutableList<Article> = mutableListOf()
    var iOnItemClickListener: IOnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleViewHolder(ItemCardArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseViewHolder).bindItem(itemList[position], iOnItemClickListener)
    }

    override fun getItemCount(): Int = itemList.size

    fun bindData(itemList: List<Article>) {
        val diffCallback = DiffUtils(this.itemList, itemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.itemList.clear()
        this.itemList.addAll(itemList)

        diffResult.dispatchUpdatesTo(this)
    }

}