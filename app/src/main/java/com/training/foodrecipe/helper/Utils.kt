package com.training.foodrecipe.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.Layout
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.training.foodrecipe.datasource.remote.response.ResponseStatus
import com.training.foodrecipe.view.MainActivity
import com.training.foodrecipe.view.fragment.ArticleFragment
import com.training.foodrecipe.view.fragment.RecipeFragment
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException


/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Saturday, 16/01/2021 13.08
 * https://gitlab.com/indra-yana
 ****************************************************/

private const val TAG = "Utils"

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(this)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun String.getRandomString(length: Int) {
    (('a'..'z') + ('A'..'Z') + ('0'..'9')).random().toString().substring(0, length)
}

fun Fragment.handleRequestError(failure: ResponseStatus.Failure, action: (() -> Unit)? = null) {
    val anchor = when (this) {
        is RecipeFragment -> {
            (activity as MainActivity).binding.fabCreate
        }
        is ArticleFragment -> {
            (activity as MainActivity).binding.fabCreate
        }
        else -> null
    }

    when (val exception = failure.exception) {
        is HttpException -> {
            when (val errorCode = exception.code()) {
                401 -> requireView().snackBar("$errorCode: Bad request!", anchor = anchor)
                403 -> requireView().snackBar("$errorCode: Not authorize!", anchor = anchor)
                404 -> requireView().snackBar("$errorCode: Resource not found!", anchor = anchor)
                500 -> requireView().snackBar("$errorCode: Internal server error!", anchor = anchor)
                else -> requireView().snackBar("Http response failure with status code $errorCode", anchor = anchor)
            }

            Timber.tag(TAG).e("handleRequestError: ${exception.response()?.errorBody()?.string().toString()}")
        }
        is UnknownHostException -> {
            requireView().snackBar("Please check your internet connection!", long = true, anchor = anchor, action = action)

            Timber.tag(TAG).e("handleRequestError: ${exception.message}")
        }
        else -> {
            requireView().snackBar("Something when wrong please try again later!", long = true, anchor = anchor, action = action)

            Timber.tag(TAG).e("handleRequestError: ${exception.message}")
        }
    }
}

fun showInputKey(view: View, show: Boolean) {
    when (show) {
        true -> {
            view.requestFocus().run {
                val input = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        false -> {
            view.requestFocus().run {
                val input = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}

fun TextView.hasEllipsis(): Boolean {
    var hasLongContent = false
    val descriptionLayout: Layout? = this.layout

    if (descriptionLayout != null) {
        val lines: Int = descriptionLayout.lineCount
        if (lines > 0) {
            hasLongContent = descriptionLayout.getEllipsisCount(lines - 1) > 0
        }
    }

    return hasLongContent
}

//fun ImageView.showOrHidePassword(passwordField: EditText, passwordField2: EditText? = null) {
//    setOnClickListener {
//        if (passwordField.transformationMethod == PasswordTransformationMethod.getInstance()) {
//            setImageResource(R.drawable.ic_eye_invisible)
//            passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            passwordField2?.transformationMethod = HideReturnsTransformationMethod.getInstance()
//        } else if (passwordField.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
//            setImageResource(R.drawable.ic_eye_visible)
//            passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
//            passwordField2?.transformationMethod = PasswordTransformationMethod.getInstance()
//        }
//
//        passwordField.setSelection(passwordField.text.length)
//        passwordField2?.setSelection(passwordField2.text.length)
//    }
//}

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Float.cube(): Float = this * this * this
fun Int.cube(): Int = this * this * this

fun Float.sqr(): Float = this * this
fun Int.sqr(): Int = this * this


