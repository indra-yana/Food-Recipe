package com.training.foodrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On 03/04/2020 13.56
 ****************************************************/
class ProfileFragmentViewModelTest : ViewModel() {

    private val mCount = MutableLiveData<Int>().apply {
        value = 0
    }

    fun getCount(): LiveData<Int> {
        return mCount
    }

    fun setCount(btn: String) {
        if (btn == "+") {
            mCount.value = mCount.value?.plus(1)
        } else if (btn == "-") {
            mCount.value = mCount.value?.minus(1)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}