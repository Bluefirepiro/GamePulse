package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class SteamPlayerAchievements {
    @SerializedName("playerstats")
    val playerstats: Playerstats? = null

    class Playerstats {
        @SerializedName("steamID")
        val steamID: Long = 0

        @SerializedName("gameName")
        val gameName: String = ""

        @SerializedName("achievements")
        val achievements = listOf<SteamAchievement>()

        class SteamAchievement {
            @SerializedName("apiname")
            val apiname: String = ""

            @SerializedName("achieved")
            val achieved: Int = 0

            @SerializedName("unlocktime")
            val unlocktime: Long = 0
        }
    }
}
