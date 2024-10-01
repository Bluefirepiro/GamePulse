package com.scottparrillo.gamepulse

import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.SteamPlayerAchievements
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamWebAPIClient {
//This function gets the achivment names and percentage of people that have them. It takes in a api key a game id and then a format which we just set to json
@GET("/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/")
fun getAllAchievementPercentages(@Query("key") key: String, @Query("gameid") gameid:Long, @Query("format") format:String): Call<SteamAchievementPercentages>
//This function returns all owned games in an array given that the user's steam profile is public and you have their steam id
@GET("/IPlayerService/GetOwnedGames/v0001/")
fun getAllOwnedGames(@Query("key") key: String,@Query("include_appinfo") include_appinfo:Boolean, @Query("steamid") steamid:Long,
                     @Query("format") format:String): Call<SteamOwnedGames>
//Given the game and steam user Id this will return the api name of the achievement 0 for not gotten 1 for gotten and unlock time
/*
EXAMPLE API RETURN IN JSON
playerstats	{
steamID	"76561198064427137"
gameName	"Team Fortress 2"
achievements{
0
apiname	"TF_PLAY_GAME_EVERYCLASS"
achieved	0
unlocktime	0
1
apiname	"TF_PLAY_GAME_EVERYMAP"
achieved	0
unlocktime	0
2
apiname	"TF_GET_HEALPOINTS"
achieved	0
unlocktime	0
3
apiname	"TF_BURN_PLAYERSINMINIMUMTIME"
achieved	1
unlocktime	1347574046
}
}
 */
    @GET("/ISteamUserStats/GetPlayerAchievements/v0001/")
    fun getAllGameAchievements(@Query("appid") appid: Long,@Query("key") key: String, @Query("steamid") steamid:Long)
                                                    : Call<SteamPlayerAchievements>
}
