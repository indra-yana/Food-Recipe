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
import com.training.foodrecipe.model.NeedItem

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class NeededItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var ivNeededItem: ImageView = itemView.findViewById(R.id.ivNeededItem)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_neededitem, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_neededitem, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as NeedItem

        tvItemTitle.text = data.itemName
        Glide.with(itemView.context)
            .load(data.thumbItem)
            .apply(RequestOptions().override(450, 250))
            .into(ivNeededItem)

        itemView.setOnClickListener {
            listener?.onItemClicked(data)
        }
    }

}