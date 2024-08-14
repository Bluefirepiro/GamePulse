package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scottparrillo.gamepulse.ui.theme.CopperRose
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.Lime
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream

class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                LibraryScreen()
            }
        }
    }

    // This is the Start of the Library Screen
    @Preview(showBackground = true)
    @Composable
    fun LibraryScreen() {
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val tempList = mutableListOf<Game>()
        val sortListGame = remember { mutableStateListOf<Game>() }


        //This functions returns a mutable list of games from the saved gameList
        fun getGameFile(): MutableList<Game>? {
            try {

                val fis = context.openFileInput("gameList")
                val ois = ObjectInputStream(fis)
                val gameList = ois.readObject()
                ois.close()
                if (gameList != null) {
                    @Suppress("UNCHECKED_CAST")
                    return gameList as MutableList<Game>
                }
            } catch (e: EOFException) {
                e.printStackTrace()
            }
            return null
        }

        if (gameFile.exists()) {
            Game.gameList = getGameFile()!!
            sortListGame.addAll(getGameFile()!!)
        }

        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        //When the screen activity starts extract needed info and throw it in a list


        // Main column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CopperRose)
        ) {
            // Title text with a clickable back arrow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "A back arrow",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(65.dp)
                        .padding(8.dp)
                        .clickable {
                            //onBackPressedDispatcher?.onBackPressed() // Handle the back press
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }
                )


                Text(
                    text = "Library Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // This contains the game categories
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { /* TODO: Handle Category clicks */ }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Lime)
                    ) {
                        Text(text = "Recent")
                    }
                }
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { /* TODO: Handle Category clicks */ }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Lime)
                    ) {
                        Text(text = "Current")
                    }
                }
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Lime)
                    ) {
                        Text(text = "Beaten")
                    }
                }
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { /* TODO: Handle Category clicks */ }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Lime)
                    ) {
                        Text(text = "New")
                    }
                }
            }

            // This row should contain the sort icon
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    tempList.addAll(sortListGame)
                    Game.gameList.sortedBy { it.gameName }
                    sortListGame.clear()
                    sortListGame.addAll(tempList)


                }) {
                    Text(text = "Sort")
                }


                /* Image(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = "Sorting Arrow",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { sortListGame.sortedBy { it.gameName } }


            )*/
                Spacer(modifier = Modifier.width(180.dp))
                Button(onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            GameInputActivity::class.java
                        )
                    )
                }) {
                    Text(text = "Add Game")
                }
            }


            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
                items(sortListGame) { game ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "A plus",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(135.dp)
                                .clip(CircleShape)
                        )
                        Text(text = game.gameName)
                        Text(text = "Update fill")
                    }
                }


                /*item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.cyberpunk_2077_cover),
                        contentDescription = "A cover of the game Cyberpunk 2022",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(135.dp)
                            .clip(CircleShape)
                    )
                    Text(text = "Cyber Punk 2022")
                    Text(text = "Update fill")
                }
            }
            item { Text(text = "test") }
            item { Text(text = "test") } */
            }
        }
    }
}