package com.training.foodrecipe.view.adapter.viewholder

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.databinding.ItemModeCardBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeCardViewHolder(private val binding: ItemModeCardBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        with(binding) {
            tvItemTitle.text = data.title
            Glide.with(root.context)
                .load(data.thumb)
                .apply(RequestOptions().override(450, 250))
                .into(ivItemThumbnail)

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }
        }
    }

}