package com.scottparrillo.gamepulse

class GameManager {

    private val recentlyPlayedGames = mutableListOf<Game>()

    // Add a game to the recently played list
    fun addRecentlyPlayedGame(game: Game) {
        // Maintain a maximum limit for the list if needed
        if (recentlyPlayedGames.size >= MAX_RECENTLY_PLAYED) {
            recentlyPlayedGames.removeAt(0)
        }
        recentlyPlayedGames.add(game)
    }

    // Get the list of recently played games
    fun getRecentlyPlayedGames(): List<Game> {
        return recentlyPlayedGames
    }

    companion object {
        private const val MAX_RECENTLY_PLAYED = 10
    }
}
