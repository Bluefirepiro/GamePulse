package com.scottparrillo.gamepulse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.api.ApiClient
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.ObjectOutputStream
import java.time.Instant
import java.time.ZoneId
import kotlin.concurrent.thread


class GameImportActivity: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                GameImportScreen()
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    @Preview(showBackground = true)
    @Composable
    fun GameImportScreen() {
        var steamIdText by rememberSaveable { mutableStateOf("") }
        var steamId by rememberSaveable { mutableStateOf("") }
        var dialogFlag = rememberSaveable { mutableStateOf(false) }
        var xboxIdText by rememberSaveable { mutableStateOf("") }
        var xboxId by rememberSaveable { mutableStateOf("") }
        // val enterFlag = rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val view = LocalView.current
        view.keepScreenOn = true
        fun saveGameFile(mutableGameList: MutableList<Game>): Boolean {
            try {
                val fos = context.openFileOutput("gameList", MODE_PRIVATE)
                val oos = ObjectOutputStream(fos)
                oos.writeObject(mutableGameList)
                oos.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
            return true
        }
        //Setting up a dialog alert
        when {
            dialogFlag.value -> {
                AlertDialog(onDismissRequest = { dialogFlag.value = false }, confirmButton = {
                },
                    title = { Text(text = "Help") },
                    text = {
                        Text(
                            text = "If steam is not importing correctly please make sure your profile is set to public\n" +
                                    "If your steam ID is not showing go to your steam profile edit profile and delete your custom URL"
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogFlag.value = false }) {
                            Text(text = "Dismiss")
                        }
                    })
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {
            item {
                LazyRow(verticalAlignment = Alignment.CenterVertically) {
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.homeicon),
                            contentDescription = "Back arrow",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .size(65.dp)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    saveGameFile(Game.gameList)
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                }

                        )
                    }
                    item {
                        Text(
                            text = "Game Import", fontSize = 40.sp,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp)
                        )
                    }
                    item {
                        Image(painter = painterResource(id = R.drawable.questionmark),
                            contentDescription = "question mark",
                            modifier = Modifier
                                .size(width = 50.dp, height = 50.dp)
                                .clickable {
                                    dialogFlag.value = true
                                })
                    }
                }
            }
            item {
                Row {
                    Text(
                        text = "Steam Import", fontSize = 35.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
            item {
                LazyRow {
                    item {
                        TextField(
                            value = steamIdText, onValueChange = { steamIdText = it },
                            label = { Text("Enter Steam Id") },
                            modifier = Modifier
                                .size(width = 280.dp, height = 50.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .requiredHeight(height = 50.dp),

                            )
                    }
                    item {
                        Button(
                            onClick = {
                                steamId = steamIdText
                                steamIdText = "Importing game do not click away"

                                //Upon clicking import get the steam user id then load in the games

                                //Start of sync api call
                                thread(start = true) {
                                    //Start of first call
                                    val call = SteamRetrofit.apiSteam.apiS.getAllOwnedGames(
                                        "4A7BFC2A3443A093EA9953FD5529C795",
                                        true,
                                        steamId.toLong(),
                                        "json"
                                    )
                                    val response = call.execute()
                                    if (response.isSuccessful) {
                                        val post = response.body()!!
                                        //We need this and it is used
                                        val res = post.response!!
                                        val games: List<SteamOwnedGames.Response.SteamGames> =
                                            post.response.games
                                        for (game in games) {
                                            //Sanity check to make sure we are not adding the same game
                                            var sameNameFlag = false
                                            if (Game.gameList.isNotEmpty()) {
                                                for (gameL in Game.gameList) {
                                                    if (gameL.gameName == game.name && gameL.gamePlatform == "Steam") {
                                                        sameNameFlag = true
                                                    }
                                                }
                                            }
                                            if (!sameNameFlag) {

                                                //make a game object and add it to games list
                                                val gameconvert = Game()
                                                gameconvert.gameName = game.name
                                                gameconvert.gameId = game.appid
                                                val gameTimeHours = game.playtime_forever / 60
                                                gameconvert.gameTime = gameTimeHours.toFloat()

                                                var convertToUrl =
                                                    "https://steamcdn-a.akamaihd.net/steam/apps/"

                                                convertToUrl =
                                                    convertToUrl.plus(game.appid.toString())
                                                //convertToUrl = convertToUrl.plus("/)"
                                                convertToUrl = convertToUrl.plus("/header.jpg")
                                                gameconvert.coverURL = convertToUrl
                                                val timeLastPlayed =
                                                    Instant.ofEpochSecond(game.rtime_last_played)
                                                        .atZone(
                                                            ZoneId.systemDefault()
                                                        ).toLocalDateTime()
                                                gameconvert.dateTimeLastPlayed = timeLastPlayed
                                                gameconvert.gamePlatform = "Steam"
                                                if (gameconvert.gameTime <= 0) {
                                                    gameconvert.newlyAdded = true
                                                }
                                                Game.gameList.add(gameconvert)
                                            } else {
                                                //Do nothing
                                            }
                                        }
                                        saveGameFile(Game.gameList)
                                    }
                                    //End of game import api call
                                    for (game in Game.gameList) {
                                        val callAch =
                                            SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                                                game.gameId, "4A7BFC2A3443A093EA9953FD5529C795",
                                                steamId.toLong()
                                            )
                                        val achResponse = callAch.execute()
                                        if (achResponse.isSuccessful) {
                                            //go through and make sure the achievement isnt already added

                                            val achievementPost = achResponse.body()!!
                                            val playerStats = achievementPost.playerstats!!
                                            val gameAchievements = playerStats.achievements
                                            for (ach in gameAchievements) {
                                                var sameElementFlag = false
                                                if (game.achievements.isNotEmpty()) {
                                                    for (achL in game.achievements) {
                                                        if (achL.title == ach.apiname) {
                                                            sameElementFlag = true
                                                        }
                                                    }
                                                }
                                                if (!sameElementFlag) {
                                                    val earnedFlag =
                                                        //Ignore the warning
                                                        if (ach.achieved == 1) true else false
                                                    val toConvert = Achievement(
                                                        //0, ach.apiname,
                                                        //"", 0.0,
                                                        //earnedFlag, 0, 0, 0
                                                    )
                                                    game.achievements.add(toConvert)
                                                } else {
                                                    //do Nothing
                                                }
                                            }
                                        }
                                    }
                                    //End of player achievement Call
                                    //Start of achievement percentage call
                                    for (game in Game.gameList) {
                                        val callAchPercent =
                                            SteamRetrofit.apiSteam.apiS.getAllAchievementPercentages(
                                                game.gameId,
                                                "json"
                                            )
                                        val achPercentResponse = callAchPercent.execute()
                                        if (achPercentResponse.isSuccessful) {
                                            val achPercentList =
                                                achPercentResponse.body()?.achievementpercentages?.achievements
                                            if (achPercentList != null) {
                                                for (achPercent in achPercentList) {
                                                    for (ach in game.achievements) {
                                                        if (achPercent.name == ach.title) {
                                                            ach.percentageEarned =
                                                                achPercent.percent.toDouble()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    saveGameFile(Game.gameList)
                                    for (game in Game.gameList) {
                                        val callAch = SteamRetrofit.apiSteam.apiS.getGameSchema(
                                            "4A7BFC2A3443A093EA9953FD5529C795", game.gameId
                                        )
                                        val achResponse = callAch.execute()
                                        if (achResponse.isSuccessful) {
                                            val achievements =
                                                achResponse.body()?.game?.availableGameStats?.achievements
                                            if (game.achievements.isNotEmpty()) {
                                                if (achievements != null) {
                                                    for (ach in achievements) {
                                                        for (achI in game.achievements) {
                                                            if (achI.title == ach.name) {
                                                                achI.title = ach.displayName
                                                                achI.description = ach.description
                                                                achI.achImageUrl = ach.icon
                                                                achI.achImageUrlGray = ach.icongray
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    //Determine which games have been completed
                                    if (Game.gameList.isNotEmpty()) {
                                        for (game in Game.gameList) {
                                            var score = 0
                                            if (game.achievements.isNotEmpty()) {
                                                for (ach in game.achievements) {
                                                    if (ach.isEarned) {
                                                        score++
                                                    }
                                                }
                                                if (score == game.achievements.size) {
                                                    game.allAchiev = true
                                                }
                                            }
                                        }
                                    }
                                    saveGameFile(Game.gameList)
                                    steamIdText = "Done Importing"
                                }

                            }, modifier = Modifier.padding(horizontal = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                        ) {
                            Text("Import", color = Color.Black)
                        }
                    }

                }
            }
            // Xbox Import Section (Below Steam Import)
            item {
                Text(
                    text = "Xbox Live Import", fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                LazyRow {
                    item {
                        TextField(
                            value = xboxIdText,
                            onValueChange = { xboxIdText = it },
                            label = { Text("Enter Xbox Live ID") },
                            modifier = Modifier
                                .size(width = 280.dp, height = 50.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .requiredHeight(height = 50.dp),
                        )

                    }
                    item {
                        Row {

                                Button(
                                    onClick = {
                                        // Start Xbox game import
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                // Fetch all games by Xbox ID from Xbox API

                                                val response = ApiClient.openXBL.xboxWebAPIClient.getAllGamesByID(
                                                    xuid = xboxIdText
                                                ).execute()  // Using execute() for a synchronous call

                                                if (response.isSuccessful) {
                                                    val xboxGamesResponse = response.body()
                                                    val gamesList: List<XboxOwnedGames.XboxGame> =
                                                        xboxGamesResponse?.games ?: emptyList()

                                                    for (game in gamesList) {
                                                        val gameConvert = Game().apply {
                                                            gameName = game.name
                                                            gameId = game.titleId.toLongOrNull() ?: 0L
                                                            gamePlatform = "Xbox"
                                                            coverURL = game.displayImage // Assuming you want the display image
                                                            dateTimeLastPlayed = Instant.parse(game.titleHistory?.lastTimePlayed)
                                                                .atZone(ZoneId.systemDefault())
                                                                .toLocalDateTime()
                                                            //currentGamerscore = game.achievement.currentGamerscore
                                                            //totalGamerscore = game.achievement.totalGamerscore
                                                        }
                                                        Game.gameList.add(gameConvert)
                                                    }
                                                    saveGameFile(Game.gameList)

                                                    // Update UI on the main thread
                                                    withContext(Dispatchers.Main) {
                                                        xboxIdText = "Done Importing Xbox games"
                                                    }
                                                }  else {
                                                    // Handle API error
                                                    withContext(Dispatchers.Main) {
                                                        xboxIdText =
                                                            "Error importing games."
                                                    }
                                                    println("Error fetching recently played games: ${response.errorBody()}")
                                                }
                                            }  catch (e: Exception) {
                                                e.printStackTrace()
                                                withContext(Dispatchers.Main) {
                                                    xboxIdText = "Error during import."
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                                ) {
                                    Text("Import", color = Color.Black)
                                }

                        }
                    }


                }
            }
        }
    }
}
