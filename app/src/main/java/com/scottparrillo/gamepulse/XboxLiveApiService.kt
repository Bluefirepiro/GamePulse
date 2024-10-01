package com.scottparrillo.gamepulse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface XboxWebAPIClient {

    // Function to get achievements for a specific game and user
    @GET("/xbox/users/{xuid}/achievements")
    fun getUserAchievements(
        @Path("xuid") xuid: String,
        @Query("titleId") titleId: String
    ): Call<XboxPlayerAchievements>

    // Function to get details of a game using titleId
    @GET("/xbox/titles/{titleId}")
    fun getGameDetails(
        @Path("titleId") titleId: String
    ): Call<XboxGameDetails>

    // Function to get recently played games by a user
    @GET("/xbox/users/{xuid}/recentlyplayed")
    fun getRecentlyPlayedGames(
        @Path("xuid") xuid: String
    ): Call<XboxRecentlyPlayedGames>
}
