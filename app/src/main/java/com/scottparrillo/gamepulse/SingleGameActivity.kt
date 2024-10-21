package com.scottparrillo.gamepulse

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O_MR1
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.scottparrillo.gamepulse.ui.theme.CuriousBlue
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.SpringGreen
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.truncate

class SingleGameActivity: AppCompatActivity() {
    @RequiresApi(O_MR1)
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
                        .padding(2.dp)
                )

            }
            AsyncImage(model = Game.selectedGame.coverURL, contentScale = ContentScale.Fit,
                contentDescription = "The cover of a game",
                modifier = Modifier
                    //size(235.dp)
                    .size(height = 253.dp, width = 616.dp)
                    .clip(RectangleShape)
            )
        LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()){
            item { Text(text = "Platform: $gamePlatform") }
           // item { Text(text = "Title: $gameTitle") }
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


        }
       Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
           Text(text = "Achievements", fontWeight = FontWeight.Bold)
           if(game.achievements.size <= 0)
           {
               Text(text = "No achievements for this game")
           }
           else
           {
               Text(text = "$achievementsEarned / ${game.achievements.size}")
           }
       }
        LazyColumn {

            items(game.achievements){
                achievement ->
                val percentEarned = truncate(achievement.percentageEarned)
                Column (modifier = Modifier
                    .background(color = SpringGreen)
                    .border(1.dp, color = Color.Black)
                    .padding(horizontal = 2.dp)
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                    ){
                            Text(text = achievement.title, fontWeight = FontWeight.Bold)
                            Text(text = "People that have earned: $percentEarned%")


                        if(achievement.isEarned)
                        {
                            if(achievement.achImageUrl != ""){
                                AsyncImage(model = achievement.achImageUrl, contentDescription = "", modifier = Modifier
                                    .size(width = 120.dp, height = 120.dp)
                                    .requiredSize(width = 120.dp, height = 120.dp)
                                    .padding(horizontal = 0.dp)
                                    .border(2.dp, CuriousBlue, RectangleShape),
                                    alignment = Alignment.CenterEnd)
                            }
                            else{
                                Text(text = "Earned", modifier = Modifier.padding(horizontal = 5.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                        else
                        {
                            if(achievement.achImageUrlGray != ""){
                                AsyncImage(model = achievement.achImageUrlGray, contentDescription = "", modifier = Modifier
                                    .size(width = 120.dp, height = 120.dp)
                                    .requiredSize(width = 120.dp, height = 120.dp)
                                    .padding(horizontal = 0.dp)
                                    .border(2.dp, CuriousBlue, RectangleShape),
                                    alignment = Alignment.CenterEnd)

                            }
                            else{
                                Text(text = "Not Earned", modifier = Modifier.padding(horizontal = 5.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    Text(text = "Description", modifier = Modifier.padding(vertical = 5.dp), fontWeight = FontWeight.Bold)
                    if(achievement.description == "") {
                        Text(text = "No Description")
                    }
                    else{
                        Text(text = achievement.description)
                    }
                    }

                }



            }
        }
    }
