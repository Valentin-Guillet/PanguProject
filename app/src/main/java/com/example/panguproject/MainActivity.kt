package com.example.panguproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panguproject.ui.theme.PanguProjectTheme

/* TODO:
- make sections scrollable
- long press on card -> show info
- undo button ?
- dark mode
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PanguProjectTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "menu") {
                    composable("menu") { MenuPage(navController) }
                    composable("game") { GamePage(navController) }
                }
            }
        }
    }
}

@Preview(device = "id:S9+")
//@Preview
@Composable
fun DefaultPreview() {
    PanguProjectTheme {
//        val navController = rememberNavController()
//        NavHost(navController, startDestination = "menu") {
//            composable("menu") { MenuPage(navController) }
//            composable("game") { GamePage(navController) }
//        }
//        MenuPage(rememberNavController())
        GamePage(rememberNavController())
    }
}