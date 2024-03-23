package com.example.panguproject

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.panguproject.ui.theme.PanguProjectTheme

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

@Preview
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

@Composable
fun MenuPage(navController: NavController) {
    val activity = LocalContext.current as? Activity
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pangu Project",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(66.dp))
            Button(onClick = { navController.navigate("game") }) {
                Text(stringResource(id = R.string.play))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                activity?.finish()
            }) {
                Text(stringResource(id = R.string.quit))
            }
        }
    }
}

@Composable
fun CreateDice(number: Int) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun DiceStorage(dices: List<Int>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
//            .background(Color.Yellow, shape = RoundedCornerShape(16.dp))
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp)),

    ) {
        Text("Title")
        for (dice in dices) {
            CreateDice(dice)
        }
    }
}

@Composable
fun GamePage(navController: NavController) {
    val turn by remember { mutableIntStateOf(1) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "Turn : $turn / 10",
                modifier = Modifier.padding(16.dp),
            )

            Spacer(modifier = Modifier.weight(2f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                DiceStorage(
                    dices = listOf(1, 2, 3),
                    modifier = Modifier.weight(1f),
                )
                DiceStorage(
                    dices = listOf(4, 5, 6),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                Button(onClick = { navController.navigate("menu") }) {
                    Text("Reroll")
                }
                Button(onClick = { navController.navigate("menu") }) {
                    Text("-")
                }
                Button(onClick = { navController.navigate("menu") }) {
                    Text("+")
                }
                Button(onClick = { navController.navigate("menu") }) {
                    Text("End Turn")
                }
            }
        }
    }
//    RunPanguProject(modifier = Modifier
//        .fillMaxSize()
//        .wrapContentSize(Alignment.Center)
//    )
}

@Composable
fun RunPanguProject(modifier: Modifier = Modifier) {
    var result by remember { mutableIntStateOf(1) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreateDice(5)
        CreateDice(result)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = (1..6).random() }) {
            Text(stringResource(id = R.string.roll))
        }
    }
}