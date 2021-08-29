package com.training.foodrecipe.view.adapter.viewholder

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.databinding.ItemModeListBinding
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.33
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeListViewHolder(private val binding: ItemModeListBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        with(binding) {
            tvItemTitle.text = data.title
            tvMisc.text = ("${data.dificulty ?: (data.difficulty ?: "-")} • ${data.portion ?: (data.serving ?: "-")} • ${data.times}")

            Glide.with(root.context)
                .load(data.thumb)
                .placeholder(ContextCompat.getDrawable(root.context, R.drawable.image_placeholder))
                .apply(RequestOptions().override(100, 60))
                .into(ivItemThumbnail)

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }

            if (enableBtnItemRemove) {
                btnRemove.visible(true)
                btnRemove.setOnClickListener {
                    listener?.onToggleDeleteItemClicked(data, absoluteAdapterPosition)
                }
            }
        }
    }
}