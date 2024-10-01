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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

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
    1. Rescale images for game covers
    2. Use blocking threads for api calls
    3. Work on single game screen
    616 px x 353 px for capsule
    Center Search button on Add Game Button
    Round top two corners on square buttons
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalFoundationApi::class)
    @Preview(showBackground = true)
    @ExperimentalMaterial3Api
    @Composable
    fun LibraryScreen() {
        //Setting may val and vars for future use
        val context = LocalContext.current
        val gameFile = File(context.filesDir, "gameList")
        val gameList = remember { mutableStateListOf<Game>() }
        //This is for the search bar
        var searchText by rememberSaveable { mutableStateOf("") } //Search bar text
        val searchFlag = rememberSaveable { mutableStateOf(false) }
        //Setting up my fonts
        val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
        //val joseFin = FontFamily(Font(R.font.josefin_slab_variablefont_wght))
        //val  kdam = FontFamily(Font(R.font.kdam_thmorpro_regular))
        //Setting up drop down menu
        var expandedDrop by remember { mutableStateOf(false) }
        /*I have this below to show an example of how to make a call
        this can be commented away when not used for testing
         */

        /*
       val call = SteamRetrofit.apiSteam.apiS.getAllAchievementPercentages("4A7BFC2A3443A093EA9953FD5529C795", 1158310, "json" )
        call.enqueue(object: Callback<SteamAchievementPercentages>{
            override fun onResponse(
                call: Call<SteamAchievementPercentages>,
                response: Response<SteamAchievementPercentages>
            ) {
                if(response.isSuccessful)
                {
                   val post = response.body()!!


                    Log.v("api", post.toString())
                   // Log.v("api", post.achievementpercentages[0].achievements[0].name)
                }
            }

            override fun onFailure(p0: Call<SteamAchievementPercentages>, p1: Throwable) {
                p1.printStackTrace()
            }

        })

         */
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
                Game.gameList.clear()
                Game.gameList.addAll(getGameFile() ?: emptyList())
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp),

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
                Image(
                    painter = painterResource(id = R.drawable.gameimport),
                    contentDescription = "Magnifying Glass",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(98.dp)
                        .padding(horizontal = 2.dp)
                        .clickable {
                            context.startActivity(Intent(context, GameImportActivity::class.java))
                        }
                )
            }
            //This row holds the search bar and button
            Row() {
                TextField(
                    value = searchText, onValueChange = { searchText = it },
                    label = { Text("Search Game") },
                    modifier = Modifier
                        .size(width = 280.dp, height = 46.dp)
                        .padding(horizontal = 8.dp),
                )

                    Image(
                        painter = painterResource(id = R.drawable.searchicon),
                        contentDescription = "Magnifying Glass",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                            .clickable {

                                val tempMutableList = mutableListOf<Game>()
                                var findMark = false
                                for (game in Game.gameList) {
                                    if (game.gameName.contains(searchText, ignoreCase = true)) {
                                        tempMutableList.add(game)
                                        findMark = true
                                        searchFlag.value = true
                                    } else if (searchText == "") {
                                        gameList.clear()
                                        gameList.addAll(Game.gameList)
                                    }

                                }
                                if (findMark) {
                                    gameList.clear()
                                    gameList.addAll(tempMutableList)
                                }
                                else if(searchText == ""){
                                    gameList.clear()
                                    gameList.addAll(Game.gameList)
                                }
                                else {
                                    val toast = Toast.makeText(
                                        context, "Name not found", Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                }


                            }
                    )
            }

            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp)
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

                            .background(color = SpringGreen)
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
                            .clickable { /* Handle Category clicks */
                                val sortedList =
                                    gameList
                                        .sortedBy { it.currentlyPlaying }
                                        .toMutableList()
                                gameList.clear()
                                gameList.addAll(sortedList)
                            }
                            .padding(2.dp)

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
                            .clickable { /* Handle Category clicks */
                                val sortedList = gameList
                                    .sortedBy { it.completed }
                                    .toMutableList()
                                gameList.clear()
                                gameList.addAll(sortedList)
                            }
                            .padding(2.dp)

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
                                var sortedList = mutableListOf<Game>()
                                for(game in Game.gameList)
                                {
                                    if(game.newlyAdded)
                                        sortedList.add(game)
                                }


                                gameList.clear()
                                gameList.addAll(sortedList)
                            }
                            .padding(2.dp)

                            .background(color = SpringGreen)
                    ) {
                        Text(text = "New")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {

                Button(
                    onClick = { expandedDrop = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                ) {
                    Text(text = "Sort", color = Color.Black)
                }
                Button(onClick = {
                    gameList.clear()
                    Game.gameList.clear()
                    Game.gameList.addAll(gameList)
                    saveGameFile(Game.gameList)
                }, colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                    Text(text = "Clear All", color = Color.Black)
                }
                DropdownMenu(expanded = expandedDrop, onDismissRequest = { expandedDrop = false }) {
                    DropdownMenuItem(text = { Text(text = "Console") }, onClick = {
                        val sortedList = gameList.sortedBy { it.gamePlatform }.toMutableList()
                        gameList.clear()
                        gameList.addAll(sortedList)
                    })
                    DropdownMenuItem(text = { Text(text = "Alphabetically") }, onClick = {
                        val sortedList = gameList.sortedBy { it.gameName }.toMutableList()
                        gameList.clear()
                        gameList.addAll(sortedList)
                    })
                    DropdownMenuItem(text = { Text(text = "Time Spent") }, onClick = {
                        val sortedList = gameList.sortedBy { it.gameTime }.toMutableList()
                        sortedList.reverse()
                        gameList.clear()
                        gameList.addAll(sortedList)
                    })
                }


                Button(onClick = {
                    context.startActivity(Intent(context, GameInputActivity::class.java))
                }, colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)) {
                    Text(text = "Add Game", color = Color.Black)
                }
            }

            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 180.dp)) {
                items(gameList) { game ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Favorite Icon
                        val favoriteIcon =
                            if (game.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                        Image(
                            painter = painterResource(id = favoriteIcon),
                            contentDescription = "Favorite icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    // Toggle favorite state
                                    game.isFavorite = !game.isFavorite
                                    gameList[gameList.indexOf(game)] = game
                                    saveGameFile(gameList)
                                }
                            )

                        // Game Icon
                        if(game.coverURL == "") {
                            Image(
                                painter = painterResource(id = R.drawable.plus),
                                contentDescription = "Game icon",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(135.dp)
                                    .clip(CircleShape)
                                    .combinedClickable(
                                        enabled = true,
                                        onLongClick = {
                                            gameList.remove(game)
                                            Game.gameList.clear()
                                            Game.gameList.addAll(gameList)
                                            saveGameFile(Game.gameList)
                                        },
                                        onClick = {
                                            // Handle single click if needed
                                        }
                                    )
                                )
                        }
                        else{
                                AsyncImage(model = game.coverURL, contentScale = ContentScale.Fit,
                                    contentDescription = "The cover of a game",
                                    modifier = Modifier
                                        //size(235.dp)
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
                                            onClick ={
                                                Game.selectedGame = game
                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        SingleGameActivity::class.java
                                                    )
                                                )

                                            } ),
                                )
                        }
                        Text(text = game.gameName)
                    }
                }
            }
        }
    }
}
