package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse

data class XuidResponse(
    val people: List<Person>
)

data class Person(
    val xuid: String,
    val gamertag: String
)
