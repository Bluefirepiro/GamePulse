package com.scottparrillo.gamepulse

import android.hardware.lights.Light
import android.os.Bundle
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scottparrillo.gamepulse.ui.theme.CopperRose

import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme
import com.scottparrillo.gamepulse.ui.theme.LightBlue
import com.scottparrillo.gamepulse.ui.theme.Lime
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
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
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GamePulseTheme {
        HomeScreen()
    }
}
//This is the Start of the Library Screen
@Preview(showBackground = true)
@Composable
fun LibraryScreen(){
    //Main column
    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = CopperRose)    ){
        //Title text
        Row(verticalAlignment = Alignment.CenterVertically){
            Image(painter = painterResource(id =R.drawable.arrow),
                contentDescription ="A back arrow",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(65.dp)
                    .padding(8.dp)
                )

            Text(
                text = "Library Screen",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        //This contains the game catagories
        LazyRow (
            //horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
                //.background(Color.Transparent)
                ){

            item{
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(120.dp)
                        .height(31.dp)
                        .clickable { /* TODO: Handle Category clicks */ }
                        .padding(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Lime)

                ){
                    Text(text =  "Recent")

                 }

            }
            item{
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(120.dp)
                        .height(31.dp)
                        .clickable { /* TODO: Handle Category clicks */ }
                        .padding(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Lime)

                ){
                    Text(text = "Current")

                }
            }
            item{

                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(120.dp)
                        .height(31.dp)
                        .clickable { /* TODO: Handle Category clicks */ }
                        .padding(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Lime)
                ){
                    Text(text = "Beaten")

                }
            }
            item{
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(120.dp)
                            .height(31.dp)
                            .clickable { /* TODO: Handle Category clicks */ }
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Lime)
                    ){
                        Text(text = "New")
                    }
                }
        }
        //This row should contain the sort icon
        Row {
            Image(painter = painterResource(id = R.drawable.sort),
                contentDescription = "Sorting Arrow",
                modifier = Modifier
                    .size(35.dp)
                 )

        }
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.cyberpunk_2077_cover),
                        contentDescription = "A cover of the game Cyberpunk 2022",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(135.dp)
                            .clip(CircleShape)
                    );
                    Text(text = "Cyber Punk 2022");
                    Text(text = "Update fill")}
                }


            item { Text(text = "test") }
            item { Text(text = "test") }


        }

    }

}
