package com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XboxGameDetails {
    @SerializedName("xuid")
    val titleId: String = ""
    @SerializedName("titles")
    val titles = listOf<Titles>()
            class Titles{
                @SerializedName("titleId")
                val titleId: String = ""
                @SerializedName("name")
                val name: String = ""
            }
}
