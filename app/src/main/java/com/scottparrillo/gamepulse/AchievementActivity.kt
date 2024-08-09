package com.scottparrillo.gamepulse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AchievementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        // Set up the toolbar with a back button
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enables the back button in the toolbar

        // Set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAchievements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AchievementAdapter(getAchievements())
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Handle back button press to go back to the previous screen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
