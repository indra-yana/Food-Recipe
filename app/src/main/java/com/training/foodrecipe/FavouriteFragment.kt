package com.training.foodrecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.training.foodrecipe.viewmodel.ProfileFragmentViewModelTest
import kotlinx.android.synthetic.main.fragment_favourite.*

class FavouriteFragment : Fragment() {

    companion object {
        private val TAG = FavouriteFragment::class.java.simpleName
    }

    private lateinit var viewModel : ProfileFragmentViewModelTest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModelTest::class.java).apply {
            getCount().observe(viewLifecycleOwner, Observer {
                tvResult.text = it.toString()
            })

            btnPlus.setOnClickListener {
                setCount("+")
            }

            btnMinus.setOnClickListener {
                setCount("-")
            }
        }
    }

}