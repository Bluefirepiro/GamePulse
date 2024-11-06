package com.scottparrillo.gamepulse
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.Lime
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDateTime
import kotlin.concurrent.thread

class LibraryActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
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

    Adjust home screen button to be same size as game import (Done)
    Make star aligned with image, as right now it leads to confusion about what game it's for (Done?)
    616 px x 353 px for capsule
    Center Search button on Add Game Button
    Align search icon with game import button (Done)
    Center text and add static size to the three main buttons (Done)

    Center image (Done)
    Add border to image (Done)
    Make dedicated word box for word wrap desc
    give score based on achievements possibly based on rarity
    have a score based on all consoles and platforms
    grand/meta/omni score based on pc and xbox
    Scroll till lock on single game detail screen (Done)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalFoundationApi::class)
    @Preview(showBackground = true)
    @ExperimentalMaterial3Api
    @Composable
    fun LibraryScreen() {

        //Setting may val and vars for future use
        val dialogFlag = rememberSaveable { mutableStateOf(false) }
        val toastSearchFlag = rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val gameList = remember { mutableStateListOf<Game>() }
        //This is for the search bar
        var searchText by rememberSaveable { mutableStateOf("") } //Search bar text
        val searchFlag = rememberSaveable { mutableStateOf(false) }
        //Setting up my fonts
        val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
        val joseFin = FontFamily(Font(R.font.josefin_slab_variablefont_wght))
        val  kdam = FontFamily(Font(R.font.kdam_thmorpro_regular))
        //Gameinfo

        //Setting up drop down menu
        var expandedDrop by remember { mutableStateOf(false) }
        //Button sizes
        val mainButtonSize = 60.dp
        val mainButtonCut = 10.dp
        val mainImageSize = 48.dp
        val optionsButtonSizeWidth = 115.dp
        val optionsButtonSizeHeight = 40.dp
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
        //Some Helper Functions
        fun SearchList(){
            var findMark = false
            val tempMutableList = mutableListOf<Game>()
            if (searchText.contains("\n")) {
                searchText = searchText.replace("\n", "")
            }
            thread(true){
                for (game in Game.gameList) {
                    if (game.gameName.contains(searchText, ignoreCase = true)) {
                        tempMutableList.add(game)
                        findMark = true
                        searchFlag.value = true
                    }
                }
                if (findMark) {
                    gameList.clear()
                    gameList.addAll(tempMutableList)
                } else if (searchText == "") {
                    gameList.clear()
                    gameList.addAll(Game.gameList)
                } else {
                    toastSearchFlag.value = true
                }
            }
            when {toastSearchFlag.value ->
            {
                if(toastSearchFlag.value){
                    val toast = Toast.makeText(
                        context, "Name not found", Toast.LENGTH_SHORT
                    )
                    toast.show()
                    toastSearchFlag.value = true
                }
            }}
        }

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

        if (gameFile.exists() && !searchFlag.value) {
            if (gameList.size != getGameFile()?.size) {
                gameList.clear()
                gameList.addAll(getGameFile() ?: emptyList())

            }
            if (Game.gameList.size != getGameFile()?.size){
                Game.gameList.clear()
                Game.gameList.addAll(getGameFile() ?: emptyList())
            }

        }
        val librarySize = Game.gameList.size
        //Setting up a dialog alert
        when {
            dialogFlag.value -> {
                AlertDialog(onDismissRequest = { dialogFlag.value = false }, confirmButton = {
                    TextButton(onClick = {
                        gameList.clear()
                        Game.gameList.clear()
                        Game.gameList.addAll(gameList)
                        saveGameFile(Game.gameList)
                        dialogFlag.value = false }) {
                        Text(text = "Confirm")

                    }

                },
                    title = { Text(text = "Delete all games?")},
                    dismissButton = {
                        TextButton(onClick = { dialogFlag.value = false }) {
                            Text(text = "Dismiss")
                        }
                    })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.background(color = CuriousBlue)
                .background(color = Color.Black)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 0.dp, horizontal = 0.dp)
                    .fillMaxWidth()
                    .size(width = 100.dp, height = 100.dp)
                    .background(color = Color.Black),
                horizontalArrangement = Arrangement.SpaceBetween



            ) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(mainButtonCut))
                    .size(mainButtonSize)
                    .background(Lime)
                    .clickable {
                        context.startActivity(
                            Intent(
                                context,
                                MainActivity::class.java
                            )
                        )
                    },
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.home_icon),
                        contentDescription = "Back arrow",
                        modifier = Modifier
                            .size(mainImageSize)
                            .padding(4.dp)
                    )
                }

                Text(
                    text = "Library Screen",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = jockeyOne,
                    fontSize = 40.sp,
                    color = Lime


                )
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(mainButtonCut))
                    .size(mainButtonSize)
                    .background(Lime)
                    .clickable {
                        context.startActivity(Intent(context, GameImportActivity::class.java))
                    },
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.cloud_download_icon),
                        contentDescription = "Game Import Button",
                        modifier = Modifier
                            .size(mainImageSize)
                            .padding(4.dp)
                    )
                }

            }
            //This row holds the search bar and button
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 4.dp)
                    .background(color = Color.Black)) {
                TextField(
                    value = searchText, onValueChange = { searchText = it },
                    label = { Text("Search Game") },
                    modifier = Modifier
                        .size(width = 280.dp, height = 50.dp)
                        //.requiredSize(width = 291.dp, height = 50.dp)
                        .onKeyEvent {
                            if (it.key == Key.Enter) {
                                SearchList()
                                return@onKeyEvent true
                            } else {
                                return@onKeyEvent false
                            }


                        },
                )
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(mainButtonCut))
                    .requiredSize(mainButtonSize)
                    .size(mainButtonSize)
                    .background(Lime)
                    .clickable {
                        SearchList()
                    },
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.magnifier_glass_icon),
                        contentDescription = "Magnifying Glass",
                        modifier = Modifier
                            .size(mainImageSize)
                            .padding(4.dp)
                    )
                }
            }
            LazyRow(
                modifier = Modifier
                    .padding(vertical = 0.dp, horizontal = 0.dp)
                    .background(color = Color.Black)
            ) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { /* Handle Category clicks */
                                val sortedList =
                                    gameList
                                        .sortedBy { it.dateTimeLastPlayed }
                                        .toMutableList()
                                sortedList.reverse()
                                gameList.clear()
                                gameList.addAll(sortedList)
                            }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(5.dp, 5.dp, 0.dp, 0.dp))
                            .background(color = SpringGreen),


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
                            .clickable {/* Handle Category clicks */
                                thread(start = true) {
                                    val sortedList = mutableListOf<Game>()
                                    for (game in gameList) {
                                        if (LocalDateTime.now().dayOfYear == game.dateTimeLastPlayed.dayOfYear) {
                                            sortedList.add(game)
                                        }
                                    }
                                    gameList.clear()
                                    gameList.addAll(sortedList)
                                }
                            }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(5.dp, 5.dp, 0.dp, 0.dp))
                            .background(color = SpringGreen)
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
                            .clickable {/* Handle Category clicks */
                                thread(start = true) {
                                    val sortedList = gameList
                                        .sortedBy { it.allAchiev }
                                        .toMutableList()
                                    gameList.clear()
                                    gameList.addAll(sortedList)
                                }

                            }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(5.dp, 5.dp, 0.dp, 0.dp))
                            .background(color = SpringGreen)
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
                            .clickable { /* Handle Category clicks */
                                thread(start = true) {
                                    val sortedList = mutableListOf<Game>()
                                    for (game in Game.gameList) {
                                        if (game.newlyAdded)
                                            sortedList.add(game)
                                    }
                                    gameList.clear()
                                    gameList.addAll(sortedList)
                                }
                            }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(5.dp, 5.dp, 0.dp, 0.dp))
                            .background(color = SpringGreen)
                    ) {
                        Text(text = "New")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 0.dp)
                    .fillMaxWidth()
                    .background(color = Color.Black),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(mainButtonCut))
                    .size(width = 60.dp, height = 40.dp)
                    .background(Lime)
                    .clickable {
                        expandedDrop = true
                    },
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.descending_filter_icon),
                        contentDescription = "Delete Button",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(4.dp)
                    )
                }
                Text(text = "$librarySize Games",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = kdam,
                    fontSize = 30.sp,
                    color = Lime)
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(mainButtonCut))
                    .size(width = 60.dp, height = 40.dp)
                    .background(Lime)
                    .clickable {
                        dialogFlag.value = true
                    },
                    contentAlignment = Alignment.Center){
                    Image(
                        painter = painterResource(id = R.drawable.delete_icon),
                        contentDescription = "Delete Button",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(4.dp)
                    )
                }
                DropdownMenu(expanded = expandedDrop, onDismissRequest = { expandedDrop = false }) {

                    DropdownMenuItem(text = { Text(text = "Console") }, onClick = {
                        thread(start = true){
                            val sortedList = gameList.sortedBy { it.gamePlatform }.toMutableList()
                            gameList.clear()
                            gameList.addAll(sortedList)
                        }

                    })
                    DropdownMenuItem(text = { Text(text = "Alphabetically") }, onClick = {
                        thread(start = true){
                            val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                            gameList.clear()
                            gameList.addAll(sortedList)
                        }

                    })
                    DropdownMenuItem(text = { Text(text = "Time Spent") }, onClick = {
                        thread (start = true){
                            val sortedList = gameList.sortedBy { it.gameTime }.toMutableList()
                            sortedList.reverse()
                            gameList.clear()
                            gameList.addAll(sortedList)
                        }

                    })
                }
            }
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 140.dp),
                modifier = Modifier
                    .background(CuriousBlue)
                    .padding(horizontal = 0.dp)) {
                items(gameList) { game ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        // Game Icon
                        AsyncImage(model = ImageRequest.Builder(context)
                            .data(game.coverURL)
                            .build(), contentScale = ContentScale.Fit,
                            placeholder = painterResource(R.drawable.plus),
                            contentDescription = "The cover of a game",
                            modifier = Modifier
                                .size(height = 112.dp, width = 195.dp)
                                .clip(RectangleShape)
                                .combinedClickable(
                                    enabled = true,
                                    onLongClick = {
                                        //Rectangle two games per row
                                        gameList.remove(game)
                                        Game.gameList.clear()
                                        Game.gameList.addAll(gameList)
                                        saveGameFile(Game.gameList)
                                    },
                                    onClick = {
                                        Game.selectedGame = game
                                        context.startActivity(
                                            Intent(
                                                context,
                                                SingleGameActivity::class.java
                                            )
                                        )
                                    }),
                        )
                        // Favorite Icon
                        val favoriteIcon =
                            if (game.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                        Image(
                            painter = painterResource(id = favoriteIcon),
                            contentDescription = "Favorite icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable {
                                    // Toggle favorite state
                                    game.isFavorite = !game.isFavorite
                                    gameList[gameList.indexOf(game)] = game
                                    saveGameFile(gameList)
                                }
                        )
                        Text(text = game.gameName)
                    }
                }
            }
        }
        //End of main column

    }
}