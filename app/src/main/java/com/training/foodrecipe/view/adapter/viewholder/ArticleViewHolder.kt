package com.training.foodrecipe.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.training.foodrecipe.R
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Article


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/08/2021 13.02
 * https://gitlab.com/indra-yana
 ****************************************************/

class ArticleViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var btnToggleReadMore: ImageButton = itemView.findViewById(R.id.btnToggleReadMore)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_article, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_card_article, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Article

        tvItemTitle.text = data.title
        btnToggleReadMore.setOnClickListener {
            listener?.onItemClicked(data, absoluteAdapterPosition)
        }

        itemView.setOnClickListener {
            listener?.onItemClicked(data, absoluteAdapterPosition)
        }
    }

}