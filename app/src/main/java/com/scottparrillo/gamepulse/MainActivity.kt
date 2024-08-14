package com.scottparrillo.gamepulse

import android.app.Activity.MODE_PRIVATE
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Strings
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room.databaseBuilder
import com.scottparrillo.gamepulse.ui.theme.CopperRose
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.Lime
import com.scottparrillo.gamepulse.ui.theme.PrussainBlue
import kotlinx.coroutines.flow.count
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        //This builds our instance of the database



        setContent {
            GamePulseTheme {
                HomeScreen(
                    onNavigateToAchievements = {
                        startActivity(Intent(this, AchievementActivity::class.java))
                    },
                    onNavigateToLibrary = {
                        startActivity(Intent(this, LibraryActivity::class.java))
                    }

                )
            }
        }
    }

}

@Composable
fun HomeScreen(onNavigateToAchievements: () -> Unit, onNavigateToLibrary: () -> Unit) {
    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top row with profile picture, notifications, and settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_picture_placeholder),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )

            Row {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* TODO: Handle notifications click */ }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* TODO: Handle settings click */ }
                )
            }
        }

        // App name
        Text(
            text = "GamePulse",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Recently Played games
        Text(
            text = "Recently Played",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            repeat(10) { index ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { /* TODO: Handle Recently Played game click */ }
                        .padding(8.dp)
                ) {
                    Text("Game $index")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Recent Achievements
        Text(
            text = "Recent Achievements",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            repeat(10) { index ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable { /* TODO: Handle Recent Achievement click */ }
                        .padding(8.dp)
                ) {
                    Text("Achievement $index")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Recent Friends/Played with
        Text(
            text = "Recent Friends/Played With",
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            repeat(10) { index ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { /* TODO: Handle Recent Friend click */ }
                        .padding(8.dp)
                ) {
                    Text("Friend $index")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateToAchievements) {
                Text(text = "Achievements")
            }

            Button(onClick = onNavigateToLibrary) {
                Text(text = "Library")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GamePulseTheme {
        HomeScreen(onNavigateToAchievements = {}, onNavigateToLibrary = {})
    }
}

// This is the Start of the Library Screen
@Preview(showBackground = true)
@Composable
fun LibraryScreen() {
    val context = LocalContext.current
    val gameFile = File(context.filesDir, "gameList")
    var tempList = mutableListOf<Game>()
    var sortListGame = remember { mutableStateListOf<Game>()}


    //This functions returns a mutablelist of games from the saved gameList
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

    if(gameFile.exists())
    {
        Game.gameList = getGameFile()!!;
        sortListGame.addAll(getGameFile()!!)
    }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

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
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick =  {
                tempList.addAll(sortListGame)
                Game.gameList.sortedBy {it.gameName}
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
            Button(onClick =  { context.startActivity(Intent(context, GameInputActivity::class.java))}) {
                Text(text = "Add Game")
            }
        }


        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
            items(sortListGame){game ->
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
//This is going to be a series of text fields for the user to input game details in
@Preview(showBackground = true)
@Composable
fun GameInputScreen()
{

    val context = LocalContext.current
    //These are the variables for the text inputs
    var gameName by remember { mutableStateOf("") }
    var gameDesc by remember { mutableStateOf("") }
    var gameTime by remember { mutableStateOf("") }
    var gameDate by remember { mutableStateOf("") }
    var gamePlatform by remember { mutableStateOf("") }
    //Going to use this to input user data into the database
//This function saves games to a file named gameList
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(color = PrussainBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        //Title text
        item { Text(text = "Game Input", fontSize = 40.sp, modifier = Modifier.padding(vertical = 20.dp))  }
        item {
            //Text inputs below
            LazyRow (modifier = Modifier.padding(vertical = 20.dp))
        {
            item{ TextField(value = gameName, onValueChange = {gameName = it},
                label = {Text("Game Name")})}
        }}
        item {
            LazyRow (modifier = Modifier.padding(vertical = 20.dp))
            {
                item{ TextField(value = gameDesc, onValueChange = {gameDesc = it},
                    label = {Text("Game Description")})}
            }
        }
        item { LazyRow (modifier = Modifier.padding(vertical = 20.dp))
        {
            item{ TextField(value = gameTime, onValueChange = {gameTime = it},
                label = {Text("In Game Time")})}
        } }
        item { LazyRow (modifier = Modifier.padding(vertical = 20.dp))
        {
            item{ TextField(value = gameDate, onValueChange = {gameDate = it},
                label = {Text("Release Date")})}
        } }
        item {LazyRow (modifier = Modifier.padding(vertical = 20.dp))
        {
            item{ TextField(value = gamePlatform, onValueChange = {gamePlatform = it},
                label = {Text("Game Platform")})}
        }  }
        //Buttons
        item { LazyRow() {
            item {
                Button(onClick = { context.startActivity(Intent(context, LibraryActivity::class.java)) }) {
                    Text(text = "Back")

            } }
                //On click we used the method I made to convert the string inputs to a gameDB object
            item { Button(onClick = { val gameInput = GameUtil.InputToGame(gameName, gameDesc, gameTime, gameDate, gamePlatform)
                //Now we add the game to the list
                Game.gameList.add(gameInput)
                saveGameFile(Game.gameList)
                context.startActivity(Intent(context, LibraryActivity::class.java))
            }) {
                Text(text = "Confirm Inputs")

                
            } }
        } }


    }

}



