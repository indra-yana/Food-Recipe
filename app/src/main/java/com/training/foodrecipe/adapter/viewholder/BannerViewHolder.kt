package com.training.foodrecipe.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On 08/04/2020 10.49
 ****************************************************/
class BannerViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvTitle: TextView = itemView.findViewById(R.id.tv_item_title_2)
    private var imgPhoto: ImageView = itemView.findViewById(R.id.iv_item_banner_2)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_type_2, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_card_type_2, viewGroup, false)
    }

    override fun bind(recipe: Recipe, listener: IOnItemClickListener?) {
        tvTitle.text = recipe.title

        Glide.with(itemView.context)
            .load(recipe.thumb)
            .apply(RequestOptions().override(350, 350))
            .into(imgPhoto)

        itemView.setOnClickListener {
            listener?.onItemClicked(recipe)
        }
    }

}