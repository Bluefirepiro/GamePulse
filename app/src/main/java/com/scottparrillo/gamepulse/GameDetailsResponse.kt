package com.scottparrillo.gamepulse

data class GameDetailsResponse(
    val id: String,                // The unique ID of the game
    val name: String,              // The name of the game
    val description: String?,      // A brief description of the game
    val releaseDate: String?,      // The release date of the game
    val platform: String?,         // The platform the game is available on
    val developer: String?,        // The developer of the game
    val publisher: String?,        // The publisher of the game
    val genre: List<String>?,      // A list of genres the game belongs to
    val coverImageUrl: String?,    // URL to the cover image of the game
    val achievements: List<Achievement>? // A list of achievements for the game (if available)
)
