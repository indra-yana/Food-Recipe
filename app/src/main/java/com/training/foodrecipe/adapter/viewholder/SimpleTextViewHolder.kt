package com.training.foodrecipe.adapter.viewholder

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.training.foodrecipe.R
import com.training.foodrecipe.adapter.IOnItemClickListener
import com.training.foodrecipe.helper.hasEllipsis


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class SimpleTextViewHolder(itemView: View) : BaseViewHolder(itemView) {
    private var tvTitle: TextView = itemView.findViewById(R.id.tv_item_title)
    private var btnToggleReadMore: ImageButton = itemView.findViewById(R.id.btnToggleReadMore)

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.item_mode_text, parent, false)
    )

    override fun bindView(viewGroup: ViewGroup): View {
        return LayoutInflater.from(viewGroup.context).inflate(R.layout.item_mode_text, viewGroup, false)
    }

    override fun bind(data: Any, listener: IOnItemClickListener?) {
        val item = data as String

        tvTitle.text = item
        btnToggleReadMore.setOnClickListener {
            expandText()
        }

        itemView.setOnClickListener {
            listener?.onItemClicked(item)
            expandText()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun expandText() {
        if (tvTitle.maxLines == Int.MAX_VALUE) {
            tvTitle.maxLines = 2
            tvTitle.ellipsize = TextUtils.TruncateAt.END

            btnToggleReadMore.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_arrow_down, null))
        } else if (tvTitle.ellipsize == TextUtils.TruncateAt.END && tvTitle.hasEllipsis()) {
            tvTitle.maxLines = Int.MAX_VALUE
            tvTitle.ellipsize = null

            btnToggleReadMore.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_arrow_up, null))
        }
    }

}