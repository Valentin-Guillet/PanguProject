package com.example.panguproject

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Preview(device = "id:S9+")
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


const val DICE_SIZE: Int = 74

@Composable
fun CreateDice(number: Int) {
    Box(
        modifier = Modifier
            .size(DICE_SIZE.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun DiceStorage(name: String, dices: List<Int>, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
//            .background(Color.Yellow, shape = RoundedCornerShape(16.dp))
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp)),

    ) {
        // TODO: make it scrollable
        val nbMaxDices: Int = ((maxWidth.value - 2 * 12 - 2) / (DICE_SIZE + 2)).toInt()
        val pad: Int = if (nbMaxDices > 1) {
            ((maxWidth.value - 2 * 12 - nbMaxDices * DICE_SIZE) / (nbMaxDices - 1)).toInt()
        } else {
            0
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val nbRows: Int = dices.size / nbMaxDices
            for (row: Int in 0..nbRows) {
                val arrangement = if (nbMaxDices == 1) Arrangement.Center else Arrangement.spacedBy(pad.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .widthIn(min = 100.dp),
                    horizontalArrangement = arrangement
                ) {
                    val minId = row * nbMaxDices
                    val maxId = minOf(dices.size - 1, minId + nbMaxDices - 1)
                    for (i: Int in minId..maxId) {
                        CreateDice(dices[i])
                    }
                }
            }
        }

        Text(
            name,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 6.dp, end = 10.dp),
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp,
            color = Color.Red.copy(alpha = 0.7f),
        )
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = "Projects  0 / 3",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                )
                Text(
                    text = "Turn  $turn / 10",
                    modifier = Modifier.padding(end = 16.dp, top = 16.dp),
                )
            }

            Spacer(modifier = Modifier.weight(2f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                DiceStorage(
                    name = "Resources",
                    dices = (1..11).toList(),
                    modifier = Modifier.weight(3f),
                )
                DiceStorage(
                    name = "Preserved",
                    dices = listOf(4, 5, 6),
                    modifier = Modifier.weight(2f),
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
}