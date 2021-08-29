package com.training.foodrecipe.view.adapter.viewholder

import android.annotation.SuppressLint
import android.text.TextUtils
import com.training.foodrecipe.R
import com.training.foodrecipe.databinding.ItemModeTextBinding
import com.training.foodrecipe.helper.hasEllipsis
import com.training.foodrecipe.listener.IOnItemClickListener

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.31
 * https://gitlab.com/indra-yana
 ****************************************************/

class SimpleTextViewHolder(private val binding: ItemModeTextBinding) : BaseViewHolder(binding.root) {

    override fun bindItem(data: Any, listener: IOnItemClickListener?) {
        data as String

        with(binding) {
            tvItemTitle.text = data
            btnToggleReadMore.setOnClickListener {
                expandText()
            }

            root.setOnClickListener {
                listener?.onItemClicked(data, absoluteAdapterPosition)
                expandText()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun expandText() {
        with(binding) {
            if (tvItemTitle.maxLines == Int.MAX_VALUE) {
                tvItemTitle.maxLines = 2
                tvItemTitle.ellipsize = TextUtils.TruncateAt.END

                btnToggleReadMore.setImageDrawable(root.resources.getDrawable(R.drawable.ic_arrow_down, null))
            } else if (tvItemTitle.ellipsize == TextUtils.TruncateAt.END && tvItemTitle.hasEllipsis()) {
                tvItemTitle.maxLines = Int.MAX_VALUE
                tvItemTitle.ellipsize = null

                btnToggleReadMore.setImageDrawable(root.resources.getDrawable(R.drawable.ic_arrow_up, null))
            }
        }
    }

}