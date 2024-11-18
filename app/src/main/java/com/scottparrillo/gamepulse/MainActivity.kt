package com.scottparrillo.gamepulse

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.PreferencesUtil
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    //private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    private val recentlyPlayedGamesList = mutableStateListOf<Game>()
    private val recentlyAchievedAchievementsList = mutableStateListOf<Achievement>()

    private var profileImageUri by mutableStateOf<Uri?>(null)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load persisted data on startup
        loadPersistedData()

        // Initialize the image picker launcher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { updateProfileImage(it) }
        }

        // Initialize the storage permission request launcher
        requestStoragePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    launchImagePicker()
                } else {
                    Toast.makeText(
                        this,
                        "Storage permission is required to select profile image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        // Initialize the notification permission request launcher
        requestNotificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    sendNotificationIfAllowed()
                } else {
                    println("Notification permission denied.")
                }
            }

        if (shouldRequestNotificationPermission()) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        loadProfileImage() // Load saved profile image on startup

        setContent {
            GamePulseTheme {
                HomeScreen(
                    recentlyPlayedGames = recentlyPlayedGamesList,
                    recentlyAchievedAchievements = recentlyAchievedAchievementsList,
                    profileImageUri = profileImageUri,
                    onNavigateToAchievements = {
                        startActivity(
                            Intent(
                                this,
                                AchievementActivity::class.java
                            )
                        )
                    },
                    onNavigateToLibrary = {
                        startActivity(
                            Intent(
                                this,
                                LibraryActivity::class.java
                            )
                        )
                    },
                    onNavigateToFriends = {
                        startActivity(
                            Intent(
                                this,
                                FriendsActivity::class.java
                            )
                        )
                    },
                    onOpenSettings = { openDeviceSettings() },
                    onChangeProfilePicture = { requestStoragePermission() },
                    onGameClick = { selectedGame ->
                        val intent = Intent(this, SingleGameActivity::class.java).apply {
                            putExtra("game", Gson().toJson(selectedGame)) // Pass game data as JSON
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadPersistedData() // Reload data when activity resumes
    }

    // Request storage permission if necessary, or launch the image picker if permission is already granted
    private fun requestStoragePermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // For Android 13+, use the Photo Picker API, which does not require permission
                pickImageLauncher.launch("image/*")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                // For Android 6 to 12, use READ_EXTERNAL_STORAGE permission
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    pickImageLauncher.launch("image/*")
                }
            }
            else -> {
                // For SDK < M, no runtime permission needed
                pickImageLauncher.launch("image/*")
            }
        }
    }

    private fun launchImagePicker() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES) // Android 13+ Photo Picker API
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        pickImageLauncher.launch(intent.toString())
    }

    private fun updateProfileImage(uri: Uri) {
        profileImageUri = uri
        val sharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("profileImageUri", uri.toString())
            apply()
        }
    }

    private fun loadProfileImage() {
        val sharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val savedUriString = sharedPreferences.getString("profileImageUri", null)
        profileImageUri = savedUriString?.let { Uri.parse(it) }
    }

    // Function to save lists of games and achievements to SharedPreferences
    private fun saveDataToPreferences() {
        lifecycleScope.launch {
            PreferencesUtil.saveDataToPreferences(
                context = this@MainActivity,
                games = recentlyPlayedGamesList,
                achievements = recentlyAchievedAchievementsList
            )
        }
    }

    // Function to load lists of games and achievements from SharedPreferences
    private fun loadPersistedData() {
        val sharedPreferences = getSharedPreferences("GamePulsePrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val gamesJson = sharedPreferences.getString("recentlyPlayedGames", null)
        if (gamesJson != null) {
            val gameType = object : TypeToken<List<Game>>() {}.type
            val games: List<Game> = gson.fromJson(gamesJson, gameType)
            recentlyPlayedGamesList.clear()
            recentlyPlayedGamesList.addAll(games)
        }

        val achievementsJson = sharedPreferences.getString("recentAchievements", null)
        if (achievementsJson != null) {
            val achievementType = object : TypeToken<List<Achievement>>() {}.type
            val achievements: List<Achievement> = gson.fromJson(achievementsJson, achievementType)
            recentlyAchievedAchievementsList.clear()
            recentlyAchievedAchievementsList.addAll(achievements)
        }
    }


    private fun openDeviceSettings() {
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun shouldRequestNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    }

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
            .setSmallIcon(R.drawable.notification_icon_background)
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
            return
        }
        NotificationManagerCompat.from(this).notify(12345, notificationBuilder.build())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(
    recentlyPlayedGames: List<Game>,
    recentlyAchievedAchievements: List<Achievement>,
    profileImageUri: Uri?,
    onNavigateToAchievements: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onOpenSettings: () -> Unit,
    onChangeProfilePicture: () -> Unit,
    onGameClick: (Game) -> Unit // New parameter for handling game clicks
) {
    val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
    val SpringGreen = Color(0xFF16F2A1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CuriousBlue)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(profileImageUri, onChangeProfilePicture)
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onOpenSettings() }
            )
        }

        Text(
            text = "GamePulse",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp),
            fontFamily = jockeyOne,
            fontSize = 40.sp
        )

        SectionTitle("Recently Played", jockeyOne)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(recentlyPlayedGames) { game ->
                GameItem(game) { onGameClick(it) } // Pass the click event
            }
        }

        SectionTitle("Recent Achievements", jockeyOne)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(recentlyAchievedAchievements) { achievement ->
                AchievementItem(achievement)
            }
        }

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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImage(profileImageUri: Uri?, onChangeProfilePicture: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp) // Adjust the size as needed
            .clip(CircleShape)
            .background(Color.Gray) // Add a background color for visibility or use Color.Transparent
            .clickable { onChangeProfilePicture() }
    ) {
        if (profileImageUri != null) {
            GlideImage(
                model = profileImageUri,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop // This ensures the image is cropped to fit within the circle
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.profile_picture_placeholder),
                contentDescription = "Default Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, fontFamily: FontFamily) {
    Text(
        text = title,
        fontSize = 20.sp,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = fontFamily,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GameItem(game: Game, onClick: (Game) -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick(game) } // Add click handler
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            GlideImage(
                model = game.coverURL,
                contentDescription = "${game.gameName} Cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = game.gameName,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AchievementItem(achievement: Achievement) {
    Column(
        modifier = Modifier
            .width(120.dp) // Adjust width for the image and text
            .background(MaterialTheme.colorScheme.secondary)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the achievement image if available
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)) // Rounded corners for the image
        ) {
            if (!achievement.imageURL.isNullOrEmpty()) { // Check if an image URL is available
                GlideImage(
                    model = achievement.imageURL, // Use the imageURL property from Achievement
                    contentDescription = "${achievement.title} Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: Show a placeholder if no image URL is available
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        // Display the achievement title below the image
        Text(
            text = achievement.title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, // Truncate text if it's too long
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
