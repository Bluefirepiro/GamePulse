package com.scottparrillo.gamepulse

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

data class Achievement(
    var id: Long? = null, // Nullable ID for new entries
    val iconResId: Int,
    val title: String,
    val description: String,
    val percentageEarned: Double,  // Percentage of players who earned this achievement
    val isEarned: Boolean,         // Whether the current user has earned this achievement
    var progress: Int,             // Current progress
    val total: Int,                // Total required to earn the achievement
    val soundResId: Int?           // Nullable field for sound resource ID
)


class AchievementActivity : AppCompatActivity() {

    private lateinit var achievementAdapter: AchievementAdapter
    private val achievements = mutableListOf<Achievement>()
    private lateinit var achievementDao: AchievementDao
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        // Initialize Room database
        db = AppDatabase.getDatabase(this)
        achievementDao = db.achievementDao()

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAchievements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        achievementAdapter = AchievementAdapter(achievements) { position ->
            // Handle delete button click
            showConfirmDeleteDialog(position)
        }
        recyclerView.adapter = achievementAdapter

        // Search functionality
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAchievements(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Load initial achievements
        loadAchievements()

        // Set up the "Add Achievement" button
        val buttonAddAchievement: Button = findViewById(R.id.buttonAddAchievement)
        buttonAddAchievement.setOnClickListener {
            showAddAchievementDialog()
        }

        // Set up the spinner for sorting options
        val spinnerSortOptions: Spinner = findViewById(R.id.spinnerSortOptions)
        spinnerSortOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortAchievementsAlphabetically()
                    1 -> sortAchievementsByPercentageEarned()
                    2 -> sortAchievementsByEarnedStatus(true)
                    3 -> sortAchievementsByEarnedStatus(false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadAchievements() {
        lifecycleScope.launch {
            val loadedAchievements = achievementDao.getAllAchievements()
            achievements.clear()
            achievements.addAll(loadedAchievements.map {
                Achievement(
                    id = it.id,
                    iconResId = it.iconResId,
                    title = it.title,
                    description = it.description,
                    percentageEarned = it.percentageEarned,
                    isEarned = it.isEarned,
                    progress = it.progress,
                    total = it.total,
                    soundResId = it.soundResId
                )
            })
            achievementAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddAchievementDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_achievement, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val editTextProgress = dialogView.findViewById<EditText>(R.id.editTextProgress)
        val editTextTotal = dialogView.findViewById<EditText>(R.id.editTextTotal)
        val spinnerSound = dialogView.findViewById<Spinner>(R.id.spinnerSound)

        // Create an array of sound options
        val soundOptions = listOf("Select Sound", "Sound1", "Sound2")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, soundOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSound.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val progress = editTextProgress.text.toString().toIntOrNull() ?: 0
            val total = editTextTotal.text.toString().toIntOrNull() ?: 1
            val selectedSound = spinnerSound.selectedItem.toString()

            val soundResId = when (selectedSound) {
                "Sound1" -> R.raw.sound1
                "Sound2" -> R.raw.sound2
                else -> null
            }

            if (title.isNotEmpty() && description.isNotEmpty()) {
                addAchievement(title, description, progress, total, soundResId)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addAchievement(title: String, description: String, progress: Int, total: Int, soundResId: Int?) {
        val percentageEarned = if (total > 0) (progress.toDouble() / total) * 100 else 0.0
        val isEarned = progress >= total

        // Create a new Achievement instance
        val newAchievement = Achievement(
            iconResId = R.drawable.ic_achievement,
            title = title,
            description = description,
            percentageEarned = percentageEarned,
            isEarned = isEarned,
            progress = progress,
            total = total,
            soundResId = soundResId
        )

        lifecycleScope.launch {
            val newEntity = AchievementEntity(
                iconResId = newAchievement.iconResId,
                title = newAchievement.title,
                description = newAchievement.description,
                percentageEarned = newAchievement.percentageEarned,
                isEarned = newAchievement.isEarned,
                progress = newAchievement.progress,
                total = newAchievement.total,
                soundResId = newAchievement.soundResId
            )
            // Insert the entity and get the ID of the newly inserted record
            val id = achievementDao.insertAchievement(newEntity)
            // Update the id of the Achievement instance
            newAchievement.id = id
            // Add the new Achievement to the list
            achievements.add(newAchievement)
            achievementAdapter.notifyItemInserted(achievements.size - 1)
        }
    }



    private fun showConfirmDeleteDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Achievement")
            .setMessage("Are you sure you want to delete this achievement?")
            .setPositiveButton("Yes") { _, _ ->
                val achievementToDelete = achievements[position]
                lifecycleScope.launch {
                    // Only call deleteAchievement if id is not null
                    achievementToDelete.id?.let { id ->
                        achievementDao.deleteAchievement(id)
                        achievements.removeAt(position)
                        achievementAdapter.notifyItemRemoved(position)
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }



    private fun sortAchievementsAlphabetically() {
        achievements.sortBy { it.title }
        achievementAdapter.notifyDataSetChanged()
    }

    private fun sortAchievementsByPercentageEarned() {
        achievements.sortByDescending { it.percentageEarned }
        achievementAdapter.notifyDataSetChanged()
    }

    private fun sortAchievementsByEarnedStatus(earned: Boolean) {
        achievements.sortByDescending { it.isEarned == earned }
        achievementAdapter.notifyDataSetChanged()
    }

    private fun filterAchievements(query: String) {
        val filteredList = achievements.filter { it.title.contains(query, ignoreCase = true) }
        achievementAdapter.updateList(filteredList)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Handle back button
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun playSound(context: Context, soundResId: Int) {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
    }
}
