package com.vguillet.panguproject.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vguillet.panguproject.GameViewModel
import com.vguillet.panguproject.R
import com.vguillet.panguproject.data.GameStateStorage
import com.vguillet.panguproject.data.allBlueprintsList
import com.vguillet.panguproject.data.allProjectsList
import com.vguillet.panguproject.model.Blueprint
import com.vguillet.panguproject.model.BlueprintStatus
import com.vguillet.panguproject.model.DetailCard
import com.vguillet.panguproject.model.Dice
import com.vguillet.panguproject.model.GameState
import com.vguillet.panguproject.model.Project
import com.vguillet.panguproject.model.ProjectStatus
import com.vguillet.panguproject.ui.theme.BackgroundColor
import com.vguillet.panguproject.ui.theme.BaseDiceColor
import com.vguillet.panguproject.ui.theme.ButtonColor
import com.vguillet.panguproject.ui.theme.EcruWhite
import com.vguillet.panguproject.ui.theme.FixedDiceColor
import com.vguillet.panguproject.ui.theme.PinkRed
import com.vguillet.panguproject.ui.theme.SectionBackgroundColor
import com.vguillet.panguproject.ui.theme.SectionTextColor
import com.vguillet.panguproject.ui.theme.SelectedDiceBorderColor
import com.vguillet.panguproject.ui.theme.SpaceBlue
import com.vguillet.panguproject.ui.theme.UsableCardBorderColor
import com.vguillet.panguproject.ui.theme.WildDiceColor

@ExperimentalMaterial3Api
@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel = viewModel()) {
    var displayCardInfoList: List<DetailCard>? by rememberSaveable { mutableStateOf(null) }
    var displayCardInfoIndex: Int by rememberSaveable { mutableIntStateOf(0) }

    val gameState: GameState by gameViewModel.gameState.collectAsState()
    val logMsg: String by gameViewModel.logMsg.collectAsState()

    fun cardViewFactory(cards: List<DetailCard>): (DetailCard) -> Unit {
        return fun(card: DetailCard) {
            displayCardInfoList = cards
            displayCardInfoIndex = cards.indexOf(card)
        }
    }

    val focusAlpha = if (displayCardInfoList != null) 0.5f else 1f
    val onClose: (() -> Unit)? = if (displayCardInfoList != null) {
        { displayCardInfoList = null; displayCardInfoIndex = 0 }
    } else {
        null
    }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier.fillMaxSize()
            .clickable(
                onClick = { onClose?.invoke() },
                interactionSource = interactionSource,
                indication = null,
            ),
        color = BackgroundColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
                .alpha(focusAlpha),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GameInfoSection(
                gameState.score,
                gameState.turn,
                modifier = Modifier.height(24.dp),
                logMsg = logMsg,
            )
            GameProjectSection(
                gameState.projectStatusList,
                onProjectClick = gameViewModel::buildProject,
                onProjectLongClick = {
                    cardViewFactory(
                        gameState.projectStatusList
                            .map { projectStatus -> allProjectsList[projectStatus.id] })(it)
                },
                modifier = Modifier.weight(0.9f),
            )
            GameBuildingSection(
                gameState.buildingStatusList,
                onBlueprintClick = gameViewModel::useBuilding,
                onBlueprintLongClick = {
                    cardViewFactory(
                        gameState.buildingStatusList
                            .map { buildingStatus -> allBlueprintsList[buildingStatus.id] })(it)
                },
                modifier = Modifier.weight(2f),
            )
            GameBlueprintSection(
                gameState.blueprintStatusList,
                onBlueprintClick = gameViewModel::buildBlueprint,
                onBlueprintLongClick = {
                    cardViewFactory(
                        gameState.blueprintStatusList
                            .map { blueprintStatus -> allBlueprintsList[blueprintStatus.id] })(it)
                },
                onBlueprintDoubleClick = gameViewModel::discardBlueprint,
                modifier = Modifier.weight(2f),
            )
            GameResourceSection(
                gameState.diceList,
                onDiceClick = gameViewModel::selectDice,
                modifier = Modifier.weight(1f),
            )
            GameActionSection(
                nbRerolls = gameState.nbRerolls,
                nbMod = gameState.nbMod,
                onReroll = gameViewModel::rerollDice,
                onModMinus = gameViewModel::decreaseDiceValue,
                onModPlus = gameViewModel::increaseDiceValue,
                onEndTurn = gameViewModel::nextTurn,
            )
        }

        if (gameState.gameOver) {
            DisplayGameOver(
                navController,
                gameState.score,
                success = gameState.projectStatusList.all { it.built },
                gameViewModel::newGame
            )
        }

        if (displayCardInfoList != null) {
            CardSlider(
                cards = displayCardInfoList!!,
                index = displayCardInfoIndex,
            )
        }
    }
}

@Composable
fun GameInfoSection(
    score: Int,
    turn: Int,
    modifier: Modifier = Modifier,
    logMsg: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 16.sp, color = Color.White)
        if (logMsg != null) {
            Text(text = logMsg, fontSize = 16.sp, color = Color.White)
        }
        Text(text = "Turn  $turn / 10", fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun GameProjectSection(
    projects: List<ProjectStatus>,
    onProjectClick: (project: Project) -> Unit,
    onProjectLongClick: (project: Project) -> Unit,
    modifier: Modifier = Modifier,
) {
    DisplaySection(name = null, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            projects.forEach {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    @Suppress("UNCHECKED_CAST") (DisplayCard(
                        allProjectsList[it.id],
                        !it.built,
                        onProjectClick as (DetailCard) -> Unit,
                        onProjectLongClick as (DetailCard) -> Unit,
                        modifier = Modifier
                            .alpha(if (it.built) 0.5f else 1f),
                        subtext = allProjectsList[it.id].shortCostDescription,
                    ))

                    if (it.built) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.icons8_flat_checkmark),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(120.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameBuildingSection(
    buildings: List<BlueprintStatus>,
    onBlueprintClick: (Blueprint) -> Unit,
    onBlueprintLongClick: (Blueprint) -> Unit,
    modifier: Modifier = Modifier,
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val nbCols = if (rowIndex == nbRows - 1) buildings.size % 3 else 3
                    repeat(nbCols) { colIndex ->
                        val blueprint = buildings[rowIndex * 3 + colIndex]
                        @Suppress("UNCHECKED_CAST") (DisplayCard(
                            card = allBlueprintsList[blueprint.id],
                            usable = blueprint.usable,
                            onCardClick = onBlueprintClick as (DetailCard) -> Unit,
                            onCardLongClick = onBlueprintLongClick as (DetailCard) -> Unit,
                            modifier = Modifier
                                .width(colWidth)
                                .height(75.dp),
                            subtext = allBlueprintsList[blueprint.id].shortEffectDescription,
                            drawBorder = true,
                        ))
                    }
                }
            }
        }
    }
}

@Composable
fun GameBlueprintSection(
    blueprints: List<BlueprintStatus>,
    onBlueprintClick: (Blueprint) -> Unit,
    onBlueprintLongClick: (Blueprint) -> Unit,
    onBlueprintDoubleClick: (Blueprint) -> Unit,
    modifier: Modifier = Modifier,
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val nbCols = if (rowIndex == nbRows - 1) blueprints.size % 3 else 3
                    repeat(nbCols) { colIndex ->
                        val blueprint = blueprints[rowIndex * 3 + colIndex]
                        @Suppress("UNCHECKED_CAST") (DisplayCard(
                            card = allBlueprintsList[blueprint.id],
                            usable = true,
                            onCardClick = onBlueprintClick as (DetailCard) -> Unit,
                            onCardLongClick = onBlueprintLongClick as (DetailCard) -> Unit,
                            onCardDoubleClick = onBlueprintDoubleClick as (DetailCard) -> Unit,
                            modifier = Modifier
                                .width(colWidth)
                                .height(75.dp),
                            subtext = allBlueprintsList[blueprint.id].shortCostDescription,
                            drawBorder = false,
                        ))
                    }
                }
            }
        }
    }
}

@Composable
fun GameResourceSection(
    diceList: List<Dice>,
    onDiceClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onClick = { dice: Dice, selectOnly: Boolean ->
        onDiceClick(diceList.indexOfFirst { it === dice }, selectOnly)
    }
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val (turnDiceList, storedDiceList) = diceList.partition { !it.stored }
        DiceStorage(
            diceList = turnDiceList,
            onDiceClick = onClick,
            name = "Resources",
            modifier = Modifier.weight(3f),
        )
        DiceStorage(
            diceList = storedDiceList,
            onDiceClick = onClick,
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
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom,
    ) {
        Button(
            onClick = onReroll,
            enabled = nbRerolls > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor,
                disabledContainerColor = ButtonColor.copy(alpha = 0.7f)
            ),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.width(90.dp),
        ) {
            Text(
                "Reroll (${nbRerolls})",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onModMinus,
            contentPadding = PaddingValues(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
            modifier = Modifier.width(40.dp),
        ) {
            Text("-", textAlign = TextAlign.Center)
        }
        DisplayModIndicator(nbMod)
        Button(
            onClick = onModPlus,
            contentPadding = PaddingValues(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
            modifier = Modifier.width(40.dp),
        ) {
            Text("+", textAlign = TextAlign.Center)
        }
        Button(
            onClick = onEndTurn,
            contentPadding = PaddingValues(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
            modifier = Modifier.width(90.dp),
        ) {
            Text(
                "End Turn",
                textAlign = TextAlign.Center,
            )
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
            .padding(2.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(SectionBackgroundColor)
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
                fontSize = 16.sp,
                color = SectionTextColor,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayCard(
    card: DetailCard,
    usable: Boolean,
    onCardClick: (DetailCard) -> Unit,
    onCardLongClick: (DetailCard) -> Unit,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    drawBorder: Boolean = false,
    onCardDoubleClick: ((DetailCard) -> Unit)? = null,
) {
    val borderColor = if (drawBorder && usable) UsableCardBorderColor else Color.White

    val interactionSource = remember { MutableInteractionSource() }
    val doubleClickCallback: (() -> Unit)? = if (onCardDoubleClick != null) {
        { onCardDoubleClick(card) }
    } else null
    UpdateViewConfiguration(doubleTapTimeoutMillis = 100) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp,
            color = SectionBackgroundColor,
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .border(
                        if (drawBorder && usable) 1.dp else 0.dp,
                        borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .combinedClickable(
                        onClick = { if (usable) onCardClick(card) },
                        onLongClick = { onCardLongClick(card) },
                        onDoubleClick = doubleClickCallback,
                        interactionSource = interactionSource,
                        indication = null
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val name = card.name.removeSuffix(" Project")
                val titleFontSize: TextUnit = when (name.length) {
                    in 0..9 -> 20.sp
                    in 10..14 -> 18.sp
                    else -> 16.sp
                }
                Text(
                    name,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .weight(1f)
                        .wrapContentHeight(align = Alignment.CenterVertically),
                    fontSize = titleFontSize,
                )
                if (subtext != null) {
                    val alignment =
                        if ("\n" in subtext) Alignment.Top else Alignment.CenterVertically
                    Text(
                        subtext,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(align = alignment),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardSlider(
    cards: List<DetailCard>,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val state = rememberPagerState(
        pageCount = { cards.size },
        initialPage = index,
    )
    HorizontalPager(state = state) { cardId ->
        DisplayCardInfo(
            card = cards[cardId],
            modifier = modifier
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun DisplayCardInfo(
    card: DetailCard,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.66f)
            .padding(30.dp),
        colors = CardDefaults.cardColors(containerColor = EcruWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
    ) {
        val name = if (card.name.length > 15) card.name.replace(" ", "\n") else card.name
        Text(
            name,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 24.dp),
            color = PinkRed,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
        )
        if (card.costDescription != null) {
            DisplayDescriptionInfo(
                descName = "Cost",
                description = card.costDescription,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (card.effectDescription != null) {
            DisplayDescriptionInfo(
                descName = "Effect",
                description = card.effectDescription,
            )
        }
    }
}

@Composable
fun DisplayDescriptionInfo(
    descName: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(color = SpaceBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = descName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SpaceBlue,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = description,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = SpaceBlue,
            lineHeight = 28.sp,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 12.dp),
        )
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
                        .fillMaxWidth(),
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

private const val DICE_SIZE: Int = 32

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
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$nbMod",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.White,
        )
        Text(
            text = "Mod",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.White,
        )
    }
}

@Composable
fun DisplayGameOver(
    navController: NavController?,
    score: Int,
    success: Boolean,
    newGame: () -> Unit,
) {
    val title = if (success) "Congratulations" else "Game Over"
    Dialog(
        onDismissRequest = newGame,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(30.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    fontSize = 42.sp,
                    modifier = Modifier.padding(top = 32.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Your score is $score.",
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = {
                            navController?.navigate("menu") {
                                popUpTo("game") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .width(100.dp),
                    ) {
                        Text("Back", fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                    Button(
                        onClick = newGame,
                        modifier = Modifier
                            .height(60.dp)
                            .width(100.dp),
                    ) {
                        Text("Play\nagain", fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}


@Composable
fun UpdateViewConfiguration(
    longPressTimeoutMillis: Long? = null,
    doubleTapTimeoutMillis: Long? = null,
    doubleTapMinTimeMillis: Long? = null,
    touchSlop: Float? = null,
    content: @Composable () -> Unit,
) {
    fun ViewConfiguration.updateViewConfiguration() = object : ViewConfiguration {
        override val longPressTimeoutMillis
            get() = longPressTimeoutMillis
                ?: this@updateViewConfiguration.longPressTimeoutMillis

        override val doubleTapTimeoutMillis
            get() = doubleTapTimeoutMillis
                ?: this@updateViewConfiguration.doubleTapTimeoutMillis

        override val doubleTapMinTimeMillis
            get() =
                doubleTapMinTimeMillis
                    ?: this@updateViewConfiguration.doubleTapMinTimeMillis

        override val touchSlop: Float
            get() = touchSlop ?: this@updateViewConfiguration.touchSlop
    }

    CompositionLocalProvider(
        LocalViewConfiguration provides LocalViewConfiguration.current.updateViewConfiguration()
    ) {
        content()
    }
}

@ExperimentalMaterial3Api
@Preview(device = "id:S20 FE")
@Composable
fun GameScreenPreview() {
    val context = LocalContext.current
    val gameStateStorage = remember { GameStateStorage(context) }
    val gameViewModel = remember { GameViewModel(gameStateStorage) }
    gameViewModel.newGame()
    GameScreen(null, gameViewModel)
}