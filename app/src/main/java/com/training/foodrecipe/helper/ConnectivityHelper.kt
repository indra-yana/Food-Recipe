@file:Suppress("DEPRECATION")

package com.training.foodrecipe.helper

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData

class ConnectivityHelper(private val context: Context) : LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    init {
        connectivityManagerCallback()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onActive() {
        super.onActive()
        updateConnection()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
                Log.d("onActive", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.N")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                lollipopConnectionCallback()
                Log.d("onActive", " Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP")
            }
            else -> {
                context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                Log.d("onActive", "Using Broadcast Receiver Callback")
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } else {
            context.unregisterReceiver(networkReceiver)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopConnectionCallback() {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)

        connectivityManager.registerNetworkCallback(
            requestBuilder.build(),
            networkCallback
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(false)

                    Log.d("networkCallback", "onLost")
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    postValue(true)

                    Log.d("networkCallback", "onAvailable")
                }
            }
            return networkCallback
        } else {
            throw IllegalAccessError("Error")
        }
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()

            Log.d("updateConnection", "onReceive")
        }
    }

    private fun updateConnection() {
        val activeNetwork : NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)

        Log.d("updateConnection", "${activeNetwork?.isConnected}")
    }

}