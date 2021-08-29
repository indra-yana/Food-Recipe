package com.training.foodrecipe.view.adapter.viewholder

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.databinding.ItemModeGridBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.34
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeGridViewHolder(private val binding: ItemModeGridBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        with(binding) {
            Glide.with(root.context)
                .load(data.thumb)
                .apply(RequestOptions().override(350, 350))
                .into(ivItemThumbnail)

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }
        }
    }
}