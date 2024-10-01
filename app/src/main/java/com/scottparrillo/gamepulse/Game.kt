package com.scottparrillo.gamepulse


import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDateTime

class Game: Serializable {
    var completed: Boolean = false
    var allAchiev: Boolean = false
    var isFavorite: Boolean = false // Add this property
    var gameTime: Float = 0f
    var timeToBeat: Float = 0f
    var gameId: Long = 0
    var gamePlatform: String = ""
    var coverURL: String = ""
    var gameName: String = ""
    var gameDescription: String = ""
    var gameGenre: String = ""
    var recentlyPlayed: Boolean = false
    var currentlyPlaying: Boolean = false
    var newlyAdded: Boolean = false
    var gameReleaseDate: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    //Just setting it to now till it's over written
    var dateTimeLastPlayed:LocalDateTime = LocalDateTime.now()
    //Games have multiple achievements so just adding a list so we can iter through them
    var achievements = mutableListOf<Achievement>()
    //Global list object
    companion object{
        var gameList = mutableListOf<Game>()
        var selectedGame = Game()

    }

}
