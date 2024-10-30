package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse

import com.google.gson.annotations.SerializedName

class XuidResponse {
    @SerializedName("people")
    val people: List<Person> = listOf()

    class Person {
        @SerializedName("xuid")
        val xuid: String = ""

        @SerializedName("gamertag")
        val gamertag: String = ""  // Keep this active, even if it's not used.
    }
}
