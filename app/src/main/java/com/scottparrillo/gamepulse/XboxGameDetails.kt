package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XboxOwnedGames {
    @SerializedName("response")
    val response: Response? = null

    class Response {
        @SerializedName("game_count")
        val game_count: Long = 0

        @SerializedName("games")
        val games = listOf<XboxGame>()

        class XboxGame {
            @SerializedName("titleId")
            val titleId: String = ""

            @SerializedName("pfn")
            val pfn: String = ""

            @SerializedName("serviceConfigId")
            val serviceConfigId: String = ""

            @SerializedName("windowsPhoneProductId")
            val windowsPhoneProductId: String? = null

            @SerializedName("name")
            val name: String = ""

            @SerializedName("type")
            val type: String = ""

            @SerializedName("devices")
            val devices: List<String> = listOf()

            @SerializedName("displayImage")
            val displayImage: String = ""

            @SerializedName("mediaItemType")
            val mediaItemType: String = ""

            @SerializedName("modernTitleId")
            val modernTitleId: String = ""

            @SerializedName("isBundle")
            val isBundle: Boolean = false

            @SerializedName("achievement")
            val achievement: Achievement? = null

            @SerializedName("stats")
            val stats: Stats? = null

            @SerializedName("gamePass")
            val gamePass: GamePass? = null

            @SerializedName("titleHistory")
            val titleHistory: TitleHistory? = null

            class Achievement {
                @SerializedName("currentAchievements")
                val currentAchievements: Int = 0

                @SerializedName("totalAchievements")
                val totalAchievements: Int = 0

                @SerializedName("currentGamerscore")
                val currentGamerscore: Int = 0

                @SerializedName("totalGamerscore")
                val totalGamerscore: Int = 0

                @SerializedName("progressPercentage")
                val progressPercentage: Int = 0

                @SerializedName("sourceVersion")
                val sourceVersion: Int = 0
            }

            class Stats {
                @SerializedName("sourceVersion")
                val sourceVersion: Int = 0
            }

            class GamePass {
                @SerializedName("isGamePass")
                val isGamePass: Boolean = false
            }

            class TitleHistory {
                @SerializedName("lastTimePlayed")
                val lastTimePlayed: String = ""

                @SerializedName("visible")
                val visible: Boolean = false

                @SerializedName("canHide")
                val canHide: Boolean = false
            }
        }
    }
}
