package com.scottparrillo.gamepulse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamWebAPIClient {
//This function gets the achivment names and percentage of people that have them. It takes in a api key a game id and then a format which we just set to json
@GET("/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/")
fun getAllAchievementPercentages(@Query("key") key: String, @Query("gameid") gameid:Int, @Query("format") format:String): Call<SteamAchievementPercentages>
//This function returns all owned games in an array given that the user's steam profile is public and you have their steam id
    @GET("/IPlayerService/GetOwnedGames/v0001/")
    fun getAllOwnedGames(@Query("key") key: String,@Query("include_appinfo") include_appinfo:Boolean, @Query("steamid") steamid:Long,
                         @Query("format") format:String): Call<SteamOwnedGames>
}
