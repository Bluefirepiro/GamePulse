package com.scottparrillo.gamepulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.api.ApiClient
import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.SteamPlayerAchievements
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.scottparrillo.gamepulse.SteamOwnedGames

class AchievementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                AchievementScreen()
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun AchievementScreen() {
        var steamIdText by remember { mutableStateOf("") }
        var xboxIdText by remember { mutableStateOf("") }
        var dialogFlag by remember { mutableStateOf(false) }
        var steamAchievements by remember { mutableStateOf<List<SteamPlayerAchievements.Playerstats.SteamAchievement>>(emptyList()) }
        var steamGames by remember { mutableStateOf<List<SteamOwnedGames.Response.SteamGame>>(emptyList()) }
        var selectedGame by remember { mutableStateOf<SteamOwnedGames.Response.SteamGame?>(null) }
        var xboxAchievements by remember { mutableStateOf<List<XboxPlayerAchievements.XboxAchievement>>(emptyList()) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {
            // Title row with help icon
            item {
                LazyRow(verticalAlignment = Alignment.CenterVertically) {
                    item {
                        Text(
                            text = "Achievement Import", fontSize = 40.sp,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp)
                        )
                    }
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.questionmark), // Replace with your drawable resource
                            contentDescription = "Help Icon",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { dialogFlag = true }
                        )
                    }
                }
            }

            // Help dialog
            item {
                if (dialogFlag) {
                    AlertDialog(
                        onDismissRequest = { dialogFlag = false },
                        confirmButton = {},
                        title = { Text(text = "Help") },
                        text = {
                            Text(text = "Make sure your IDs are correct and that your profiles are public.")
                        },
                        dismissButton = {
                            Button(onClick = { dialogFlag = false }) {
                                Text(text = "Dismiss")
                            }
                        }
                    )
                }
            }

            // Steam Achievements Section
            item {
                Text(
                    text = "Steam Achievements", fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            item {
                Row {
                    TextField(
                        value = steamIdText,
                        onValueChange = { steamIdText = it },
                        label = { Text("Enter Steam ID") },
                        modifier = Modifier
                            .size(width = 280.dp, height = 50.dp)
                            .padding(horizontal = 8.dp)
                    )
                    Button(
                        onClick = {
                            fetchSteamOwnedGames(steamIdText) {
                                steamGames = it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Get Games", color = Color.Black)
                    }
                }
            }

            // List of Steam Games
            item {
                LazyColumn {
                    steamGames.forEach { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGame = game }
                                .padding(8.dp)
                        ) {
                            Text(text = "Game: ${game.name}", modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }

            // Fetch Steam Achievements for the selected game
            selectedGame?.let { game ->
                item {
                    Button(
                        onClick = {
                            importSteamAchievements(steamIdText, game.appid) { achievements ->
                                steamAchievements = achievements
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import Achievements for ${game.name}", color = Color.Black)
                    }
                }
            }

            // Display Steam Achievements
            item {
                steamAchievements.forEach { achievement ->
                    Text(
                        text = "Achievement: ${achievement.apiname}, Unlocked: ${achievement.achieved == 1}",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // Xbox Achievements Section
            item {
                Text(
                    text = "Xbox Achievements", fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            item {
                Row {
                    TextField(
                        value = xboxIdText,
                        onValueChange = { xboxIdText = it },
                        label = { Text("Enter Xbox Live ID") },
                        modifier = Modifier
                            .size(width = 280.dp, height = 50.dp)
                            .padding(horizontal = 8.dp)
                    )
                    Button(
                        onClick = {
                            importXboxAchievements(xboxIdText) {
                                xboxAchievements = it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)
                    }
                }
            }

            // Display Xbox Achievements
            item {
                xboxAchievements.forEach { achievement ->
                    Text(text = "Achievement: ${achievement.name}, Unlocked: ${achievement.unlocked}", modifier = Modifier.padding(4.dp))
                }
            }
        }
    }

    // Fetch owned games from Steam
    private fun fetchSteamOwnedGames(steamId: String, onResult: (List<SteamOwnedGames.Response.SteamGame>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.steamWebAPIClient.getAllOwnedGames(
                    key = "4A7BFC2A3443A093EA9953FD5529C795",
                    include_appinfo = true,
                    steamid = steamId.toLong(),
                    format = "json"
                ).execute()

                if (response.isSuccessful) {
                    val games = response.body()?.response?.games ?: emptyList()
                    withContext(Dispatchers.Main) {
                        onResult(games)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(emptyList()) // Handle API failure
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Handle exception
                }
            }
        }
    }

    // Import Steam Achievements
    private fun importSteamAchievements(steamId: String, appId: Long, onResult: (List<SteamPlayerAchievements.Playerstats.SteamAchievement>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.steamWebAPIClient.getAllGameAchievements(
                    appid = appId,
                    key = "4A7BFC2A3443A093EA9953FD5529C795",
                    steamid = steamId.toLong()
                ).execute()

                if (response.isSuccessful) {
                    val achievements = response.body()?.playerstats?.achievements ?: emptyList()
                    withContext(Dispatchers.Main) {
                        onResult(achievements)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(emptyList()) // Handle API failure
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Handle exception
                }
            }
        }
    }

    // Import Xbox Achievements
    private fun importXboxAchievements(xboxId: String, onResult: (List<XboxPlayerAchievements.XboxAchievement>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.xboxWebAPIClient.getUserAchievements(
                    apiKey = "139d737c-b4c4-46c4-b972-08e176ce102f",
                    xboxId = xboxId
                ).execute()

                if (response.isSuccessful) {
                    val achievements = response.body()?.achievements ?: emptyList()
                    withContext(Dispatchers.Main) {
                        onResult(achievements)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(emptyList()) // Handle API failure
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Handle exception
                }
            }
        }
    }
}
