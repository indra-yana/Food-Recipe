package com.training.foodrecipe.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.training.foodrecipe.R
import com.training.foodrecipe.helper.visible
import com.training.foodrecipe.listener.IOnItemClickListener
import com.training.foodrecipe.model.Recipe

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.33
 * https://gitlab.com/indra-yana
 ****************************************************/

class RecipeListViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvItemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
    private var tvMisc: TextView = itemView.findViewById(R.id.tvMisc)

    // private var tvServing: TextView = itemView.findViewById(R.id.tvServing)
    private var ivItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)
    private var btnAddFavourite: ImageButton = itemView.findViewById(R.id.btnAddFavourite)
    private var btnShare: ImageButton = itemView.findViewById(R.id.btnShare)
    private var btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_list, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_list, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        data as Recipe

        tvItemTitle.text = data.title
        tvMisc.text = ("${data.dificulty ?: (data.difficulty ?: "-")} • ${data.portion ?: (data.serving ?: "-")} • ${data.times}")
        // tvServing.text = recipe.serving

        Glide.with(itemView.context)
            .load(data.thumb)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.image_placeholder))
            .apply(RequestOptions().override(100, 60))
            .into(ivItemThumbnail)

        itemView.setOnClickListener {
            listener?.onItemClicked(data, absoluteAdapterPosition)
        }

        if (enableBtnItemRemove) {
            btnRemove.visible(true)
            btnRemove.setOnClickListener {
                listener?.onToggleDeleteItemClicked(data, absoluteAdapterPosition)
            }
        }
    }
}