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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.api.ApiClient
import com.scottparrillo.gamepulse.com.scottparrillo.gamepulse.XuidResponse
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.Lime
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import com.scottparrillo.gamepulse.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.ObjectOutputStream
import java.time.Instant
import java.time.ZoneId


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

    fun getXuidFromGamertag(gamertag: String): XuidResponse.Person? {
        try {
            val call = ApiClient.openXBL.xboxWebAPIClient.getXuidFromGamertag(gamertag)
            val response = call.execute()
            if (response.isSuccessful) {
                val person = response.body()?.people?.firstOrNull()
                if (person != null) {
                    Log.d("GameImportActivity", "XUID retrieved: ${person.xuid}")
                    return person
                } else {
                    Log.e("GameImportActivity", "No person found for gamertag: $gamertag")
                }
            } else {
                Log.e(
                    "GameImportActivity",
                    "Error retrieving XUID: ${response.errorBody()?.string()}"
                )
                Log.e("GameImportActivity", "Response code: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    @Preview(showBackground = true)
    @Composable
    fun GameImportScreen() {
        val steamKey = Constants.STEAM_API_KEY
        var currentProgress by rememberSaveable { mutableStateOf(0f) }
        var threeStepCurrentProgress by rememberSaveable { mutableStateOf(0f) }
        var loading by rememberSaveable { mutableStateOf(false) }
        var steamIdText by rememberSaveable { mutableStateOf("") }
        var steamId by rememberSaveable { mutableStateOf("") }
        var dialogFlag = rememberSaveable { mutableStateOf(false) }
        var xboxIdText by rememberSaveable { mutableStateOf("") }
        var xboxId by rememberSaveable { mutableStateOf("") }
        // val enterFlag = rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val view = LocalView.current
        val mainButtonSize = 60.dp
        val mainButtonCut = 10.dp
        val mainImageSize = 48.dp
        view.keepScreenOn = true
        //fonts
        val jockeyOne = FontFamily(Font(R.font.jockey_one_regular))
        val joseFin = FontFamily(Font(R.font.josefin_slab_variablefont_wght))
        val kdam = FontFamily(Font(R.font.kdam_thmorpro_regular))
        //These are the variables for the text inputs
        var gameNameM by remember { mutableStateOf("") }
        var gameDescM by remember { mutableStateOf("") }
        var gameTimeM by remember { mutableStateOf("") }
        var gameDateM by remember { mutableStateOf("") }
        var gamePlatformM by remember { mutableStateOf("") }
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
        //Setting up a dialog alert
        when {
            dialogFlag.value -> {
                AlertDialog(onDismissRequest = { dialogFlag.value = false }, confirmButton = {
                },
                    title = { Text(text = "Help") },
                    text = {
                        Text(
                            text = "If steam is not importing correctly please make sure your " +
                                    "profile is set to public\n" +
                                    "If your steam ID is not showing go to your " +
                                    "steam profile edit profile and delete your custom URL"
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogFlag.value = false }) {
                            Text(text = "Dismiss")
                        }
                    })
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CuriousBlue)
                .padding(horizontal = 0.dp)
        ) {
            item {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 0.dp)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(mainButtonCut))
                                .size(mainButtonSize)
                                .background(Lime)
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            LibraryActivity::class.java
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.home_icon),
                                contentDescription = "Back arrow",
                                modifier = Modifier
                                    .size(mainImageSize)
                                    .padding(4.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "Game Import", fontSize = 40.sp,
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 8.dp),
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = jockeyOne,
                            color = Lime
                        )
                    }
                    item {

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(mainButtonCut))
                                .size(mainButtonSize)
                                .background(Lime)
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    dialogFlag.value = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.round_information_icon),
                                contentDescription = "question mark",
                                modifier = Modifier
                                    .size(mainImageSize)
                                    .padding(4.dp)

                            )
                        }
                    }
                }
            }
            item {
                Row {
                    Text(
                        text = "Steam Import", fontSize = 35.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        TextField(
                            value = steamIdText, onValueChange = { steamIdText = it },
                            label = { Text("Enter Steam Id") },
                            modifier = Modifier
                                .size(width = 280.dp, height = 50.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .requiredHeight(height = 50.dp),

                            )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(mainButtonCut))
                                .size(mainButtonSize)
                                .background(Lime)
                                .clickable {

                                    steamId = steamIdText
                                    steamIdText = "Importing game do not click away"
                                    loading = true
                                    var totalGames = 0.0f
                                    var iterOne = 0.0f
                                    var iterTwo = 0.0f
                                    var iterThree = 0.0f
                                    var iterFour = 0.0f

                                    //Upon clicking import get the steam user id then load in the games

                                    //Start of sync api call
                                    CoroutineScope(Dispatchers.IO).launch {
                                        //Start of first call
                                        val call = SteamRetrofit.apiSteam.apiS.getAllOwnedGames(
                                            steamKey,
                                            true,
                                            steamId.toLong(),
                                            "json"
                                        )
                                        val response = call.execute()
                                        if (response.isSuccessful) {
                                            val post = response.body()!!
                                            //We need this and it is used
                                            val res = post.response!!
                                            val games: List<SteamOwnedGames.Response.SteamGames> =
                                                post.response.games
                                            for (game in games) {
                                                //Sanity check to make sure we are not adding the same game
                                                var sameNameFlag = false
                                                if (Game.gameList.isNotEmpty()) {
                                                    for (gameL in Game.gameList) {
                                                        if (gameL.gameName == game.name && gameL.gamePlatform == "Steam") {
                                                            sameNameFlag = true
                                                        }
                                                    }
                                                }
                                                if (!sameNameFlag) {

                                                    //make a game object and add it to games list
                                                    val gameconvert = Game()
                                                    gameconvert.gameName = game.name
                                                    gameconvert.gameId = game.appid
                                                    val gameTimeHours =
                                                        game.playtime_forever / 60
                                                    gameconvert.gameTime =
                                                        gameTimeHours.toFloat()

                                                    var convertToUrl =
                                                        "https://steamcdn-a.akamaihd.net/steam/apps/"

                                                    convertToUrl =
                                                        convertToUrl.plus(game.appid.toString())
                                                    //convertToUrl = convertToUrl.plus("/)"
                                                    convertToUrl =
                                                        convertToUrl.plus("/header.jpg")
                                                    gameconvert.coverURL = convertToUrl
                                                    val timeLastPlayed =
                                                        Instant.ofEpochSecond(game.rtime_last_played)
                                                            .atZone(
                                                                ZoneId.systemDefault()
                                                            ).toLocalDateTime()
                                                    gameconvert.dateTimeLastPlayed =
                                                        timeLastPlayed
                                                    gameconvert.gamePlatform = "Steam"
                                                    if (gameconvert.gameTime <= 0) {
                                                        gameconvert.newlyAdded = true
                                                    }
                                                    Game.gameList.add(gameconvert)
                                                    totalGames += 1.0f
                                                } else {
                                                    //Do nothing
                                                }
                                            }
                                            saveGameFile(Game.gameList)
                                            threeStepCurrentProgress = 0.20f
                                        }
                                        //End of game import api call
                                        for (game in Game.gameList) {
                                            val callAch =
                                                SteamRetrofit.apiSteam.apiS.getAllGameAchievements(
                                                    game.gameId,
                                                    steamKey,
                                                    steamId.toLong()
                                                )
                                            val achResponse = callAch.execute()
                                            if (achResponse.isSuccessful) {
                                                //go through and make sure the achievement isnt already added

                                                val achievementPost = achResponse.body()!!
                                                val playerStats = achievementPost.playerstats!!
                                                val gameAchievements = playerStats.achievements
                                                for (ach in gameAchievements) {
                                                    var sameElementFlag = false
                                                    if (game.achievements.isNotEmpty()) {
                                                        for (achL in game.achievements) {
                                                            if (achL.title == ach.apiname) {
                                                                sameElementFlag = true
                                                            }
                                                        }
                                                    }
                                                    if (!sameElementFlag) {
                                                        val earnedFlag =
                                                            //Ignore the warning
                                                            if (ach.achieved == 1) true else false
                                                        val toConvert = Achievement()
                                                        toConvert.title = ach.apiname
                                                        toConvert.isEarned = earnedFlag
                                                        game.achievements.add(toConvert)
                                                    } else {
                                                        //do Nothing
                                                    }
                                                }
                                            }
                                            iterOne += 1.0f
                                            currentProgress = (iterOne / totalGames)

                                        }
                                        threeStepCurrentProgress = 0.40f
                                        //End of player achievement Call
                                        //Start of achievement percentage call
                                        for (game in Game.gameList) {
                                            val callAchPercent =
                                                SteamRetrofit.apiSteam.apiS.getAllAchievementPercentages(
                                                    game.gameId,
                                                    "json"
                                                )
                                            val achPercentResponse = callAchPercent.execute()
                                            if (achPercentResponse.isSuccessful) {
                                                val achPercentList =
                                                    achPercentResponse.body()
                                                        ?.achievementpercentages?.achievements
                                                if (achPercentList != null) {
                                                    for (achPercent in achPercentList) {
                                                        for (ach in game.achievements) {
                                                            if (achPercent.name == ach.title) {
                                                                ach.percentageEarned =
                                                                    achPercent.percent.toDouble()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            iterTwo += 1.0f
                                            currentProgress = (iterTwo / totalGames)
                                        }
                                        threeStepCurrentProgress = 0.60f
                                        saveGameFile(Game.gameList)
                                        for (game in Game.gameList) {
                                            val callAch =
                                                SteamRetrofit.apiSteam.apiS.getGameSchema(
                                                    steamKey,
                                                    game.gameId
                                                )
                                            val achResponse = callAch.execute()
                                            if (achResponse.isSuccessful) {
                                                val achievements =
                                                    achResponse.body()?.game?.availableGameStats
                                                        ?.achievements
                                                if (game.achievements.isNotEmpty()) {
                                                    if (achievements != null) {
                                                        for (ach in achievements) {
                                                            for (achI in game.achievements) {
                                                                if (achI.title == ach.name) {
                                                                    achI.title = ach.displayName
                                                                    achI.description =
                                                                        ach.description
                                                                    achI.achImageUrl = ach.icon
                                                                    achI.achImageUrlGray =
                                                                        ach.icongray
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            iterThree += 1.0f
                                            currentProgress = (iterThree / totalGames)
                                        }
                                        threeStepCurrentProgress = 0.80f
                                        //Determine which games have been completed
                                        if (Game.gameList.isNotEmpty()) {
                                            for (game in Game.gameList) {
                                                var score = 0
                                                if (game.achievements.isNotEmpty()) {
                                                    for (ach in game.achievements) {
                                                        if (ach.isEarned) {
                                                            score++
                                                        }
                                                    }
                                                    if (score == game.achievements.size) {
                                                        game.allAchiev = true
                                                    }
                                                }
                                                iterFour += 1.0f
                                                currentProgress = (iterFour / totalGames)

                                            }
                                        }
                                        saveGameFile(Game.gameList)
                                        steamIdText = "Done Importing"
                                        threeStepCurrentProgress = 1.00f
                                        loading = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.download_file_icon),
                                contentDescription = "question mark",
                                modifier = Modifier
                                    .size(mainImageSize)
                                    .padding(4.dp)

                            )
                        }
                    }
                }
            }
            // Xbox Import Section (Below Steam Import)
            item {
                Text(
                    text = "Xbox Live Import", fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    item {
                        TextField(
                            value = xboxIdText,
                            onValueChange = { xboxIdText = it },
                            label = { Text("Enter Gamertag") },
                            modifier = Modifier
                                .size(width = 280.dp, height = 50.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                .requiredHeight(height = 50.dp),
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(mainButtonCut))
                                .size(mainButtonSize)
                                .background(Lime)
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val gamertag = xboxIdText.trim()

                                            if (gamertag.isNotEmpty()) {
                                                val xuid = getXuidFromGamertag(gamertag)?.xuid
                                                if (xuid != null) {
                                                    val gamesResponse =
                                                        ApiClient.openXBL.xboxWebAPIClient.getAllGamesByID(xuid = xuid.toString()).execute()
                                                    if (gamesResponse.isSuccessful) {
                                                        val xboxGames = gamesResponse.body()?.games ?: emptyList()

                                                        for (xboxGame in xboxGames) {
                                                            // Sanity check to avoid duplicates
                                                            val isDuplicate = Game.gameList.any { existingGame ->
                                                                existingGame.gameName == xboxGame.name && existingGame.gamePlatform == "Xbox"
                                                            }

                                                            if (!isDuplicate) {
                                                                // Convert Xbox game data to Game object
                                                                val gameConvert = Game().apply {
                                                                    gameName = xboxGame.name
                                                                    gameId = xboxGame.titleId.toLongOrNull() ?: 0L
                                                                    gamePlatform = "Xbox"
                                                                    coverURL = xboxGame.displayImage.replace("http", "https")
                                                                    dateTimeLastPlayed = Instant.parse(xboxGame.titleHistory?.lastTimePlayed)
                                                                        .atZone(ZoneId.systemDefault())
                                                                        .toLocalDateTime()
                                                                }

                                                                // Fetch achievements for each Xbox game
                                                                val achievementsResponse =
                                                                    ApiClient.openXBL.xboxWebAPIClient.getUserAchievements(
                                                                        xuid = xuid.toString(),
                                                                        titleId = xboxGame.titleId
                                                                    ).execute()

                                                                if (achievementsResponse.isSuccessful) {
                                                                    val xboxAchievements = achievementsResponse.body()?.achievements ?: emptyList()

                                                                    for (achievement in xboxAchievements) {
                                                                        val achievementData = Achievement().apply {
                                                                            title = achievement.name
                                                                            isEarned = achievement.unlocked
                                                                            description = achievement.description
                                                                            achImageUrl = achievement.mediaAssets.firstOrNull()?.url ?: ""
                                                                        }
                                                                        gameConvert.achievements.add(achievementData)
                                                                    }
                                                                }

                                                                Game.gameList.add(gameConvert)
                                                            }
                                                        }

                                                        saveGameFile(Game.gameList)

                                                        // Update UI on main thread
                                                        withContext(Dispatchers.Main) {
                                                            xboxIdText = "Done Importing Xbox games and achievements"
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            xboxIdText = "Error importing games."
                                                        }
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        xboxIdText = "Could not retrieve XUID. Check Gamertag."
                                                    }
                                                }
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    xboxIdText = "Please enter a valid Gamertag."
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                xboxIdText = "Error during import."
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.download_file_icon),
                                contentDescription = "Import Icon",
                                modifier = Modifier
                                    .size(mainImageSize)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
            item{
                if(loading){
                    Column (modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text="Loading", fontSize = 25.sp )
                        LinearProgressIndicator(progress = currentProgress,
                            modifier = Modifier
                                .size(width = 400.dp,height = 20.dp),
                            trackColor = Color.Black,
                            color = Lime,
                        ) }
                }
                if(loading){
                    Column (modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally){
                        LinearProgressIndicator(progress = threeStepCurrentProgress,
                            modifier = Modifier
                                .size(width = 400.dp,height = 20.dp),
                            trackColor = Color.Black,
                            color = Lime,
                        ) }
                }
            }
            item {
                Text(
                    text = "Manual Import", fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //Text inputs below
                    LazyRow(modifier = Modifier.padding(vertical = 20.dp))
                    {
                        item {
                            TextField(value = gameNameM, onValueChange = { gameNameM = it },
                                label = { Text("Game Name") })
                        }
                    }


                    LazyRow(modifier = Modifier.padding(vertical = 20.dp))
                    {
                        item {
                            TextField(value = gameDescM, onValueChange = { gameDescM = it },
                                label = { Text("Game Description") })
                        }
                    }

                    LazyRow(modifier = Modifier.padding(vertical = 20.dp))
                    {
                        item {
                            TextField(value = gameTimeM, onValueChange = { gameTimeM = it },
                                label = { Text("In Game Time") })
                        }
                    }
                    LazyRow(modifier = Modifier.padding(vertical = 20.dp))
                    {
                        item {
                            TextField(value = gameDateM, onValueChange = { gameDateM = it },
                                label = { Text("Release Date") })
                        }
                    }

                    LazyRow(modifier = Modifier.padding(vertical = 20.dp))
                    {
                        item {
                            TextField(value = gamePlatformM,
                                onValueChange = { gamePlatformM = it },
                                label = { Text("Game Platform") })
                        }
                    }
                    //Buttons
                    LazyRow(
                        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        item {
                            Button(
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            LibraryActivity::class.java
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                            ) {
                                Text(text = "Back", color = Color.Black)
                            }
                        }
                        //On click we used the method I made to convert the string inputs to a gameDB object
                        item {
                            Button(
                                onClick = {
                                    val gameInput = GameUtil.InputToGame(
                                        gameNameM,
                                        gameDescM,
                                        gameTimeM,
                                        gameDateM,
                                        gamePlatformM
                                    )
                                    //Now we add the game to the list
                                    Game.gameList.add(gameInput)
                                    saveGameFile(Game.gameList)
                                    context.startActivity(
                                        Intent(
                                            context,
                                            LibraryActivity::class.java
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpringGreen)
                            ) {
                                Text(text = "Confirm Inputs", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
