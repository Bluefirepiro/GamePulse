package com.scottparrillo.gamepulse

import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.SteamGameSchema
import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.SteamPlayerAchievements
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamWebAPIClient {
//This function gets the achivment names and percentage of people that have them. It takes in a api key a game id and then a format which we just set to json
@GET("/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/")
//fun getAllAchievementPercentages(@Query("key") key: String, @Query("gameid") gameid:Long, @Query("format") format:String): Call<SteamAchievementPercentages>
fun getAllAchievementPercentages(@Query("gameid") gameid:Long, @Query("format") format:String): Call<SteamAchievementPercentages>
//This function returns all owned games in an array given that the user's steam profile is public and you have their steam id
@GET("/IPlayerService/GetOwnedGames/v0001/")
fun getAllOwnedGames(@Query("key") key: String,@Query("include_appinfo") include_appinfo:Boolean, @Query("steamid") steamid:Long,
                     @Query("format") format:String): Call<SteamOwnedGames>
//Given the game and steam user Id this will return the api name of the achievement 0 for not gotten 1 for gotten and unlock time
    @GET("/ISteamUserStats/GetPlayerAchievements/v0001/")
    fun getAllGameAchievements(@Query("appid") appid: Long,@Query("key") key: String, @Query("steamid") steamid:Long)
                                                    : Call<SteamPlayerAchievements>
    @GET("/ISteamUserStats/GetSchemaForGame/v2/")
    fun getGameSchema(@Query("key") key: String, @Query("appid") appid: Long)
            : Call<SteamGameSchema>
}
