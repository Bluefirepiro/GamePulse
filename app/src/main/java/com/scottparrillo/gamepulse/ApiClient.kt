package com.scottparrillo.gamepulse.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object XboxLiveApiClient {
    private const val BASE_URL = "https://xboxapi.com/v2/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: XboxLiveApiService = retrofit.create(XboxLiveApiService::class.java)
}


