package com.scottparrillo.gamepulse

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.scottparrillo.gamepulse.api.ApiClient
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {


    private val apiService = ApiClient.openXBL.xboxWebAPIClient
    var xboxIdText by mutableStateOf("")


    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    // Define a list to hold recently played games
    private val recentlyPlayedGamesList = mutableStateListOf<Game>()

    // Define a List to hold recently achieved achievements
    private val recentlyAchievedAchievementsList = mutableStateListOf<Achievement>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the permission request launcher
        requestNotificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted, attempt to send notification
                    sendNotificationIfAllowed()
                } else {
                    // Permission denied
                    println("Notification permission denied.")
                }
            }

        // Request notification permission if needed
        if (shouldRequestNotificationPermission()) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            GamePulseTheme {
                HomeScreen(

                    recentlyPlayedGames = recentlyPlayedGamesList,
                    recentlyAchievedAchievements = recentlyAchievedAchievementsList,
/*
                    onGamePlayed = { game ->
                        // Handle the game that was played, e.g., navigate to details
                        startActivity(Intent(this, GameDetailActivity::class.java).apply {
                            putExtra("GAME_ID", game.gameId)
                        })
                        updateRecentlyPlayed(game)

                        loadRecentlyPlayedGames(xuid = xboxIdText)

                    },

 */

/*
                    onAchievementUnlocked = { achievement ->
                        updateRecentlyAchieved(achievement)

                        loadRecentlyAchievedAchievements(xuid = xboxIdText)

                    },

 */


                    onNavigateToAchievements = {
                        startActivity(Intent(this, AchievementActivity::class.java))
                    },
                    onNavigateToLibrary = {
                        startActivity(Intent(this, LibraryActivity::class.java))
                    },
                    onNavigateToFriends = {
                        startActivity(Intent(this, FriendsActivity::class.java))
                    },
                    onOpenSettings = {
                        openDeviceSettings() // Handle settings click
                    }
/*
                    onOpenNotifications = {
                        openNotificationSettings() // Handle notifications click
                    }

 */


                )
            }
        }

        // Load recently played games from SharedPreferences when the activity starts

        //loadRecentlyPlayedGames(xuid = xboxIdText)
        //loadRecentlyAchievedAchievements(xuid = xboxIdText)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun shouldRequestNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun openDeviceSettings() {
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openNotificationSettings() {
        showNotificationSleepOptions()
    }

    private fun showNotificationSleepOptions() {
        val options = arrayOf("15 minutes", "1 hour", "8 hours", "1 day")
        val durations = arrayOf(
            TimeUnit.MINUTES.toMillis(15),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.HOURS.toMillis(8),
            TimeUnit.DAYS.toMillis(1)
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Notification Sleep Settings")
            .setItems(options) { dialog, which ->
                setNotificationSleepDuration(durations[which])
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun setNotificationSleepDuration(durationMillis: Long) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("notification_sleep_end_time", System.currentTimeMillis() + durationMillis)
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendNotificationIfAllowed() {
        if (shouldSendNotification()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                displayNotification()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            println("Notifications are currently in sleep mode.")
        }
    }

    private fun shouldSendNotification(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val sleepEndTime = sharedPreferences.getLong("notification_sleep_end_time", 0)
        return System.currentTimeMillis() >= sleepEndTime
    }

    private fun displayNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Your Channel Name"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.notification_icon_background) // Make sure this drawable exists
            .setContentTitle("Your Notification Title")
            .setContentText("Your notification content.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)

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
        NotificationManagerCompat.from(this).notify(12345, notificationBuilder.build())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
/*
    private fun updateRecentlyPlayed(game: Game) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the current list
        val recentlyPlayedList = loadRecentlyPlayedGamesFromSharedPreferences().toMutableList()

        // Add the new game to the top of the list
        recentlyPlayedList.add(0, game)

        // Limit the list to a certain size (e.g., 10 games)
        if (recentlyPlayedList.size > 10) {
            recentlyPlayedList.removeAt(recentlyPlayedList.size - 1)
        }

        // Save the updated list back to SharedPreferences
        val updatedJson = Gson().toJson(recentlyPlayedList)
        editor.putString("recently_played", updatedJson)
        editor.apply()

        // Update the UI list
        recentlyPlayedGamesList.clear()
        recentlyPlayedGamesList.addAll(recentlyPlayedList)
    }

 */
/*
    private fun updateRecentlyAchieved(achievement: Achievement) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the current list
        val recentlyAchievedList = loadRecentlyAchievedAchievementsFromSharedPreferences().toMutableList()

        // Add the new achievement to the top of the list
        recentlyAchievedList.add(0, achievement)

        // Limit the list to a certain size (e.g., 10 achievements)
        if (recentlyAchievedList.size > 10) {
            recentlyAchievedList.removeAt(recentlyAchievedList.size - 1)
        }

        // Save the updated list back to SharedPreferences
        val updatedJson = Gson().toJson(recentlyAchievedList)
        editor.putString("recently_achieved", updatedJson)
        editor.apply()

        // Update the UI list
        recentlyAchievedAchievementsList.clear()
        recentlyAchievedAchievementsList.addAll(recentlyAchievedList)
    }


    private fun loadRecentlyPlayedGames(xuid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the API call
                val response = ApiClient.xboxWebAPIClient.getRecentlyPlayedGames(xuid = xuid)

                if (response.isSuccessful) {
                    val apiGamesList = response.body()?.games ?: emptyList()

                    // Convert the API response to your internal data model
                    val gameList = apiGamesList.map { apiGame ->
                        Game().apply {
                            gameName = apiGame.name // Assuming the API has a 'name' field
                            gameId = apiGame.titleId.toLongOrNull() ?: 0L // Convert titleId to Long if possible
                            gamePlatform = "Xbox" // Adjust as needed
                        }
                    }

                    // Switch to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        recentlyPlayedGamesList.clear()
                        recentlyPlayedGamesList.addAll(gameList)
                    }
                } else {
                    // Handle API error (optional)
                    println("Error fetching recently played games: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }


    private fun loadRecentlyAchievedAchievements(xuid: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the API call
                val response = ApiClient.xboxWebAPIClient.getRecentAchievements(xuid = xuid)

                if (response.isSuccessful) {
                    val apiAchievementsList = response.body() ?: emptyList()

                    // Convert the API response to your internal data model
                    val achievementList = apiAchievementsList.map { apiAchievement ->
                        Achievement(
                            title = apiAchievement.title,
                            description = apiAchievement.description,
                            percentageEarned = apiAchievement.percentageEarned, // Directly from AchievementResponse
                            isEarned = apiAchievement.isEarned,
                            progress = apiAchievement.progress,
                            total = apiAchievement.total
                        )
                    }

                    // Switch to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        recentlyAchievedAchievementsList.clear()
                        recentlyAchievedAchievementsList.addAll(achievementList)
                    }
                } else {
                    // Handle API error (optional)
                    println("Error fetching recently achieved achievements: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }



    private fun loadRecentlyPlayedGamesFromSharedPreferences(): List<Game> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("recently_played", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Game>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun loadRecentlyAchievedAchievementsFromSharedPreferences(): List<Achievement> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("recently_achieved", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Achievement>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

 */
}

@Composable
fun HomeScreen(

    recentlyPlayedGames: List<Game>,
    recentlyAchievedAchievements: List<Achievement>,
   // onGamePlayed: (Game) -> Unit,
   // onAchievementUnlocked: (Achievement) -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onOpenSettings: () -> Unit,
   // onOpenNotifications: () -> Unit
) {

    val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CuriousBlue) // Set background color to CuriousBlue
            .padding(16.dp)
    ) {
        // Top row with profile picture, notifications, and settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_picture_placeholder),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )

            Row {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(24.dp)
                     //   .clickable { onOpenNotifications() } // Call the notifications function
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onOpenSettings() } // Call the settings function
                )
            }
        }

        // App name
        Text(
            text = "GamePulse",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp),
            fontFamily = jockeyOne,
            fontSize = 40.sp
        )

        val SpringGreen = Color(0xFF16F2A1)

        // Recently Played games
        Text(
            text = "Recently Played",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = jockeyOne,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            recentlyPlayedGames.forEach { game ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(SpringGreen)
                        //.clickable { onGamePlayed(game) }
                        .padding(8.dp)
                ) {
                    Text(game.gameName, fontFamily = jockeyOne)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Recent Achievements
        Text(
            text = "Recent Achievements",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = jockeyOne,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            recentlyAchievedAchievements.forEach { achievement ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(SpringGreen)
                        //.clickable { onAchievementUnlocked(achievement) }
                        .padding(8.dp)
                ) {
                    Text(achievement.title, fontFamily = jockeyOne)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Recent Friends/Played with
        Text(
            text = "Recent Friends/Played With",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = jockeyOne,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            repeat(10) { index ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(SpringGreen)
                        .clickable { /* TODO: Handle Recent Friend click */ }
                        .padding(8.dp)
                ) {
                    Text("Friend $index", fontFamily = jockeyOne)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onNavigateToAchievements,
                colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
            ) {
                Text(text = "Achievements", fontFamily = jockeyOne, color = Color.Black)
            }

            Button(
                onClick = onNavigateToLibrary,
                colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
            ) {
                Text(text = "Library", fontFamily = jockeyOne, color = Color.Black)
            }

            Button(
                onClick = onNavigateToFriends,
                colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
            ) {
                Text(text = "Friends", fontFamily = jockeyOne, color = Color.Black)
            }
        }
    }
}

