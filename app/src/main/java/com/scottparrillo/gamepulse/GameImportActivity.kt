package com.scottparrillo.gamepulse


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.SteamPlayerAchievements
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    fun GameImportScreen()
    {  var steamIdText by rememberSaveable { mutableStateOf("") }
        var steamId by rememberSaveable { mutableStateOf("") }
       // val enterFlag = rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
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
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ){
            item {
                    LazyRow(verticalAlignment = Alignment.CenterVertically) {
                        item { Image(
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
                        ) }
                        item {Text(text = "Game Import", fontSize = 40.sp,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp)) }

                    }

                    }
            item { Row (){
                Text(text = "Steam Import", fontSize = 35.sp,
                     textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp))
            } }

            item { LazyRow {
                item {TextField(
                    value = steamIdText, onValueChange = { steamIdText = it},
                    label = { Text("Enter Steam Id") },
                    modifier = Modifier
                        .size(width = 280.dp, height = 50.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .requiredHeight(height = 50.dp),

                )  }
                item {
                    Button(
                        onClick = {
                            steamId = steamIdText
                            steamIdText = "Importing game do not click away"

                            //Upon clicking import get the steam user id then load in the games

                            //Start of synch api call
                            thread (start = true){
                                //Start of first call
                                val call = SteamRetrofit.apiSteam.apiS.getAllOwnedGames("4A7BFC2A3443A093EA9953FD5529C795", true, steamId.toLong(), "json" )
                                val response = call.execute()
                                if(response.isSuccessful)
                                {
                                    val post = response.body()!!
                                    //We need this and it is used
                                    val res = post.response!!
                                    val games:List<SteamOwnedGames.Response.SteamGames> = post.response.games
                                    for(game in games) {
                                        //make a game object and add it to games list
                                        val gameconvert = Game()
                                        gameconvert.gameName = game.name
                                        gameconvert.gameId = game.appid
                                        val gameTimeHours = game.playtime_forever / 60
                                        gameconvert.gameTime = gameTimeHours.toFloat()
                                        var convertToUrl = "https://steamcdn-a.akamaihd.net/steam/apps/"
                                        convertToUrl = convertToUrl.plus(game.appid.toString())
                                        //convertToUrl = convertToUrl.plus("/)"
                                        convertToUrl = convertToUrl.plus("/header.jpg")
                                        gameconvert.coverURL = convertToUrl
                                        val timeLastPlayed = Instant.ofEpochSecond(game.rtime_last_played).atZone(
                                            ZoneId.systemDefault()).toLocalDateTime()
                                        gameconvert.dateTimeLastPlayed = timeLastPlayed
                                        gameconvert.gamePlatform = "Steam"
                                        if(gameconvert.gameTime <= 0)
                                        {
                                            gameconvert.newlyAdded = true
                                        }
                                        Game.gameList.add(gameconvert)
                                    }
                                    saveGameFile(Game.gameList)
                                }
                                //End of game import api call
                                for (game in Game.gameList) {
                                   val callAch = SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                                        game.gameId, "4A7BFC2A3443A093EA9953FD5529C795",
                                        steamId.toLong())
                                    callAch.enqueue(object: Callback<SteamPlayerAchievements> {
                                        override fun onResponse(
                                            p0: Call<SteamPlayerAchievements>,
                                            achResponse: Response<SteamPlayerAchievements>
                                        ) {
                                            if(achResponse.isSuccessful){
                                                val achievementPost = achResponse.body()!!
                                                val playerStats = achievementPost.playerstats!!
                                                val gameAchievements = playerStats.achievements
                                                for (ach in gameAchievements) {
                                                    val earnedFlag =
                                                        //Ignore the warning
                                                        if (ach.achieved == 1) true else false
                                                    val toConvert = Achievement(0, ach.apiname,
                                                        "", 0.0,
                                                        earnedFlag, 0, 0, 0)
                                                    game.achievements.add(toConvert)
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            p0: Call<SteamPlayerAchievements>,
                                            p1: Throwable
                                        ) {
                                            p1.printStackTrace()
                                        }
                                    })
                                    /*
                                        //First call being made for getting the achievement
                                        val achievementCall =
                                            SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                                                game.gameId, "4A7BFC2A3443A093EA9953FD5529C795",
                                                steamId.toLong())
                                        val achResponse = achievementCall.execute()
                                        if (achResponse.isSuccessful) {
                                            val achievementPost = achResponse.body()!!
                                            val playerStats = achievementPost.playerstats!!
                                            val gameAchievements = playerStats.achievements
                                            for (ach in gameAchievements) {
                                                val earnedFlag =
                                                    //Ignore the warning
                                                    if (ach.achieved == 1) true else false
                                                val toConvert = Achievement(0, ach.apiname,
                                                    "", 0.0,
                                                    earnedFlag, 0, 0, 0)
                                                game.achievements.add(toConvert)
                                            }
                                        }

                                    }

                                     */

                                }
                                saveGameFile(Game.gameList)

                                }
                            steamIdText = "Done Importing"

                        }, modifier = Modifier.padding(horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)
                    }

                }
            } }



        }


    }

}