package com.scottparrillo.gamepulse

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.scottparrillo.gamepulse.ui.theme.GamePulseTheme

class GameInputActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GamePulseTheme {
                GameInputScreen()
            }
        }
    }
}