package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class SteamGameSchema {
    @SerializedName("game")
    val game: GameSteam? = null
    class GameSteam{
        @SerializedName("gameName")
        val gameName = ""
        @SerializedName("gameVersion")
        val gameVersion = ""
        @SerializedName("availableGameStats")
        val availableGameStats: AvailableGameStats? = null
        class AvailableGameStats{
            @SerializedName("achievements")
            val achievements = listOf<Achievements>()
            class Achievements{
                @SerializedName("name")
                val name:String = ""
                @SerializedName("displayName")
                val displayName:String = ""
                @SerializedName("description")
                val description:String = ""
                @SerializedName("icon")
                val icon:String = ""
                @SerializedName("icongray")
                val icongray:String = ""
            }
        }
    }



}