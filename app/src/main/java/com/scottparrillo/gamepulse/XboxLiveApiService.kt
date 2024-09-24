package com.scottparrillo.gamepulse.api

import com.scottparrillo.gamepulse.AchievementResponse
import com.scottparrillo.gamepulse.GameDetailsResponse
import com.scottparrillo.gamepulse.util.Constants.OPENXBL_API_KEY
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.Call

interface OpenXBLApiService {

    // Get Xbox Profile Information
    @GET("profile")
    fun getProfile(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY
    ): Call<ProfileResponse>

    // Get achievements for a specific game
    @GET("achievements/{xuid}/{titleId}")
    fun getAchievements(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String,
        @Path("titleId") titleId: String
    ): Call<AchievementResponse>

    // Get recent achievements for a user
    @GET("achievements/recent/{xuid}")
    fun getRecentAchievements(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Call<List<AchievementResponse>>

    // Get recently played games for a user
    @GET("recentlyplayed/{xuid}")
    fun getRecentlyPlayedGames(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Call<List<GameDetailsResponse>>
}
