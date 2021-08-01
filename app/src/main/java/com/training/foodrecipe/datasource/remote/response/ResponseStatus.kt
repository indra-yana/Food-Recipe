package com.training.foodrecipe.datasource.remote.response

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 19.07
 * https://gitlab.com/indra-yana
 ****************************************************/

sealed class ResponseStatus<out T> {
    object Loading: ResponseStatus<Nothing>()
    data class Success<out T>(val value: T) : ResponseStatus<T>()
    data class Failure(val exception: Exception) : ResponseStatus<Nothing>()
}