package com.training.foodrecipe.view.adapter.viewholder

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.databinding.ItemNeededitemBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.NeedItem

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class NeededItemViewHolder(private val binding: ItemNeededitemBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as NeedItem

        with(binding) {
            tvItemTitle.text = data.itemName
            Glide.with(root.context)
                .load(data.thumbItem)
                .apply(RequestOptions().override(450, 250))
                .into(ivNeededItem)

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
            }
        }
    }

}