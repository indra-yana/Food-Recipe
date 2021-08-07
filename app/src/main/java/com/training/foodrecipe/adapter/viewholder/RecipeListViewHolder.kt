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
    private var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    private var tvMisc: TextView = itemView.findViewById(R.id.tvMisc)
    private var tvServing: TextView = itemView.findViewById(R.id.tvServing)
    private var imgPhoto: ImageView = itemView.findViewById(R.id.ivThumbnail)
    private var btnFavourite: ImageButton = itemView.findViewById(R.id.btn_set_favourite)
    private var btnShare: ImageButton = itemView.findViewById(R.id.btn_set_share)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_list, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_list, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        val recipe = data as Recipe

        tvTitle.text = recipe.title
        tvMisc.text = ("${recipe.dificulty ?: "-"} | ${recipe.portion} | ${recipe.times}")
        tvServing.text = recipe.serving

        Glide.with(itemView.context)
            .load(recipe.thumb)
            .apply(RequestOptions().override(100, 60))
            .into(imgPhoto)

        itemView.setOnClickListener {
            listener?.onItemClicked(recipe)
        }

        btnFavourite.setOnClickListener {
            listener?.onButtonFavouriteClicked(recipe)
        }

        btnShare.setOnClickListener {
            listener?.onButtonShareClicked(recipe)
        }

    }
}