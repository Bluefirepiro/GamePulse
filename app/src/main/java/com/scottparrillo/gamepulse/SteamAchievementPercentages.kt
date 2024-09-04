package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class SteamAchievementPercentages {
    @SerializedName("achievementpercentages")
    val achievementpercentages: AchievementPercentages? = null
    class AchievementPercentages {
        @SerializedName("achievements")
        val achievements = listOf<Achievements>()
        class Achievements{
            @SerializedName("name")
            val name:String = ""
            @SerializedName("percent")
            val percent:Float = 0.0f
        }
    }
}
