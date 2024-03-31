package com.example.panguproject

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.panguproject.ui.theme.BaseDiceColor
import com.example.panguproject.ui.theme.BlueprintColor
import com.example.panguproject.ui.theme.FixedDiceColor
import com.example.panguproject.ui.theme.SelectedDiceBorderColor
import com.example.panguproject.ui.theme.UsableBlueprintColor
import com.example.panguproject.ui.theme.WildDiceColor

@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel = viewModel()) {
    val turn: Int by gameViewModel.turn.collectAsState()
    val diceList: List<Dice> by gameViewModel.diceList.collectAsState()
    val nbRerolls: Int by gameViewModel.nbRerolls.collectAsState()
    val nbMod: Int by gameViewModel.nbMod.collectAsState()
    val buildingList: List<Blueprint> by gameViewModel.buildingList.collectAsState()
    val blueprintList: List<Blueprint> by gameViewModel.blueprintList.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GameInfoSection(turn)
            Spacer(modifier = Modifier.height(4.dp))
            GameProjectSection(modifier = Modifier.weight(0.8f))
            GameBuildingSection(
                buildingList,
                onBlueprintClick = gameViewModel::useBuilding,
                modifier = Modifier.weight(2f)
            )
            GameBlueprintSection(
                blueprintList,
                onBlueprintClick = gameViewModel::buyBlueprint,
                modifier = Modifier.weight(2f))
            GameResourceSection(
                diceList,
                onDiceClick = gameViewModel::selectDice,
                modifier = Modifier.weight(1.5f)
            )
            GameActionSection(
                nbRerolls = nbRerolls,
                nbMod = nbMod,
                onReroll = gameViewModel::reroll,
                onModMinus = gameViewModel::modMinus,
                onModPlus = gameViewModel::modPlus,
                onEndTurn = gameViewModel::nextTurn,
            )
        }
    }
}

@Composable
fun GameInfoSection(turn: Int, modifier: Modifier = Modifier) {
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
fun GameProjectSection(modifier: Modifier = Modifier) {
    DisplaySection(name = null, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val projectNames = listOf("Project 1\nsix ones", "Project 2", "Project 3")
            repeat(3) { index ->
                DisplayProject(projectNames[index], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GameBuildingSection(
    buildings: List<Blueprint>,
    onBlueprintClick: (Blueprint) -> Unit,
    modifier: Modifier = Modifier
) {
    DisplaySection(name = "Colony", modifier = modifier) {
        val nbRows = (buildings.size / 3) + 1
        val colWidth: Dp = (maxWidth - 24.dp) / 3
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(nbRows) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val nbCols = if (rowIndex == nbRows - 1) buildings.size % 3 else 3
                    repeat(nbCols) { colIndex ->
                        val blueprint = buildings[rowIndex * 3 + colIndex]
                        DisplayBlueprint(
                            blueprint = blueprint,
                            usable = blueprint.usable,
                            onBlueprintClick = onBlueprintClick,
                            drawBorder = true,
                            modifier = Modifier
                                .width(colWidth)
                                .height(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameBlueprintSection(
    blueprints: List<Blueprint>,
    onBlueprintClick: (Blueprint) -> Unit,
    modifier: Modifier = Modifier
) {
    DisplaySection(name = "Blueprints", modifier = modifier) {
        val nbRows = (blueprints.size / 3) + 1
        val colWidth: Dp = (maxWidth - 24.dp) / 3
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(nbRows) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val nbCols = if (rowIndex == nbRows - 1) blueprints.size % 3 else 3
                    repeat(nbCols) { colIndex ->
                        DisplayBlueprint(
                            blueprint = blueprints[rowIndex * 3 + colIndex],
                            usable = true,
                            onBlueprintClick = onBlueprintClick,
                            drawBorder = false,
                            modifier = Modifier
                                .width(colWidth)
                                .height(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameResourceSection(
    diceList: List<Dice>,
    onDiceClick: (Dice, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val (turnDiceList, storedDiceList) = diceList.partition { !it.stored }
        DiceStorage(
            diceList = turnDiceList,
            onDiceClick = onDiceClick,
            name = "Resources",
            modifier = Modifier.weight(3f),
        )
        DiceStorage(
            diceList = storedDiceList,
            onDiceClick = onDiceClick,
            name = "Storage",
            modifier = Modifier.weight(2f),
        )
    }
}

@Composable
fun GameActionSection(
    nbRerolls: Int,
    nbMod: Int,
    onReroll: () -> Unit,
    onModMinus: () -> Unit,
    onModPlus: () -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        Button(onClick = onReroll, enabled = nbRerolls > 0) {
            Text("Reroll (${nbRerolls})")
        }
        Button(onClick = onModMinus) {
            Text("-")
        }
        DisplayModIndicator(nbMod)
        Button(onClick = onModPlus) {
            Text("+")
        }
        Button(onClick = onEndTurn) {
            Text("End Turn")
        }
    }
}


@Composable
fun DisplaySection(
    name: String?,
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .border(4.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        content()

        if (!name.isNullOrEmpty()) {
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
}

@Composable
fun DisplayProject(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = name,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayBlueprint(
    blueprint: Blueprint,
    usable: Boolean,
    onBlueprintClick: (Blueprint) -> Unit,
    modifier: Modifier = Modifier,
    drawBorder: Boolean = false,
) {
    val color = if (usable && drawBorder) UsableBlueprintColor else BlueprintColor
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .border(4.dp, color, shape = RoundedCornerShape(8.dp))
            .combinedClickable(
                enabled = usable,
                onClick = { onBlueprintClick(blueprint) },
                onLongClick = { onBlueprintClick(blueprint) },
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        Text(blueprint.name, modifier = Modifier.align(Alignment.Center), fontSize = 28.sp)
    }
}

@Composable
fun DiceStorage(
    diceList: List<Dice>,
    onDiceClick: (Dice, Boolean) -> Unit,
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(colPad.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val nbRows: Int = diceList.size / nbMaxDice
            repeat(nbRows + 1) { row: Int ->
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

const val DICE_SIZE: Int = 32

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayDice(
    dice: Dice,
    onDiceClick: (Dice, Boolean) -> Unit,
) {
    val diceColor: Color = if (dice.fixed) {
        FixedDiceColor
    } else if (dice.wild) {
        WildDiceColor
    } else {
        BaseDiceColor
    }
    val borderColor = if (dice.selected) SelectedDiceBorderColor else diceColor
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(DICE_SIZE.dp)
            .background(diceColor, shape = RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = { onDiceClick(dice, false) },
                onLongClick = { onDiceClick(dice, true) },
                interactionSource = interactionSource,
                indication = null
            ),
    ) {
        Text(
            text = dice.value.toString(),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun DisplayModIndicator(nbMod: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "$nbMod",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Mod",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(device = "id:S9+")
@Composable
fun GameScreenPreview() {
    GameScreen(null)
}