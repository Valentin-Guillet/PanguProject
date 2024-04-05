package com.example.panguproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panguproject.ui.theme.PanguProjectTheme

/* TODO:
- make info card swipable
- log line that displays when error on buy
- remove reset building and implement real reset
- make game savable
- undo button ?
- dark mode
*/

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanguProjectApp()
        }
    }
}

@ExperimentalMaterial3Api
@Preview(device = "id:S9+")
@Composable
fun PanguProjectApp() {
    PanguProjectTheme {
//        val navController = rememberNavController()
//        NavHost(navController, startDestination = "menu") {
//            composable("menu") { MenuScreen(navController) }
//            composable("game") { GameScreen(navController) }
//        }
        GameScreen(null)
    }
}
