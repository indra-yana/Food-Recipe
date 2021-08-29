package com.training.foodrecipe.view.adapter.viewholder

import com.training.foodrecipe.databinding.ItemCategoriesBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.ArticleCategory
import com.training.foodrecipe.model.RecipeCategory

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class CategoryViewHolder(private val binding: ItemCategoriesBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        with(binding) {
            tvItemTitle.text = when (data) {
                is RecipeCategory -> {
                    data.category
                }
                is ArticleCategory -> {
                    data.title
                }
                else -> "-"
            }

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }
        }
    }

}