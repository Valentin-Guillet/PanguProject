package com.example.panguproject

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.panguproject.ui.theme.BaseDiceColor
import com.example.panguproject.ui.theme.CardColor
import com.example.panguproject.ui.theme.FixedDiceColor
import com.example.panguproject.ui.theme.SelectedDiceBorderColor
import com.example.panguproject.ui.theme.UsableCardBorderColor
import com.example.panguproject.ui.theme.WildDiceColor

@ExperimentalMaterial3Api
@Composable
fun GameScreen(navController: NavController?, gameViewModel: GameViewModel = viewModel()) {
    var displayCardInfoList: List<DetailCard>? by remember { mutableStateOf(null) }
    var displayCardInfoIndex: Int by remember { mutableIntStateOf(0) }

    val score: Int by gameViewModel.score.collectAsState()
    val turn: Int by gameViewModel.turn.collectAsState()
    val diceList: List<Dice> by gameViewModel.diceList.collectAsState()
    val nbRerolls: Int by gameViewModel.nbRerolls.collectAsState()
    val nbMod: Int by gameViewModel.nbMod.collectAsState()
    val projectList: List<Project> by gameViewModel.projectList.collectAsState()
    val buildingList: List<Blueprint> by gameViewModel.buildingList.collectAsState()
    val blueprintList: List<Blueprint> by gameViewModel.blueprintList.collectAsState()
    val logMsg: String by gameViewModel.logMsg.collectAsState()

    fun cardViewFactory(cards: List<DetailCard>): (DetailCard) -> Unit {
        return fun(card: DetailCard) {
            displayCardInfoList = cards
            displayCardInfoIndex = cards.indexOf(card)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val focusAlpha = if (displayCardInfoList != null) 0.5f else 1f
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            GameInfoSection(
                score,
                turn,
                modifier = Modifier.height(24.dp),
                logMsg = logMsg,
            )
            GameProjectSection(
                projectList,
                onProjectClick = gameViewModel::buildProject,
                onProjectLongClick = { cardViewFactory(projectList)(it) },
                modifier = Modifier
                    .weight(0.9f)
                    .alpha(focusAlpha),
            )
            GameBuildingSection(
                buildingList,
                onBlueprintClick = gameViewModel::useBuilding,
                onBlueprintLongClick = { cardViewFactory(buildingList)(it) },
                modifier = Modifier
                    .weight(2f)
                    .alpha(focusAlpha),
            )
            GameBlueprintSection(
                blueprintList,
                onBlueprintClick = gameViewModel::buildBlueprint,
                onBlueprintLongClick = { cardViewFactory(blueprintList)(it) },
                onBlueprintDoubleClick = gameViewModel::discardBlueprint,
                modifier = Modifier
                    .weight(2f)
                    .alpha(focusAlpha),
            )
            GameResourceSection(
                diceList,
                onDiceClick = gameViewModel::selectDice,
                modifier = Modifier
                    .weight(1f)
                    .alpha(focusAlpha),
            )
            GameActionSection(
                nbRerolls = nbRerolls,
                nbMod = nbMod,
                onReroll = gameViewModel::rerollDice,
                onModMinus = gameViewModel::decreaseDiceValue,
                onModPlus = gameViewModel::increaseDiceValue,
                onEndTurn = gameViewModel::nextTurn,
                modifier = Modifier.alpha(focusAlpha),
            )
        }

        if (displayCardInfoList != null) {
            CardSlider(
                cards = displayCardInfoList!!,
                index = displayCardInfoIndex,
                onCardClick = { displayCardInfoList = null; displayCardInfoIndex = 0 },
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
        Text(text = "Score: $score", fontSize = 16.sp)
        if (logMsg != null) {
            Text(text = logMsg, fontSize = 16.sp)
        }
        Text(text = "Turn  $turn / 10", fontSize = 16.sp)
    }
}

@Composable
fun GameProjectSection(
    projects: List<Project>,
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
                    @Suppress("UNCHECKED_CAST") DisplayCard(
                        it,
                        !it.built,
                        onProjectClick as (DetailCard) -> Unit,
                        onProjectLongClick as (DetailCard) -> Unit,
                        modifier = Modifier
                            .alpha(if (it.built) 0.5f else 1f),
                        subtext = it.shortCostDescription,
                    )

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
    buildings: List<Blueprint>,
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
                        @Suppress("UNCHECKED_CAST") DisplayCard(
                            card = blueprint,
                            usable = blueprint.usable,
                            onCardClick = onBlueprintClick as (DetailCard) -> Unit,
                            onCardLongClick = onBlueprintLongClick as (DetailCard) -> Unit,
                            modifier = Modifier
                                .width(colWidth)
                                .height(75.dp),
                            subtext = blueprint.shortEffectDescription,
                            drawBorder = true,
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
                        @Suppress("UNCHECKED_CAST") DisplayCard(
                            card = blueprint,
                            usable = true,
                            onCardClick = onBlueprintClick as (DetailCard) -> Unit,
                            onCardLongClick = onBlueprintLongClick as (DetailCard) -> Unit,
                            onCardDoubleClick = onBlueprintDoubleClick as (DetailCard) -> Unit,
                            modifier = Modifier
                                .width(colWidth)
                                .height(75.dp),
                            subtext = blueprint.shortCostDescription,
                            drawBorder = false,
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
            .clip(shape = RoundedCornerShape(16.dp))
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
                fontSize = 16.sp,
                color = Color.Red.copy(alpha = 0.7f),
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
    val borderColor = if (drawBorder && usable) UsableCardBorderColor else CardColor
    val interactionSource = remember { MutableInteractionSource() }
    val doubleClickCallback: (() -> Unit)? = if (onCardDoubleClick != null) {
        { onCardDoubleClick(card) }
    } else null
    UpdateViewConfiguration(doubleTapTimeoutMillis = 80) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .border(4.dp, borderColor, shape = RoundedCornerShape(8.dp))
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
                val alignment = if ("\n" in subtext) Alignment.Top else Alignment.CenterVertically
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardSlider(
    cards: List<DetailCard>,
    index: Int,
    onCardClick: (DetailCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberPagerState(
        pageCount = { cards.size },
        initialPage = index,
    )
    HorizontalPager(state = state) { cardId ->
        DisplayCardInfo(
            card = cards[cardId],
            onClick = onCardClick,
            modifier = modifier
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun DisplayCardInfo(
    card: DetailCard,
    onClick: (DetailCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(card) },
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 30.dp, horizontal = 30.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
    ) {
        val name = if (card.name.length > 15) card.name.replace(" ", "\n") else card.name
        Text(
            name,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp, bottom = 50.dp),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
        )
        if (card.costDescription != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
                        append("Cost: ")
                    }
                    append(card.costDescription)
                },
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp)
                    .align(Alignment.Start),
                textAlign = TextAlign.Justify,
                fontSize = 20.sp,
                lineHeight = 28.sp,
            )
        }
        if (card.effectDescription != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
                        append("Effect: ")
                    }
                    append(card.effectDescription)
                },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Start),
                textAlign = TextAlign.Justify,
                fontSize = 20.sp,
                lineHeight = 38.sp,
            )
        }
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
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Mod",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
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
            get() = longPressTimeoutMillis ?: this@updateViewConfiguration.longPressTimeoutMillis

        override val doubleTapTimeoutMillis
            get() = doubleTapTimeoutMillis ?: this@updateViewConfiguration.doubleTapTimeoutMillis

        override val doubleTapMinTimeMillis
            get() =
                doubleTapMinTimeMillis ?: this@updateViewConfiguration.doubleTapMinTimeMillis

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
@Preview(device = "id:S9+")
@Composable
fun GameScreenPreview() {
    GameScreen(null)
}