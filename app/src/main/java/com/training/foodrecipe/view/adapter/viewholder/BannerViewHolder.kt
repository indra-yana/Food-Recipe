package com.training.foodrecipe.view.adapter.viewholder

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.databinding.ItemCardBannerBinding
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On 08/04/2020 10.49
 ****************************************************/

class BannerViewHolder(private val binding: ItemCardBannerBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        with(binding) {
            tvItemTitle.text = data.title
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