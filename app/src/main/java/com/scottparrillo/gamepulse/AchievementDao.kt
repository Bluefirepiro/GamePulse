package com.scottparrillo.gamepulse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    suspend fun getAllAchievements(): List<AchievementEntity>

    @Insert
    suspend fun insertAchievement(achievement: AchievementEntity): Long  // Return Long

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("DELETE FROM achievements WHERE id = :id")
    suspend fun deleteAchievement(id: Long)
}

