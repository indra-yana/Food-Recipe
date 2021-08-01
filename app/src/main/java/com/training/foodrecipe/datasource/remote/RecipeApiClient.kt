package com.training.foodrecipe.datasource.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/****************************************************
 * Created by Indra Muliana (indra.ndra26@gmail.com)
 * On Friday, 02/04/2021 14.30
 * https://gitlab.com/indra-yana
 ****************************************************/

object RecipeApiClient {

    private const val BASE_URL = "https://masak-apa.tomorisakura.vercel.app"
    private const val API_KEY = "your_secret_api_key"
    private val retrofit: Retrofit

    init {
        // Add additional query param like API_KEY or Header
        val requestInterceptor = Interceptor { chain ->
            val urlBuilder = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val requestHeaders = chain.request()
                .newBuilder()
                .url(urlBuilder)
                .addHeader("Accept", "application/json")
                .build()

            return@Interceptor chain.proceed(requestHeaders)
        }

        // Add logging interceptor if needed
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        // OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <Api> crete(api: Class<Api>): Api = retrofit.create(api)

}