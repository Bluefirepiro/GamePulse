package com.scottparrillo.gamepulse

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.io.EOFException
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

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

    @ExperimentalMaterial3Api
    @Composable
    fun LibraryScreen() {
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val gameList = remember { mutableStateListOf<Game>() }
        val searchText = remember { mutableStateOf("") }
        val searchFlag = remember { mutableStateOf(false) }
        val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))

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

        fun saveGameList(gameList: List<Game>) {
            val fos = context.openFileOutput("gameList", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(gameList)
            oos.close()
            fos.close()
        }

        if (gameFile.exists()) {
            gameList.addAll(getGameFile() ?: emptyList())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {

            // Header Row
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
                        .clickable { context.startActivity(Intent(context, MainActivity::class.java)) }
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

            // Search Bar
            SearchBar(
                query = searchText.value,
                onQueryChange = { searchText.value = it },
                onSearch = { searchFlag.value = false },
                active = searchFlag.value,
                onActiveChange = { searchFlag.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(horizontal = 4.dp),
                shape = RectangleShape
            ) {}

            // Category Buttons Row
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

            // Sort and Add Game Buttons
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                        gameList.clear()
                        gameList.addAll(sortedList)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                ) {
                    Text(text = "Sort", color = Color.Black)
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, GameInputActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                ) {
                    Text(text = "Add Game", color = Color.Black)
                }
            }

            // Favorite and Regular Games Section
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
                // Favorites Section
                val favoriteGames = gameList.filter { it.isFavorite }
                if (favoriteGames.isNotEmpty()) {
                    item {
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(favoriteGames) { game ->
                        GameItem(game, gameList, context)
                    }
                }

                // Regular Games Section
                val nonFavoriteGames = gameList.filter { !it.isFavorite }
                if (nonFavoriteGames.isNotEmpty()) {
                    item {
                        Text(
                            text = "All Games",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(nonFavoriteGames) { game ->
                        GameItem(game, gameList, context)
                    }
                }
            }
        }
    }

    @Composable
    fun GameItem(game: Game, gameList: MutableList<Game>, context: Context) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = game.gameName, modifier = Modifier.weight(1f))

                // Favorite button
                val favoriteIcon = if (game.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                Image(
                    painter = painterResource(id = favoriteIcon),
                    contentDescription = "Favorite Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            game.isFavorite = !game.isFavorite
                            saveGameList(gameList)
                        }
                )
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
    private fun toggleFavorite(game: Game) {
        game.isFavorite = !game.isFavorite
        saveGameList(Game.gameList) // Save the updated game list
    }

    private fun saveGameList(gameList: List<Game>) {
        val context = this
        try {
            val fos = context.openFileOutput("gameList", MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(gameList)
            oos.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
