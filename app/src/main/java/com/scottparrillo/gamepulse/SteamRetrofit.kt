package com.scottparrillo.gamepulse

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface SteamRetrofit {

    object retrofitSteam{
        private const val BASE_URL = "https://api.steampowered.com/"
      val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }


    }
    object apiSteam{
        val apiS: SteamWebAPIClient by lazy {
            retrofitSteam.retrofit.create(SteamWebAPIClient::class.java)
        }
    }


}