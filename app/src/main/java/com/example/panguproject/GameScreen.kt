package com.example.panguproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun GamePage(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GameInfoSection()
            Spacer(modifier = Modifier.height(4.dp))
            GameProjectSection()
            GameBuildingSection()
            GameBlueprintSection()
            GameResourceSection()
            GameActionSection()
        }
    }
}

@Composable
fun GameInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score  17")
        Text(text = "Pangu Project", fontSize = 28.sp)
        Text(text = "Turn  1 / 10")
    }
}

@Composable
fun ColumnScope.GameProjectSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(0.8f)
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
        ) {
            val projectNames = listOf("Project 1\nsix ones", "Project 2", "Project 3")
            repeat(3) { index -> CreateProject(projectNames[index])}
        }
    }
}

@Composable
fun ColumnScope.GameBuildingSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(2f)
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) { }
}

@Composable
fun ColumnScope.GameBlueprintSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(2f)
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) { }
}

@Composable
fun ColumnScope.GameResourceSection() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(1.5f),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
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
}

@Composable
fun GameActionSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        Button(onClick = {}) {
            Text("Reroll (2)")
        }
        Button(onClick = {}) {
            Text("-")
        }
        Button(onClick = {}) {
            Text("+")
        }
        Button(onClick = {}) {
            Text("End Turn")
        }
    }
}


const val DICE_SIZE: Int = 32

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
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp)),

        ) {
        val colPad = 8
        val nbMaxDices: Int = ((maxWidth.value - 2 * colPad - 2) / (DICE_SIZE + 2)).toInt()
        val pad: Int = if (nbMaxDices > 1) ((maxWidth.value - 2 * colPad - nbMaxDices * DICE_SIZE) / (nbMaxDices - 1)).toInt() else 0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(colPad.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val nbRows: Int = dices.size / nbMaxDices
            for (row: Int in 0..nbRows) {
                val arrangement = if (nbMaxDices == 1) Arrangement.Center else Arrangement.spacedBy(pad.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                .padding(bottom = 4.dp, end = 10.dp)
                .zIndex(-1f),
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            color = Color.Red.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun RowScope.CreateProject(name: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .padding(2.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = name,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}