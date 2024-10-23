package com.scottparrillo.gamepulse

import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.XuidResponse
import com.scottparrillo.gamepulse.util.Constants.OPENXBL_API_KEY
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface XboxWebAPIClient {


    // Get Xbox Profile Information
    @GET("profile")
    fun getProfile(
        @Header("Authorization") accessToken: String
    ): Call<ProfileResponse>

    // Get achievements for a specific game and user
    @GET("/xbox/users/{xuid}/achievements")
    fun getUserAchievements(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String,
        @Query("titleId") titleId: String
    ): Call<XboxPlayerAchievements>

    // Get details of a game using titleId
    @GET("/xbox/titles/{titleId}")
    fun getGameDetails(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("titleId") titleId: String
    ): Call<XboxOwnedGames>

    // Get recently played games by a user
    @GET("/xbox/users/{xuid}/recentlyplayed")
    suspend fun getRecentlyPlayedGames(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Response<XboxRecentlyPlayedGames>

    // Get recent achievements for a user
    @GET("achievements/recent/{xuid}")
    suspend fun getRecentAchievements(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Response<List<AchievementResponse>>

    // Get achievements for a specific game
    @GET("achievements/{xuid}/{titleId}")
    fun getAchievements(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String,
        @Path("titleId") titleId: String
    ): Call<AchievementResponse>

    // Get recently played games (as an alternative to /xbox/users/{xuid}/recentlyplayed)
    @GET("recentlyplayed/{xuid}")
    fun getRecentlyPlayedGamesAlternative(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Call<List<GameDetailsResponse>>

    @GET("player/titleHistory/{xuid}")
    fun getAllGamesByID(
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY,
        @Path("xuid") xuid: String
    ): Call<XboxOwnedGames>

    @GET("v2/search/{gamertag}")
    fun getXuidFromGamertag(
        @Path("gamertag") gamertag: String,
        @Header("X-Authorization") apiKey: String = OPENXBL_API_KEY
    ): Call<XuidResponse>  // Change to Call<XuidResponse>

}
