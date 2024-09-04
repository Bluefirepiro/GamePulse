package com.scottparrillo.gamepulse

import retrofit2.Call
import retrofit2.http.GET

interface SteamWebAPIClient {
@GET("/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?gameid=870780&format=json")
fun getAllAchievementPercentages(): Call<SteamAchievementPercentages>
}
