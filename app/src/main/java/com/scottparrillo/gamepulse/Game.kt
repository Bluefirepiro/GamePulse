package com.scottparrillo.gamepulse


import java.io.Serializable

class Game: Serializable {
    var completed: Boolean = false
    var allAchiev: Boolean = false
    var isFavorite: Boolean = false // Add this property
    var gameTime: Float = 0f
    var timeToBeat: Float = 0f
    var gameId: Int = 0
    var gamePlatform: String = ""
    var coverURL: String = ""
    var gameName: String = ""
    var gameDescription: String = ""
    var gameGenre: String = ""
    var recentlyPlayed: Boolean = false
    var currentlyPlaying: Boolean = false
    var newlyAdded: Boolean = false
    var gameReleaseDate: String = ""
    //Games have multiple achievements so just adding a list so we can iter through them
    var achievements: ArrayList<Achievement>? = null
    //Global list object
    companion object{
        var gameList = mutableListOf<Game>()

    }

}
