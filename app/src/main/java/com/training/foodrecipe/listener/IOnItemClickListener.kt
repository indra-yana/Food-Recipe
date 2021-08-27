package com.training.foodrecipe.listener


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 14/05/2021 22.25
 * https://gitlab.com/indra-yana
 ****************************************************/

interface IOnItemClickListener {
    fun onItemClicked(data: Any)
    fun onButtonFavouriteClicked(data: Any) { }
    fun onButtonShareClicked(data: Any) { }
}