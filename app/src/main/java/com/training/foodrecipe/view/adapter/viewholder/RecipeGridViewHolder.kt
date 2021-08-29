package com.training.foodrecipe.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.34
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeGridViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var ivItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_grid, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_grid, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        Glide.with(itemView.context)
            .load(data.thumb)
            .apply(RequestOptions().override(350, 350))
            .into(ivItemThumbnail)

        itemView.setOnClickListener {
            listener?.onItemClicked(data, absoluteAdapterPosition)
        }
    }
}