package com.training.foodrecipe.view.adapter.viewholder

import com.training.foodrecipe.databinding.ItemCardArticleBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Article

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/08/2021 13.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleViewHolder(private val binding: ItemCardArticleBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Article

        with(binding) {
            tvItemTitle.text = data.title
            btnToggleReadMore.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }
        }
    }

}