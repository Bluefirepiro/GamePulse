package com.scottparrillo.gamepulse

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class Achievement(
    val iconResId: Int,
    val title: String,
    val description: String,
    var percentageEarned: Double,
    val isEarned: Boolean,
    var progress: Int,
    val total: Int,
    val soundResId: Int?,
    var isFavorite: Boolean = false
) : Serializable

class AchievementActivity : AppCompatActivity() {

    private lateinit var achievementAdapter: AchievementAdapter
    private val achievements = mutableListOf<Achievement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAchievements)
        recyclerView.layoutManager = LinearLayoutManager(this)
        achievementAdapter = AchievementAdapter(achievements,
            { position ->
                showConfirmDeleteDialog(position)
            },
            { position ->
                val achievement = achievements[position]
                achievement.isFavorite = !achievement.isFavorite
                achievementAdapter.notifyItemChanged(position)
            }
        )
        recyclerView.adapter = achievementAdapter

        // Search functionality
        val searchEditText: EditText = findViewById(R.id.search)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAchievements(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Load initial achievements from file
     //   loadAchievementsFromApi()

        // Set up the "Add Achievement" button
        val buttonAddAchievement: Button = findViewById(R.id.buttonAddAchievement)
        buttonAddAchievement.setOnClickListener {
            showAddAchievementDialog()
        }

        // Set up the sorting dropdown menu
        val sortButton: Button = findViewById(R.id.buttonSortOptions)
        sortButton.setOnClickListener { view ->
            showSortMenu(view)
        }

        // Create the notification channel
        createNotificationChannel()
    }
/*
    private fun loadAchievementsFromApi() {
        lifecycleScope.launch {
            try {
                val gameId = "someGameId" // Replace with actual game ID
                val response = XboxLiveApiClient.apiService.getGameDetails(gameId)

                // Assuming the response contains achievement details
                val fetchedAchievements = response.achievements.map { achievement ->
                    Achievement(
                        iconResId = R.drawable.ic_achievement, // Replace with appropriate icon logic
                        title = achievement.title,
                        description = achievement.description,
                        percentageEarned = achievement.percentageEarned,
                        isEarned = achievement.isEarned,
                        progress = achievement.progress,
                        total = achievement.total,
                        soundResId = null // Customize sound if needed
                    )
                }

                updateAchievementsList(fetchedAchievements)
            } catch (e: Exception) {
                // Handle exceptions, e.g., network issues
                e.printStackTrace()
            }
        }
    }

 */


    private fun showAddAchievementDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_achievement, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val editTextProgress = dialogView.findViewById<EditText>(R.id.editTextProgress)
        val editTextTotal = dialogView.findViewById<EditText>(R.id.editTextTotal)
        val spinnerSound = dialogView.findViewById<Spinner>(R.id.spinnerSound)

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

        val newAchievement = Achievement(
            R.drawable.ic_achievement,
            title,
            description,
            percentageEarned,
            isEarned,
            progress,
            total,
            soundResId
        )

        achievements.add(newAchievement)
        achievementAdapter.notifyItemInserted(achievements.size - 1)
        saveAchievementFile(achievements)

        if (isEarned) {
            playSound(this, soundResId ?: R.raw.achievement_completed)
            sendAchievementNotification(this, title, "Congratulations! You've completed an achievement.", soundResId)
        }
    }

    private fun showConfirmDeleteDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Achievement")
            .setMessage("Are you sure you want to delete this achievement?")
            .setPositiveButton("Yes") { _, _ ->
                achievementAdapter.removeItem(position)
                saveAchievementFile(achievements)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showSortMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menuInflater: MenuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.sort_alphabetically -> sortAchievementsAlphabetically()
                R.id.sort_percentage_earned -> sortAchievementsByPercentageEarned()
                R.id.sort_earned_true -> sortAchievementsByEarnedStatus(true)
                R.id.sort_earned_false -> sortAchievementsByEarnedStatus(false)
                R.id.sort_favorites -> sortAchievementsByFavorites()
            }
            true
        }
        popupMenu.show()
    }

    private fun sortAchievementsAlphabetically() {
        val sortedList = achievements.sortedBy { it.title }
        updateAchievementsList(sortedList)
    }

    private fun sortAchievementsByPercentageEarned() {
        val sortedList = achievements.sortedByDescending { it.percentageEarned }
        updateAchievementsList(sortedList)
    }

    private fun sortAchievementsByEarnedStatus(earned: Boolean) {
        val sortedList = achievements.sortedByDescending { it.isEarned == earned }
        updateAchievementsList(sortedList)
    }

    private fun sortAchievementsByFavorites() {
        val sortedList = achievements.sortedByDescending { it.isFavorite }
        updateAchievementsList(sortedList)
    }

    private fun updateAchievementsList(newList: List<Achievement>) {
        val diffCallback = AchievementDiffCallback(achievements, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        achievements.clear()
        achievements.addAll(newList)
        diffResult.dispatchUpdatesTo(achievementAdapter)
    }


    private fun filterAchievements(query: String) {
        val filteredList = achievements.filter { it.title.contains(query, ignoreCase = true) }
        achievementAdapter.updateList(filteredList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Achievement Channel"
            val descriptionText = "Channel for Achievement Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("achievement_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendAchievementNotification(context: Context, title: String, message: String, soundResId: Int?) {
        val notificationId = 1
        val channelId = "achievement_channel"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_achievement)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        soundResId?.let {
            val soundUri = Uri.parse("android.resource://${context.packageName}/$it")
            notificationBuilder.setSound(soundUri)
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }

    private fun saveAchievementFile(achievementList: MutableList<Achievement>): Boolean {
        return try {
            val fos = openFileOutput("achievements", MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(achievementList)
            oos.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun getAchievementFile(): MutableList<Achievement>? {
        return try {
            val fis = openFileInput("achievements")
            val ois = ObjectInputStream(fis)
            @Suppress("UNCHECKED_CAST")
            val achievementList = ois.readObject() as? MutableList<Achievement>
            ois.close()
            achievementList
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
