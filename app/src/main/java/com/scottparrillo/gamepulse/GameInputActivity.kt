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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.PrussainBlue
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class GameInputActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                GameInputScreen()
            }
        }
    }
    //This is going to be a series of text fields for the user to input game details in
    @Preview(showBackground = true)
    @Composable
    fun GameInputScreen()
    {
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        //These are the variables for the text inputs
        var gameName by remember { mutableStateOf("") }
        var gameDesc by remember { mutableStateOf("") }
        var gameTime by remember { mutableStateOf("") }
        var gameDate by remember { mutableStateOf("") }
        var gamePlatform by remember { mutableStateOf("") }
        //Going to use this to input user data into the database

        //This function saves games to a file named gameList
        fun getGameFile(): List<Game>? {
            return try {
                val fis = context.openFileInput("gameList")
                val ois = ObjectInputStream(fis)
                @Suppress("UNCHECKED_CAST")
                ois.readObject() as? List<Game>
            } catch (e: EOFException) {
                e.printStackTrace()
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


        fun saveGameFile(mutableGameList: MutableList<Game>): Boolean{
            try {
                val fos = context.openFileOutput("gameList", MODE_PRIVATE)
                val oos = ObjectOutputStream(fos)
                oos.writeObject(mutableGameList)
                oos.close()
            }
            catch(e: IOException){
                e.printStackTrace()
                return false
            }
            return true
        }
        if (gameFile.exists()) {
            Game.gameList.clear()
            Game.gameList.addAll(getGameFile() ?: emptyList())
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
                .background(color = CuriousBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            //Title text
            item { Row (verticalAlignment = Alignment.CenterVertically){
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
                Text(text = "Game Input", fontSize = 40.sp, modifier = Modifier.padding(vertical = 20.dp))
            }   }
            item {
                //Text inputs below
                LazyRow (modifier = Modifier.padding(vertical = 20.dp))
                {
                    item{ TextField(value = gameName, onValueChange = {gameName = it},
                        label = { Text("Game Name") })
                    }
                }
            }
            item {
                LazyRow (modifier = Modifier.padding(vertical = 20.dp))
                {
                    item{ TextField(value = gameDesc, onValueChange = {gameDesc = it},
                        label = { Text("Game Description") })
                    }
                }
            }
            item { LazyRow (modifier = Modifier.padding(vertical = 20.dp))
            {
                item{ TextField(value = gameTime, onValueChange = {gameTime = it},
                    label = { Text("In Game Time") })
                }
            } }
            item { LazyRow (modifier = Modifier.padding(vertical = 20.dp))
            {
                item{ TextField(value = gameDate, onValueChange = {gameDate = it},
                    label = { Text("Release Date") })
                }
            } }
            item {
                LazyRow (modifier = Modifier.padding(vertical = 20.dp))
            {
                item{ TextField(value = gamePlatform, onValueChange = {gamePlatform = it},
                    label = { Text("Game Platform") })
                }
            }  }
            //Buttons
            item { LazyRow {
                item {
                    Button(onClick = { context.startActivity(Intent(context, LibraryActivity::class.java)) },
                        colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                        Text(text = "Back",color = Color.Black)

                    } }
                //On click we used the method I made to convert the string inputs to a gameDB object
                item { Button(onClick = { val gameInput = GameUtil.InputToGame(gameName, gameDesc, gameTime, gameDate, gamePlatform)
                    //Now we add the game to the list
                    Game.gameList.add(gameInput)
                    saveGameFile(Game.gameList)
                    context.startActivity(Intent(context, LibraryActivity::class.java))
                },
                    colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                    Text(text = "Confirm Inputs",color = Color.Black)


                } }
            } }


        }

    }
}