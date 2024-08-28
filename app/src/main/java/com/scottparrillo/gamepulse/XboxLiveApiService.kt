package com.scottparrillo.gamepulse.api

import com.scottparrillo.gamepulse.GameDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface XboxLiveApiService {

    @GET("endpoint/path") // Replace with actual endpoint
    suspend fun getGameDetails(
        @Query("gameId") gameId: String
    ): GameDetailsResponse // Replace with actual response model
}
