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

fun Context.shortToast(msg: String): Unit = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Context.longToast(msg: String): Unit = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
fun View.shortSnackBar(msg: String): Unit = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
fun View.longSnackBar(msg: String): Unit = Snackbar.make(this, msg, Snackbar.LENGTH_LONG).show()