package com.scottparrillo.gamepulse


import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
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

class GameImportActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                GameImportScreen()
            }
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun GameImportScreen()
    {  var steamIdText by rememberSaveable { mutableStateOf("") }
        val enterFlag = rememberSaveable { mutableStateOf(false) }
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
                    LazyRow {
                        item { Image(
                            painter = painterResource(id = R.drawable.homeicon),
                            contentDescription = "Back arrow",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .size(65.dp)
                                .clickable {
                                    saveGameFile(Game.gameList)
                                    context.startActivity(
                                        Intent(
                                            context,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                        ) }
                        item {Text(text = "Game Import", fontSize = 40.sp,
                            modifier = Modifier.padding(vertical = 20.dp)) }

                    }

                    }

            item { LazyRow {
                item {TextField(
                    value = steamIdText, onValueChange = { steamIdText = it },
                    label = { Text("Enter Steam Id") },
                    modifier = Modifier
                        .size(width = 280.dp, height = 46.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )  }
                item {
                    Button(
                        onClick = {
                            // This bool is used to indicate whether the game list is done importing
                            var listDone = false
                            //Upon clicking import get the steam user id then load in the games
                            val call = SteamRetrofit.apiSteam.apiS.getAllOwnedGames("4A7BFC2A3443A093EA9953FD5529C795", true, steamIdText.toLong(), "json" )
                            call.enqueue(object: Callback<SteamOwnedGames> {
                                override fun onResponse(
                                    call: Call<SteamOwnedGames>,
                                    response: Response<SteamOwnedGames>
                                ) {
                                    if(response.isSuccessful)
                                    {
                                        val post = response.body()!!
                                        val res = post.response!!
                                        val games:List<SteamOwnedGames.Response.SteamGames> = post.response.games
                                        for(game in games) {
                                            //make a game object and add it to games list
                                            var gameconvert = Game()
                                            gameconvert.gameName = game.name
                                            gameconvert.gameId = game.appid
                                            var convertToUrl: String = "https://steamcdn-a.akamaihd.net/steam/apps/"
                                            convertToUrl = convertToUrl.plus(game.appid.toString())
                                            //convertToUrl = convertToUrl.plus("/)"
                                            convertToUrl = convertToUrl.plus("/header.jpg")
                                            gameconvert.coverURL = convertToUrl
                                            gameconvert.gamePlatform = "Steam"
                                            gameconvert.gameTime = game.playtime_forever.toFloat()
                                            Game.gameList.add(gameconvert)
                                        }
                                        saveGameFile(Game.gameList)

                                        //context.startActivity(Intent(context, LibraryActivity::class.java))

                                    }
                                }
                                override fun onFailure(p0: Call<SteamOwnedGames>, p1: Throwable) {
                                    p1.printStackTrace()
                                }

                            })
                            /*
                            Note thread joining is causing the achievement import to run before the games are imported
                            Move the achievement to another button or add an explicit call to the thread for order of run
                             */


                            //Iterate through the Game.gamelist and add the achievemnts
                            /*
                            for(game in Game.gameList) {
                                val achievementCall = SteamRetrofit.apiSteam.apiS.getAllGameAchievements(game.gameId,"4A7BFC2A3443A093EA9953FD5529C795", steamIdText.toLong())
                                achievementCall.enqueue(object: Callback<SteamPlayerAchievements>{
                                    override fun onResponse(
                                        achievementCall: Call<SteamPlayerAchievements>,
                                        achievementResponse: Response<SteamPlayerAchievements>
                                    ) {
                                        if(achievementResponse.isSuccessful){
                                            val achievementPost = achievementResponse.body()!!
                                            val playerStats = achievementPost.playerstats!!
                                            val gameAchievements = playerStats.achievements
                                            for(ach in gameAchievements)
                                            {
                                                val earnedFlag = if(ach.achieved == 1)true else false
                                                val toConvert:Achievement = Achievement(0,ach.apiname,"",0.0,earnedFlag,0,0,0)
                                                game.achievements.add(toConvert)
                                            }
                                        }
                                    }

                                    override fun onFailure(
                                        achievementCall: Call<SteamPlayerAchievements>,
                                        achievementThrow: Throwable
                                    ) {
                                        achievementThrow.printStackTrace()
                                    }

                                })
                            }

                             */
                            //Put test code here



                        }, modifier = Modifier.padding(horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                    ) {
                        Text("Import", color = Color.Black)


                    }
                    //I want to just test this call and nothing else

                }


                //Get Id import steam games and add them to the list
            } }
            item {
                Row {
                    Button(onClick = {
                        //Loop through Game.game list and grab achievements
                        for(game in Game.gameList)
                        {
                            val achievementCall = SteamRetrofit.apiSteam.apiS.getAllGameAchievements(game.gameId,"4A7BFC2A3443A093EA9953FD5529C795", steamIdText.toLong())
                            achievementCall.enqueue(object: Callback<SteamPlayerAchievements>{
                                override fun onResponse(
                                    call: Call<SteamPlayerAchievements>,
                                    response: Response<SteamPlayerAchievements>
                                ) {
                                    if(response.isSuccessful){
                                        val achievementPost = response.body()!!
                                        val playerStats = achievementPost.playerstats!!
                                        val gameAchievements = playerStats.achievements
                                        for(ach in gameAchievements)
                                        {
                                            val earnedFlag = if(ach.achieved == 1)true else false
                                            val toConvert:Achievement = Achievement(0,ach.apiname,"",0.0,earnedFlag,0,0,0)
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
                        }
                        saveGameFile(Game.gameList)


                    }) {
                        Text("Achievement Test", color = Color.Black)
                    }
                }
            }
        }

    }
}