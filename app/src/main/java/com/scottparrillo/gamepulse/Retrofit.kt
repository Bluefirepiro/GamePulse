package com.scottparrillo.gamepulse.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://xboxliveapi.example.com/" // Replace with actual base URL

    val xboxLiveApiService: XboxLiveApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XboxLiveApiService::class.java)
    }
}
