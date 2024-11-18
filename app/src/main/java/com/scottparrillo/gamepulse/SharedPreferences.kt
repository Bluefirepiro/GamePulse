package com.scottparrillo.gamepulse.com.scottparrillo.gamepulse

/*import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scottparrillo.gamepulse.Game

fun saveRecentGames(context: Context, games: List<Game>) {
    val sharedPreferences = context.getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(games)
    editor.putString("recentlyPlayedGames", json)
    editor.apply()
}

fun loadRecentGames(context: Context): List<Game> {
    val sharedPreferences = context.getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("recentlyPlayedGames", null)
    return if (json != null) {
        val type = object : TypeToken<List<Game>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}*/
