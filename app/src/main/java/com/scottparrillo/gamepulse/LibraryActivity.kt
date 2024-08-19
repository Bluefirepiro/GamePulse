package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream

class LibraryActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                LibraryScreen()
            }
        }
    }
    /*
    Green Background with white text
    Pull color scheme from logo using adobe color
    Important for everything to be readable and have contrast
    Check for color blindness
    Even with mono make sure you still have the important stuff pop
     */
    @Preview(showBackground = true)
    @ExperimentalMaterial3Api
    @Composable
    fun LibraryScreen() {
        //Setting may val and vars for future use
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val gameList = remember { mutableStateListOf<Game>() }
        //This is for the search bar
        val searchText = rememberSaveable { mutableStateOf("")} //Search bar text
        val searchFlag = rememberSaveable { mutableStateOf(false)}
        //Setting up my fonts
        val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
        //val joseFin = FontFamily(Font(R.font.josefin_slab_variablefont_wght))
        //val  kdam = FontFamily(Font(R.font.kdam_thmorpro_regular))
        //Setting up drop down menu
        var expandedDrop by remember { mutableStateOf(false) }

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

        if (gameFile.exists()) {
            if(gameList.size != getGameFile()?.size){
                gameList.addAll(getGameFile() ?: emptyList())
            }

        }

        /*val onBackPressedDispatcher =
           LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher */

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.homeicon),
                    contentDescription = "Back arrow",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(65.dp)
                        .clickable {
                            context.startActivity(
                                Intent(
                                    context,
                                    MainActivity::class.java
                                )
                            )
                        }
                )
                Text(
                    text = "Library Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    fontFamily = jockeyOne,
                    fontSize = 40.sp
                )
            }
            Row(){
                SearchBar(
                    query = searchText.value,
                    onQueryChange = {searchText.value = it},
                    onSearch = {searchFlag.value = false},
                    active = searchFlag.value,
                    onActiveChange = {searchFlag.value = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 30.dp)
                        .padding(horizontal = 4.dp),
                    shape = RectangleShape
                ) {

                }
            }

            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                item {
                    CategoryButton("Recent")
                }
                item {
                    CategoryButton("Current")
                }
                item {
                    CategoryButton("Beaten")
                }
                item {
                    CategoryButton("New")
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {

                    Button(onClick = { expandedDrop = true }) {
                        Text(text = "Sort")
                    }
                    DropdownMenu(expanded = expandedDrop, onDismissRequest = { expandedDrop = false }) {
                        DropdownMenuItem(text = { Text(text = "Recent") }, onClick = {
                            gameList.clear()
                            gameList.addAll(Game.gameList)
                        })
                        DropdownMenuItem(text = { Text(text = "Alphabetically") }, onClick = {
                            val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                            gameList.clear()
                            gameList.addAll(sortedList)
                        })
                        DropdownMenuItem(text = { Text(text = "Time Spent") }, onClick = {
                            val sortedList = gameList.sortedBy { it.gameTime.toInt() }.toMutableList()
                            gameList.clear()
                            gameList.addAll(sortedList)
                        })
                    }

                /*Button(onClick = {
                    val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                    gameList.clear()
                    gameList.addAll(sortedList)

                },colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                    Text(text = "Sort",color = Color.Black)
                } */

                Button(onClick = {
                    context.startActivity(Intent(context, GameInputActivity::class.java))
                },colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                    Text(text = "Add Game",color = Color.Black)
                }
            }

            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
                items(gameList) { game ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Game icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(135.dp)
                                .clip(CircleShape)
                        )
                        Text(text = game.gameName)
                    }
                }
            }
        }
    }

        @Composable
    fun CategoryButton(text: String) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(120.dp)
                .height(31.dp)
                .clickable { /* Handle Category clicks */ }
                .padding(2.dp)

                .background(color = SpringGreen)
        ) {
            Text(text = text)
        }


    }


}
