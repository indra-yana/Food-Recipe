package com.training.foodrecipe.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.34
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeGridViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var imgPhoto: ImageView = itemView.findViewById(R.id.ivDetailThumb)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_grid, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_grid, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        val recipe = data as Recipe

        Glide.with(itemView.context)
            .load(recipe.thumb)
            .apply(RequestOptions().override(350, 350))
            .into(imgPhoto)

        itemView.setOnClickListener {
            listener?.onItemClicked(recipe)
        }
    }
}