package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.scottparrillo.gamepulse.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        var steamGames by remember { mutableStateOf<List<SteamOwnedGames.Response.SteamGames>>(emptyList()) }
        var selectedSteamGame by remember { mutableStateOf<SteamOwnedGames.Response.SteamGames?>(null) }
        var xboxAchievements by remember { mutableStateOf<List<XboxPlayerAchievements.XboxAchievement>>(emptyList()) }
        var xboxGames by remember { mutableStateOf<List<XboxOwnedGames.XboxGame>>(emptyList()) }
        var selectedXboxGame by remember { mutableStateOf<XboxOwnedGames.XboxGame?>(null) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {
            // Home button and title row
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.homeicon),
                        contentDescription = "Home Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                startActivity(Intent(this@AchievementActivity, MainActivity::class.java))
                            }
                            .padding(start = 8.dp, end = 16.dp)
                    )
                    Text(
                        text = "Achievement Import", fontSize = 30.sp,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.round_information_icon),
                        contentDescription = "Help Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { dialogFlag = true }
                    )
                }
            }

            // Help dialog
            item {
                if (dialogFlag) {
                    AlertDialog(
                        onDismissRequest = { dialogFlag = false },
                        confirmButton = {},
                        title = { Text(text = "Help") },
                        text = { Text(text = "Make sure your IDs are correct and that your profiles are public.") },
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
                    text = "Steam Achievements", fontSize = 25.sp,
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
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .requiredHeight(height = 50.dp),
                    )
                    Button(
                        onClick = {
                            fetchSteamOwnedGames(steamIdText) {
                                steamGames = it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 3.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)
                    }
                }
            }

            // List of Steam Games
            items(steamGames) { game ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSteamGame = game }
                        .padding(8.dp)
                ) {
                    Text(text = "Game: ${game.name}", modifier = Modifier.padding(4.dp))
                }
            }

            // Fetch Steam Achievements for the selected game
            selectedSteamGame?.let { game ->
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
            items(steamAchievements) { achievement ->
                Text(
                    text = "Achievement: ${achievement.apiname}, Unlocked: ${achievement.achieved == 1}",
                    modifier = Modifier.padding(4.dp)
                )
            }

            // Xbox Achievements Section
            item {
                Text(
                    text = "Xbox Achievements", fontSize = 25.sp,
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
                        label = { Text("Enter Gamertag") },
                        modifier = Modifier
                            .size(width = 280.dp, height = 50.dp)
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .requiredHeight(height = 50.dp),
                    )
                    Button(
                        onClick = {
                            fetchXboxOwnedGames(xboxIdText) {
                                xboxGames = it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)
                    }
                }
            }

            // List of Xbox Games
            items(xboxGames) { game ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedXboxGame = game }
                        .padding(8.dp)
                ) {
                    Text(text = "Game: ${game.name}", modifier = Modifier.padding(4.dp))
                }
            }

            // Fetch Xbox Achievements for the selected game
            selectedXboxGame?.let { game ->
                item {
                    Button(
                        onClick = {
                            importXboxAchievements(xboxIdText, game.titleId) { achievements ->
                                xboxAchievements = achievements
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import Achievements for ${game.name}", color = Color.Black)
                    }
                }
            }

            // Display Xbox Achievements
            items(xboxAchievements) { achievement ->
                Text(
                    text = "Achievement: ${achievement.name}, Unlocked: ${achievement.unlocked}",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }

    // Fetch Xbox Owned Games
    private fun fetchXboxOwnedGames(
        gamertag: String,
        onResult: (List<XboxOwnedGames.XboxGame>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val xuidResponse = ApiClient.openXBL.xboxWebAPIClient.getXuidFromGamertag(gamertag).execute()
            val xuid = xuidResponse.body()?.people?.firstOrNull()?.xuid ?: return@launch

            val response = ApiClient.openXBL.xboxWebAPIClient.getAllGamesByID(xuid = xuid).execute()
            if (response.isSuccessful) {
                val games = response.body()?.games ?: emptyList()
                withContext(Dispatchers.Main) {
                    onResult(games)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Handle API failure
                }
            }
        }
    }
    // Fetch owned games from Steam
    private fun fetchSteamOwnedGames(
        steamId: String,
        onResult: (List<SteamOwnedGames.Response.SteamGames>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = SteamRetrofit.apiSteam.apiS.getAllOwnedGames(
                    key = Constants.STEAM_API_KEY,
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
    private fun importSteamAchievements(
        steamId: String,
        appId: Long,
        onResult: (List<SteamPlayerAchievements.Playerstats.SteamAchievement>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                    appid = appId,
                    key = Constants.STEAM_API_KEY,
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

    // Import Xbox Achievements for a specific game
    private fun importXboxAchievements(
        gamertag: String,
        titleId: String,
        onResult: (List<XboxPlayerAchievements.XboxAchievement>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val xuidResponse = ApiClient.openXBL.xboxWebAPIClient.getXuidFromGamertag(gamertag).execute()
            val xuid = xuidResponse.body()?.people?.firstOrNull()?.xuid ?: return@launch

            val response = ApiClient.openXBL.xboxWebAPIClient.getUserAchievements(xuid = xuid, titleId = titleId).execute()
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
        }
    }
}
