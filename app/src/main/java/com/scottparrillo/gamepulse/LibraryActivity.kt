package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.wear.compose.material.MaterialTheme.colors
import com.scottparrillo.gamepulse.ui.theme.Charcoal
import com.scottparrillo.gamepulse.ui.theme.CopperRose
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.LightBlue
import com.scottparrillo.gamepulse.ui.theme.Lime
import com.scottparrillo.gamepulse.ui.theme.NeonLightGreen
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

    @Preview(showBackground = true)
    @Composable
    fun LibraryScreen() {
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val gameList = remember { mutableStateListOf<Game>() }

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
            gameList.addAll(getGameFile() ?: emptyList())
        }

        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Charcoal)
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
                            onBackPressedDispatcher?.onBackPressed()
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Library Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                    gameList.clear()
                    gameList.addAll(sortedList)

                },colors = ButtonDefaults.buttonColors(containerColor = LightBlue)) {
                    Text(text = "Sort")
                }

                Button(onClick = {
                    context.startActivity(Intent(context, GameInputActivity::class.java))
                },colors = ButtonDefaults.buttonColors(containerColor = LightBlue)) {
                    Text(text = "Add Game")
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
                .clip(RoundedCornerShape(10.dp))
                .background(color = NeonLightGreen)
        ) {
            Text(text = text)
        }


    }
}
