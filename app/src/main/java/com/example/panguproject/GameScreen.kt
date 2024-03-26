package com.example.panguproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel = viewModel()) {
    val game: Game by gameViewModel.game.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GameInfoSection(game)
            Spacer(modifier = Modifier.height(4.dp))
            GameProjectSection(game, modifier = Modifier.weight(0.8f))
            GameBuildingSection(game, modifier = Modifier.weight(2f))
            GameBlueprintSection(game, modifier = Modifier.weight(2f))
            GameResourceSection(game, onDiceClick = { gameViewModel.selectDice(it) }, modifier = Modifier.weight(1.5f))
            GameActionSection(game, { gameViewModel.nextTurn() })
        }
    }
}

@Composable
fun GameInfoSection(game: Game, modifier: Modifier = Modifier) {
    val turn: Int = game.turn

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score  17")
        Text(text = "Pangu Project", fontSize = 28.sp)
        Text(text = "Turn  $turn / 10")
    }
}

@Composable
fun GameProjectSection(game: Game, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
        ) {
            val projectNames = listOf("Project 1\nsix ones", "Project 2", "Project 3")
            repeat(3) { index ->
                DisplayProject(projectNames[index], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GameBuildingSection(game: Game, modifier: Modifier = Modifier) {
    DisplaySection(name = "Colony", modifier = modifier) { }
}

@Composable
fun GameBlueprintSection(game: Game, modifier: Modifier = Modifier) {
    DisplaySection(name = "Blueprints", modifier = modifier) { }
}

@Composable
fun GameResourceSection(
    game: Game,
    onDiceClick: (Dice) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val (diceListBasic, diceListFixed) = game.diceList.partition { !it.fixed }
        DiceStorage(
            diceList = diceListBasic,
            onDiceClick = onDiceClick,
            name = "Resources",
            modifier = Modifier.weight(3f),
        )
        DiceStorage(
            diceList = diceListFixed,
            onDiceClick = onDiceClick,
            name = "Storage",
            modifier = Modifier.weight(2f),
        )
    }
}

@Composable
fun GameActionSection(
    game: Game,
    onNextTurn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
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
        Button(onClick = onNextTurn) {
            Text("End Turn")
        }
    }
}


@Composable
fun DisplaySection(
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        content()

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

const val DICE_SIZE: Int = 32

@Composable
fun DisplayDice(dice: Dice, onDiceClick: (Dice) -> Unit) {
    val color = if (dice.selected) Color.Black else Color.LightGray
    Box(
        modifier = Modifier
            .size(DICE_SIZE.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .border(2.dp, color, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = { onDiceClick(dice) }),
    ) {
        Text(
            text = dice.value.toString(),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun DiceStorage(
    diceList: List<Dice>,
    onDiceClick: (Dice) -> Unit,
    name: String,
    modifier: Modifier = Modifier,
) {
    DisplaySection(name = name, modifier = modifier) {
        val colPad = 8
        val nbMaxDice: Int = ((maxWidth.value - 2 * colPad - 2) / (DICE_SIZE + 2)).toInt()
        val pad: Int =
            if (nbMaxDice > 1) ((maxWidth.value - 2 * colPad - nbMaxDice * DICE_SIZE) / (nbMaxDice - 1)).toInt() else 0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(colPad.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val nbRows: Int = diceList.size / nbMaxDice
            for (row: Int in 0..nbRows) {
                val arrangement =
                    if (nbMaxDice == 1) Arrangement.Center else Arrangement.spacedBy(pad.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = arrangement
                ) {
                    val minId = row * nbMaxDice
                    val maxId = minOf(diceList.size - 1, minId + nbMaxDice - 1)
                    for (i: Int in minId..maxId) {
                        val dice = diceList[i]
                        DisplayDice(dice, onDiceClick)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayProject(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(2.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = name,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(device = "id:S9+")
@Composable
fun GameScreenPreview() {
    GameScreen(null)
}