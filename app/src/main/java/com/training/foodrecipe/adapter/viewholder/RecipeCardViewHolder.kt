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
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeCardViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvName: TextView = itemView.findViewById(R.id.tv_item_title)
    private var imgPhoto: ImageView = itemView.findViewById(R.id.ivDetailThumb)
    private var btnFavourite: ImageButton = itemView.findViewById(R.id.btn_set_favourite)
    private var btnShare: ImageButton = itemView.findViewById(R.id.btn_set_share)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_card, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_card, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        val recipe = data as Recipe

        tvName.text = recipe.title
        Glide.with(itemView.context)
            .load(recipe.thumb)
            .apply(RequestOptions().override(450, 250))
            .into(imgPhoto)

        btnFavourite.setOnClickListener {
            listener?.onButtonFavouriteClicked(recipe)
        }

        btnShare.setOnClickListener {
            listener?.onButtonShareClicked(recipe)
        }

        itemView.setOnClickListener {
            listener?.onItemClicked(recipe)
        }
    }

}