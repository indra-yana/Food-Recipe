package com.training.foodrecipe.view.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.training.foodrecipe.listener.IOnItemClickListener

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.48
 * https://gitlab.com/indra-yana
 ****************************************************/

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var enableBtnItemRemove: Boolean = false

    abstract fun bindItem(data: Any, listener: IOnItemClickListener?)
}