package com.training.foodrecipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.training.foodrecipe.helper.ConnectivityHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_recipe.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ConnectivityHelper(applicationContext).observe(this, Observer { isConnected ->
            if (isConnected) {
//                networkStatusContainer.visibility = View.GONE
                layoutConnected.visibility = View.VISIBLE
                layoutDisconnected.visibility = View.GONE
            } else {
//                networkStatusContainer.visibility = View.VISIBLE
                layoutConnected.visibility = View.GONE
                layoutDisconnected.visibility = View.VISIBLE
            }
        })
    }
}