package com.scottparrillo.gamepulse.api

import com.scottparrillo.gamepulse.XboxWebAPIClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://xbl.io/api/v2/") // Base URL for OpenXBL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Use XboxWebAPIClient instead of OpenXBLApiService
    val xboxWebAPIClient: XboxWebAPIClient by lazy {
        retrofit.create(XboxWebAPIClient::class.java)
    }
}
