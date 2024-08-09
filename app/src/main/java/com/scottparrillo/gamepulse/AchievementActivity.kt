package com.scottparrillo.gamepulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AchievementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAchievements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AchievementAdapter(getAchievements())
    }

    // Sample data for achievements
    private fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement(R.drawable.ic_achievement, "Achievement 1", "Description 1"),
            Achievement(R.drawable.ic_achievement, "Achievement 2", "Description 2"),
            Achievement(R.drawable.ic_achievement, "Achievement 3", "Description 3")
        )
    }
}

data class Achievement(val iconResId: Int, val title: String, val description: String)
