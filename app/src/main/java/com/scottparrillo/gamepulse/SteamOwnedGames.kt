package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class SteamOwnedGames {
    @SerializedName("response")
val response: Response? = null
    class Response{
        @SerializedName("game_count")
        val game_count:Long = 0
        @SerializedName("games")
        val games = listOf<SteamGames>()
        class SteamGames{
            @SerializedName("appid")
            val appid:Long = 0
            @SerializedName("name")
            val name:String = ""
            @SerializedName("playtime_forever")
            val playtime_forever:Long = 0
            @SerializedName("img_icon_url")
            val img_icon_url:String = ""
            @SerializedName("has_community_visible_stats")
            val has_community_visible_stats: Boolean = false
            @SerializedName("playtime_windows_forever")
            val playtime_windows_forever: Long = 0
            @SerializedName("playtime_mac_forever")
            val playtime_mac_forever: Int = 0
            @SerializedName("playtime_linux_forever")
            val playtime_linux_forever: Int = 0
            @SerializedName("playtime_deck_forever")
            val playtime_deck_forever: Int = 0
            @SerializedName("rtime_last_played")
            val rtime_last_played: Long = 0
        }
    }
}