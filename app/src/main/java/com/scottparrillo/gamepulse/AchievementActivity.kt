package com.scottparrillo.gamepulse

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
        var xboxAchievements by remember { mutableStateOf<List<XboxPlayerAchievements.XboxAchievement>>(emptyList()) }

        // LazyColumn to hold achievements and input fields
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {
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

            // AlertDialog for help information
            item{
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

            // Section for Steam achievements
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

                            val appId: Long = 0
                            importSteamAchievements(steamIdText, appId) {
                                steamAchievements = it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)
                    }
                }
            }

            // Display Steam Achievements
            item {
                steamAchievements.forEach { achievement ->
                    Text(text = "Achievement: ${achievement.apiname}, Unlocked: ${achievement.achieved == 1}", modifier = Modifier.padding(4.dp))
                }
            }

            // Section for Xbox achievements
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

    private fun importSteamAchievements(steamId: String, appId: Long, onResult: (List<SteamPlayerAchievements.Playerstats.SteamAchievement>) -> Unit) {
        // Coroutine for fetching Steam achievements
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                    appid = appId,
                    key = "4A7BFC2A3443A093EA9953FD5529C795", // Your Steam API key
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

    private fun importXboxAchievements(xboxId: String, onResult: (List<XboxPlayerAchievements.XboxAchievement>) -> Unit) {
        // Coroutine for fetching Xbox achievements
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.openXBL.xboxWebAPIClient.getUserAchievements("139d737c-b4c4-46c4-b972-08e176ce102f", xboxId, "YOUR_TITLE_ID").execute() // Replace with your titleId
            if (response.isSuccessful) {
                val achievements = response.body()?.achievements ?: emptyList()
                withContext(Dispatchers.Main) {
                    onResult(achievements)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(emptyList()) // Handle error or show a message
                }
            }
        }
    }
}
