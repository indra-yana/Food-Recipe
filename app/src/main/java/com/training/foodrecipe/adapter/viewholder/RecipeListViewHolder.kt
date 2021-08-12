package com.training.foodrecipe.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.33
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeListViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var tvMisc: TextView = itemView.findViewById(R.id.tvMisc)
    private var tvServing: TextView = itemView.findViewById(R.id.tvServing)
    private var ivItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)
    private var btnAddFavourite: ImageButton = itemView.findViewById(R.id.btnAddFavourite)
    private var btnShare: ImageButton = itemView.findViewById(R.id.btnShare)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_list, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_list, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        tvItemTitle.text = data.title
        tvMisc.text = ("${data.dificulty ?: (data.difficulty ?: "-")} | ${data.portion ?: (data.serving ?: "-")} | ${data.times}")
        // tvServing.text = recipe.serving

        Glide.with(itemView.context)
            .load(data.thumb)
            .apply(RequestOptions().override(100, 60))
            .into(ivItemThumbnail)

        itemView.setOnClickListener {
            listener?.onItemClicked(data)
        }

        btnAddFavourite.setOnClickListener {
            listener?.onButtonFavouriteClicked(data)
        }

        btnShare.setOnClickListener {
            listener?.onButtonShareClicked(data)
        }

    }
}