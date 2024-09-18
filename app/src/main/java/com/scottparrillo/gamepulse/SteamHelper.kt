package com.scottparrillo.gamepulse

object SteamHelper {
    //"/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?key=4A7BFC2A3443A093EA9953FD5529C795&gameid=870780&format=json"
    //A lot of steam api calls require id input so this is just a way to build them
    fun SteamGameIdStringBuilder(number: Int): String{
       var toReturn = "/?key=4A7BFC2A3443A093EA9953FD5529C795&gameid="
        val id = number.toString()
        toReturn = toReturn.plus(id)
        val addAtEnd = "&format=json"
        toReturn = toReturn.plus(addAtEnd)
        return toReturn
    }
}