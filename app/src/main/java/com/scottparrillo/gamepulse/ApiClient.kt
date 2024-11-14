package com.scottparrillo.gamepulse.api

import com.scottparrillo.gamepulse.XboxWebAPIClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Configure OkHttpClient with timeout settings
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Adjust the timeout as needed
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://xbl.io/api/v2/") // Base URL for OpenXBL
            .client(okHttpClient) // Attach the OkHttpClient with timeout settings
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    object openXBL {
        val xboxWebAPIClient: XboxWebAPIClient by lazy {
            retrofit.create(XboxWebAPIClient::class.java)
        }
    }
}
