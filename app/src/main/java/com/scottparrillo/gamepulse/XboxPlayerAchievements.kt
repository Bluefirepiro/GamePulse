package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XboxPlayerAchievements {
    @SerializedName("achievements")
    val achievements: List<XboxAchievement> = listOf()

    class XboxAchievement {
        @SerializedName("id")
        val id: String = ""

        @SerializedName("name")
        val name: String = ""

        @SerializedName("description")
        val description: String = ""

        @SerializedName("unlocked")
        val unlocked: Boolean = false

        @SerializedName("unlockTime")
        val unlockTime: Long = 0

        @SerializedName("progressState")
        val progressState: String = ""

        @SerializedName("progression")
        val progression: Progression? = null

        @SerializedName("mediaAssets")
        val mediaAssets: List<MediaAsset> = listOf()

        class Progression {
            @SerializedName("current")
            val current: Int = 0

            @SerializedName("total")
            val total: Int = 0
        }

        class MediaAsset {
            @SerializedName("name")
            val name: String = ""

            @SerializedName("url")
            val url: String = ""
        }
    }
}
