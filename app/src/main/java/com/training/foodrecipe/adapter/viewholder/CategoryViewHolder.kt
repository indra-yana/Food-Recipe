package com.training.foodrecipe.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.ArticleCategory
import com.training.foodrecipe.model.RecipeCategory


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class CategoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_categories, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        tvItemTitle.text = when(data) {
            is RecipeCategory -> { data.category }
            is ArticleCategory -> { data.title }
            else -> "-"
        }

        itemView.setOnClickListener {
            listener?.onItemClicked(data)
        }
    }

}