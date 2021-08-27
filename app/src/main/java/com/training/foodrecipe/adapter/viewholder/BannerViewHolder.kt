package com.training.foodrecipe.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On 08/04/2020 10.49
 ****************************************************/
class BannerViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var ivItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_banner, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_card_banner, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        tvItemTitle.text = data.title
        Glide.with(itemView.context)
            .load(data.thumb)
            .apply(RequestOptions().override(350, 350))
            .into(ivItemThumbnail)

        itemView.setOnClickListener {
            listener?.onItemClicked(data)
        }
    }

}