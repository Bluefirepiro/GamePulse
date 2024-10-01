package com.scottparrillo.gamepulse

import java.io.Serializable

data class AchievementResponse(
    val title: String,
    val description: String,
    val percentageEarned: Double,
    val isEarned: Boolean,
    val progress: Int,
    val total: Int
) : Serializable

data class GameDetailsResponse(
    val gameId: String,
    val gameName: String,
    val gameDescription: String,
    val platform: String,
    val releaseDate: String,
    val developer: String,
    val publisher: String,
    val genres: List<String>,
    val coverImageUrl: String,
    val achievements: List<AchievementResponse> // Add this to include achievements
) : Serializable

data class ProfileResponse(
    val id: String,
    val name: String,
    val avatarUrl: String
) : Serializable
