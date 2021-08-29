package com.training.foodrecipe.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeCardViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var ivItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)
    private var btnAddFavourite: ImageButton = itemView.findViewById(R.id.btnAddFavourite)
    private var btnShare: ImageButton = itemView.findViewById(R.id.btnShare)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_card, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_card, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        tvItemTitle.text = data.title
        Glide.with(itemView.context)
            .load(data.thumb)
            .apply(RequestOptions().override(450, 250))
            .into(ivItemThumbnail)

        btnAddFavourite.setOnClickListener {
            listener?.onToggleFavouriteItemClicked(data, absoluteAdapterPosition)
        }

        btnShare.setOnClickListener {
            listener?.onToggleShareItemClicked(data, absoluteAdapterPosition)
        }

        itemView.setOnClickListener {
            listener?.onItemClicked(data, absoluteAdapterPosition)
        }
    }

}