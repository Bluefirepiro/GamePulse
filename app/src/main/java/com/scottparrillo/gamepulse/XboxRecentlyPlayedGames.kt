package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XboxRecentlyPlayedGames {
    @SerializedName("games")
    val games: List<Game> = listOf()

    class Game {
        @SerializedName("titleId")
        val titleId: String = ""

        @SerializedName("name")
        val name: String = ""

        @SerializedName("lastPlayed")
        val lastPlayed: Long = 0
    }
}
