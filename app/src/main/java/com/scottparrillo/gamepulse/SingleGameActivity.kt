package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class SingleGameActivity: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                SingleGameScreen()
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun SingleGameScreen(){
    //Vars and vals
    val game = Game.selectedGame
    val context = LocalContext.current
    val gameTitle = game.gameName
    val gamePlatform = game.gamePlatform
    val gameTimeTotal = game.gameTime
    val gameLastPlayed = game.dateTimeLastPlayed.truncatedTo(ChronoUnit.DAYS)
    var noTime = false
    //1969 12 31 18:00
    if(gameLastPlayed == LocalDateTime.of(1969, 12, 31, 0,0))
    {
        noTime = true
    }
    var achievementsEarned = 0
    //Quick loop
    for (ach in game.achievements)
        {
            if(ach.isEarned)
            {
                achievementsEarned++
            }
        }


    Column(modifier = Modifier
        .background(color = CuriousBlue)
        .fillMaxSize()
            ) {


            Row (horizontalArrangement = Arrangement.Start){
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
                                    LibraryActivity::class.java
                                )
                            )
                        }
                )

            }
            AsyncImage(model = Game.selectedGame.coverURL, contentScale = ContentScale.Fit,
                contentDescription = "The cover of a game",
                modifier = Modifier
                    //size(235.dp)
                    .size(height = 353.dp, width = 616.dp)
                    .clip(RectangleShape)
            )
        LazyColumn {
            item { Text(text = "Platform: $gamePlatform") }
            item { Text(text = "Title: $gameTitle") }
            item {Text(text = "Total Hours played: $gameTimeTotal")}
            item {
                if(noTime)
                {
                    Text(text = "Last played: No data available")
                }
                else
                {
                    Text(text = "Last played: $gameLastPlayed")
                }
            }
            item { if(game.achievements.size <= 0)
                    {
                        Text(text = "Achievements: No achievements for this game")
                    }
                    else
                    {
                        Text(text = "Total Achievements: $achievementsEarned / ${game.achievements.size}")
                    }
                }

        }
    }

}