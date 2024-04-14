package com.example.panguproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panguproject.data.GameStateStorage
import com.example.panguproject.ui.GameScreen
import com.example.panguproject.ui.MenuScreen
import com.example.panguproject.ui.theme.PanguProjectTheme

/* TODO:
- make real menu page
- make an icon
- add statistics screen
*/

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanguProjectApp(applicationContext)
        }
    }
}

@ExperimentalMaterial3Api
@Preview(device = "id:S9+")
@Composable
fun PanguProjectApp(context: Context = LocalContext.current) {
    val gameStateStorage = remember { GameStateStorage(context) }
    val gameViewModel = remember { GameViewModel(gameStateStorage) }
    PanguProjectTheme {
        val navController = rememberNavController()
        NavHost(
            navController,
            startDestination = "menu",
            enterTransition = { EnterTransition.None },
        ) {
            composable("menu") { MenuScreen(navController, gameViewModel) }
            composable("game") { GameScreen(navController, gameViewModel) }
        }
    }
}
