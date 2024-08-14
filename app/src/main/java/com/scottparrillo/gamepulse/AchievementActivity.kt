package com.scottparrillo.gamepulse

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class Achievement(
    val iconResId: Int,
    val title: String,
    val description: String,
    val percentageEarned: Double,
    val isEarned: Boolean,
    var progress: Int,
    val total: Int,
    val soundResId: Int?
) : Serializable

class AchievementActivity : AppCompatActivity() {

    private lateinit var achievementAdapter: AchievementAdapter
    private val achievements = mutableListOf<Achievement>()

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
        achievementAdapter = AchievementAdapter(achievements) { position ->
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

        // Load initial achievements from file
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

        // Create the notification channel
        createNotificationChannel()
    }

    private fun loadAchievements() {
        val savedAchievements = getAchievementFile()
        val newAchievements = if (savedAchievements != null) {
            savedAchievements
        } else {
            listOf(
                Achievement(R.drawable.ic_achievement, "Achievement 1", "Description 1", 50.0, true, 500, 1000, R.raw.sound1),
                Achievement(R.drawable.ic_achievement, "Achievement 2", "Description 2", 30.0, false, 200, 500, R.raw.sound2),
                Achievement(R.drawable.ic_achievement, "Achievement 3", "Description 3", 75.0, true, 1000, 1000, null)
            )
        }

        achievementAdapter.updateList(newAchievements)
    }


    private fun showAddAchievementDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_achievement, null)
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

    private fun addAchievement(
        title: String,
        description: String,
        progress: Int,
        total: Int,
        soundResId: Int?
    ) {
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

        // Play custom sound and send notification if achievement is completed
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

    private fun updateAchievementsList(newList: List<Achievement>) {
        val diffCallback = AchievementDiffCallback(achievements, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        achievements.clear()
        achievements.addAll(newList)
        diffResult.dispatchUpdatesTo(achievementAdapter)
    }

    // DiffUtil Callback class remains the same
    class AchievementDiffCallback(
        private val oldList: List<Achievement>,
        private val newList: List<Achievement>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].title == newList[newItemPosition].title
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }


    private fun filterAchievements(query: String) {
        val filteredList = achievements.filter { it.title.contains(query, ignoreCase = true) }
        achievementAdapter.updateList(filteredList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Handle back button
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

        // Creating the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Achievement Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)

            // Set custom sound for the channel if available
            soundResId?.let {
                val soundUri = Uri.parse("android.resource://${context.packageName}/$it")
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                notificationChannel.setSound(soundUri, audioAttributes)
            }

            // Get the NotificationManager from context
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_achievement) // Replace with your actual icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        // Set custom sound for the notification if available
        soundResId?.let {
            val soundUri = Uri.parse("android.resource://${context.packageName}/$it")
            notificationBuilder.setSound(soundUri)
        }

        // Use NotificationManagerCompat to show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }


    private fun showAchievementNotification(context: Context, title: String, message: String, soundUri: Uri) {
        val notificationId = 1
        val channelId = "achievement_channel"

        // Creating the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Achievement Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                setSound(soundUri, null) // Set custom sound here
            }

            // Use context to get the NotificationManager
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_achievement) // Replace with your icon
            .setContentTitle(title)
            .setContentText(message)
            .setSound(soundUri)
            .setAutoCancel(true)

        // Use NotificationManagerCompat to show the notification
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    }


    // Saving achievements to a file
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

    // Loading achievements from a file
    private fun getAchievementFile(): MutableList<Achievement>? {
        return try {
            val fis = openFileInput("achievements")
            val ois = ObjectInputStream(fis)
            @Suppress("UNCHECKED_CAST") // Suppress unchecked cast warning
            val achievementList = ois.readObject() as? MutableList<Achievement>
            ois.close()
            achievementList
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

