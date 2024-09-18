package com.scottparrillo.gamepulse.api

import com.google.android.gms.common.api.Response
import com.scottparrillo.gamepulse.Achievement
import com.scottparrillo.gamepulse.GameDetailsResponse
import com.scottparrillo.gamepulse.UserProfile
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface XboxLiveApiService {
    @GET("profile/{xuid}")
    suspend fun getUserProfile(
        @Header("Authorization") accessToken: String,
        @Path("xuid") xuid: String
    ): Response<UserProfile>

    @GET("achievements/{gameId}")
    suspend fun getGameAchievements(
        @Header("Authorization") accessToken: String,
        @Path("gameId") gameId: String
    ): Response<List<Achievement>>

    // Add other API endpoints as needed
}
