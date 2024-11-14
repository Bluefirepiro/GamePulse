package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scottparrillo.gamepulse.Game
import com.scottparrillo.gamepulse.Achievement

object PreferencesUtil {

    private const val PREFS_NAME = "GamePulsePrefs"
    private const val KEY_RECENT_GAMES = "recentlyPlayedGames"
    private const val KEY_RECENT_ACHIEVEMENTS = "recentAchievements"
    private val gson = Gson()

    fun saveDataToPreferences(context: Context, games: List<Game>, achievements: List<Achievement>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gamesJson = gson.toJson(games)
        val achievementsJson = gson.toJson(achievements)

        editor.putString(KEY_RECENT_GAMES, gamesJson)
        editor.putString(KEY_RECENT_ACHIEVEMENTS, achievementsJson)
        editor.apply()
    }

    fun loadGamesFromPreferences(context: Context): List<Game> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gamesJson = sharedPreferences.getString(KEY_RECENT_GAMES, null)
        return if (gamesJson != null) {
            val type = object : TypeToken<List<Game>>() {}.type
            gson.fromJson(gamesJson, type)
        } else {
            emptyList()
        }
    }

    fun loadAchievementsFromPreferences(context: Context): List<Achievement> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val achievementsJson = sharedPreferences.getString(KEY_RECENT_ACHIEVEMENTS, null)
        return if (achievementsJson != null) {
            val type = object : TypeToken<List<Achievement>>() {}.type
            gson.fromJson(achievementsJson, type)
        } else {
            emptyList()
        }
    }
}
