package com.scottparrillo.gamepulse

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // autoGenerate for id
    val iconResId: Int,
    val title: String,
    val description: String,
    val percentageEarned: Double,
    val isEarned: Boolean,
    var progress: Int,
    val total: Int,
    val soundResId: Int?
)
