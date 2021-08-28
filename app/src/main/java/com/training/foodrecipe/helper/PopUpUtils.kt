package com.training.foodrecipe.helper

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 27/08/2021 21.40
 * https://gitlab.com/indra-yana
 ****************************************************/


fun Context.toast(msg: String, long: Boolean = false) {
    Toast.makeText(this, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun View.snackBar(msg: String, long: Boolean = false, actionName: String = "Retry", action: (() -> Unit)? = null) {
    Snackbar.make(this, msg, if (long) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).apply {
        action?.let {
            setAction(actionName) {
                it()
            }
        }
        show()
    }
}