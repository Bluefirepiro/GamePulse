package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XboxPlayerAchievements {
    @SerializedName("achievements")
    val achievements: List<XboxAchievement> = listOf()

    class XboxAchievement {
        @SerializedName("name")
        val name: String = ""

        @SerializedName("unlocked")
        val unlocked: Boolean = false

        @SerializedName("unlockTime")
        val unlockTime: Long = 0
    }
}
