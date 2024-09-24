package com.scottparrillo.gamepulse.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://xbl.io/api/v2/") // Base URL for OpenXBL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openXBLApiService: OpenXBLApiService by lazy {
        retrofit.create(OpenXBLApiService::class.java)
    }
}
