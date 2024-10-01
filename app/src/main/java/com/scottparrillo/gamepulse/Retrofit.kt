package com.scottparrillo.gamepulse

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object XboxRetrofit {
    private const val BASE_URL = "https://api.xboxlive.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiClient: XboxWebAPIClient by lazy {
        retrofit.create(XboxWebAPIClient::class.java)
    }
}
