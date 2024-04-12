package com.example.panguproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panguproject.data.GameStateStorage
import com.example.panguproject.model.Blueprint
import com.example.panguproject.model.Dice
import com.example.panguproject.model.Project
import com.example.panguproject.data.allBlueprintsList
import com.example.panguproject.data.allProjectsList
import com.example.panguproject.model.BlueprintStatus
import com.example.panguproject.model.GameState
import com.example.panguproject.model.ProjectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameViewModel(
    val gameStateStorage: GameStateStorage,
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _logMsg = MutableStateFlow("")
    val logMsg: StateFlow<String> = _logMsg.asStateFlow()

    internal var nbDiceEndOfTurn = 0
    internal var hasBasicDiceLeft = false

    private val initNbBlueprints = 3
    private val initNbDice = 4

    fun loadGame() {
        val savedGameState = gameStateStorage.loadState()
        if (savedGameState != null) {
            _gameState.value = savedGameState
        } else {
            newGame()
        }
    }

    fun newGame() {
        val newProjectList =
            allProjectsList.indices.shuffled().take(3).map { ProjectStatus(it, false) }
                .toMutableList()
        val newBuildingList =
            allBlueprintsList.filter { it.isDefault }.indices.map { BlueprintStatus(it, false) }
                .toMutableList()
        val newBlueprintList = List(initNbBlueprints) { getNextBlueprint() }
        val newGameState = GameState(
            projectStatusList = newProjectList,
            buildingStatusList = newBuildingList,
            blueprintStatusList = newBlueprintList,
        )
        _gameState.value = newGameState

        nextTurn()

        viewModelScope.launch { gameStateStorage.clearState() }
    }

    fun nextTurn() {
        if (gameState.value.turn == 10) {
            _gameState.value = _gameState.value.copy(gameOver = true)
            return
        }

        nbDiceEndOfTurn = _gameState.value.diceList.size
        hasBasicDiceLeft = _gameState.value.diceList.any { !it.stored }

        val nextDiceList = _gameState.value.diceList
            .filter { it.stored }
            .map { it.copy(selected = false) } + List(initNbDice) { Dice((1..6).random()) }

        val nextBuildingList = _gameState.value.buildingStatusList
            .map { it.copy(usable = (allBlueprintsList[it.id].onClick != null)) }

        val newGameState = _gameState.value.copy(
            turn = _gameState.value.turn + 1,
            nbRerolls = 2,
            diceList = nextDiceList,
            buildingStatusList = nextBuildingList,
        )
        _gameState.value = newGameState

        for (building in _gameState.value.buildingStatusList)
            allBlueprintsList[building.id].onStartTurn?.invoke(this)

        _logMsg.value = ""

        _gameState.value = _gameState.value.copy(
            blueprintBuiltInTurn = false,
            usedWildDiceInTurn = false,
        )
        drawBlueprint()
        saveState()
    }

    fun rollDice(
        value: Int? = null,
        wild: Boolean = false,
        fixed: Boolean = false,
        stored: Boolean = false,
    ) {
        _logMsg.value = ""
        val dieValue = value ?: (1..6).random()
        _gameState.value = _gameState.value.copy(
            diceList = _gameState.value.diceList + listOf(Dice(dieValue, wild, fixed, stored))
        )
    }

    fun decreaseDiceValue() {
        modifySelectedDice(-1)
    }

    fun increaseDiceValue() {
        modifySelectedDice(1)
    }

    fun gainMod(delta: Int) {
        _logMsg.value = ""
        _gameState.value = _gameState.value.copy(
            nbMod = _gameState.value.nbMod + delta
        )
    }

    fun increaseBaseMod() {
        _gameState.value = _gameState.value.copy(
            baseModDelta = _gameState.value.baseModDelta + 1
        )
    }

    fun consumeDice() {
        _logMsg.value = ""
        val (selectedDice, unselectedDice) = _gameState.value.diceList.partition { it.selected }

        _gameState.value = _gameState.value.copy(
            diceList = unselectedDice.toMutableList(),
            usedWildDiceInTurn = _gameState.value.usedWildDiceInTurn || selectedDice.any { it.wild },
        )
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        _logMsg.value = ""
        var newDiceList = _gameState.value.diceList.toMutableList()
        if (selectOnly)
            newDiceList = newDiceList.map { it.copy(selected = false) }.toMutableList()
        val diceIndex: Int = newDiceList.indexOfFirst { it === clickedDice }
        if (diceIndex == -1)
            return
        newDiceList[diceIndex] = clickedDice.copy(selected = !clickedDice.selected)

        _gameState.value = _gameState.value.copy(diceList = newDiceList)
    }

    fun getSelectedDice(): List<Dice> {
        return _gameState.value.diceList.filter { it.selected }
    }

    fun rerollDice(force: Boolean = false, useReroll: Boolean = true) {
        if (_gameState.value.diceList.none { it.selected && !it.wild && (force || !it.fixed) }) {
            _logMsg.value = "No basic dice to reroll"
            return
        }
        _logMsg.value = ""

        val newDiceList = _gameState.value.diceList.toMutableList()
        for (i in 0 until newDiceList.size) {
            val dice = newDiceList[i]
            if (dice.selected && !dice.wild && (force || !dice.fixed))
                newDiceList[i] = newDiceList[i].copy(value = (1..6).random())
        }
        _gameState.value = _gameState.value.copy(diceList = newDiceList)

        for (building in _gameState.value.buildingStatusList)
            allBlueprintsList[building.id].onReroll?.invoke(this)

        if (useReroll)
            _gameState.value = _gameState.value.copy(nbRerolls = _gameState.value.nbRerolls - 1)
        saveState()
    }

    fun buildProject(project: Project) {
        val newProjectList = _gameState.value.projectStatusList.toMutableList()
        if (!project.costFunction(this)) {
            _logMsg.value = "Invalid requirements"
            return
        }

        _logMsg.value = ""
        consumeDice()
        newProjectList[project.id] = newProjectList[project.id].copy(built = true)
        val newScore = _gameState.value.score + 3 * (11 - _gameState.value.turn) + 1

        val gameOver = newProjectList.all { it.built }

        _gameState.value = _gameState.value.copy(
            gameOver = gameOver,
            score = newScore,
            projectStatusList = newProjectList,
        )
        saveState()
    }

    fun drawBlueprint() {
        val newBlueprintList = _gameState.value.blueprintStatusList.toMutableList()
        val newBlueprint = getNextBlueprint()
        if (newBlueprintList.size >= 12) {
            _logMsg.value = "Too many blueprints"
            gainMod(_gameState.value.baseModDelta)
            return
        }

        _logMsg.value = ""
        newBlueprintList.add(newBlueprint)
        _gameState.value = _gameState.value.copy(
            blueprintStatusList = newBlueprintList
        )
        saveState()
    }

    fun buildBlueprint(blueprint: Blueprint) {
        if (!blueprint.costFunction?.invoke(this)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        consumeDice()

        val newBlueprintList = _gameState.value.blueprintStatusList.filter { it.id != blueprint.id }
        val newBuildingList = _gameState.value.buildingStatusList.toMutableList()
        newBuildingList += listOf(
            BlueprintStatus(
                blueprint.id, usable = (allBlueprintsList[blueprint.id].onClick != null)
            )
        )
        newBuildingList.sortBy { allBlueprintsList[it.id].onClick == null }

        _gameState.value = _gameState.value.copy(
            score = _gameState.value.score + 1,
            blueprintStatusList = newBlueprintList,
            buildingStatusList = newBuildingList,

            blueprintBuiltInTurn = true,
        )

        blueprint.onBuy?.invoke(this)
        saveState()
    }

    fun discardBlueprint(blueprint: Blueprint) {
        gainMod(_gameState.value.baseModDelta)

        _gameState.value = _gameState.value.copy(
            blueprintStatusList = _gameState.value.blueprintStatusList.filter { it.id != blueprint.id },
        )
        saveState()
    }

    fun useBuilding(blueprint: Blueprint) {
        val selectedDice = getSelectedDice()
        if (!blueprint.clickCostFunction?.invoke(selectedDice)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        blueprint.onClick?.invoke(this)

        val newBuildingList = _gameState.value.buildingStatusList.toMutableList()
        val buildingIndex: Int = newBuildingList.indexOfFirst { it.id == blueprint.id }
        if (buildingIndex == -1)
            return
        newBuildingList[buildingIndex] = newBuildingList[buildingIndex].copy(usable = false)

        _gameState.value = _gameState.value.copy(buildingStatusList = newBuildingList)
        saveState()
    }

    fun allowWrapping() {
        _gameState.value = _gameState.value.copy(wrappingAllowed = true)
        saveState()
    }


    private fun modifySelectedDice(delta: Int) {
        val selectedDice = getSelectedDice()
        if (selectedDice.size != 1) {
            _logMsg.value = "Too many dice"
            return
        }

        val dice = selectedDice[0]

        if (!dice.wild && _gameState.value.nbMod == 0) {
            _logMsg.value = "No more modifiers"
            return
        }
        _logMsg.value = ""

        if (!_gameState.value.wrappingAllowed && (dice.value + delta < 1 || dice.value + delta > 6))
            return

        val newValue = (dice.value + delta + 5) % 6 + 1

        val newDiceList = _gameState.value.diceList.toMutableList()
        val diceIndex = newDiceList.indexOfFirst { it === dice }
        if (diceIndex == -1)
            return
        newDiceList[diceIndex] = dice.copy(value = newValue)

        _gameState.value = _gameState.value.copy(
            diceList = newDiceList,
            nbMod = if (!dice.wild) _gameState.value.nbMod else _gameState.value.nbMod - 1
        )
        saveState()
    }

    private fun getNextBlueprint(): BlueprintStatus {
        if (_gameState.value.blueprintIndex == _gameState.value.remainingBlueprints.size) {
            _gameState.value = _gameState.value.copy(
                remainingBlueprints = allBlueprintsList.filter { !it.isDefault }.map { it.id }
                    .shuffled(),
                blueprintIndex = 0,
            )
        }

        val blueprintId = _gameState.value.remainingBlueprints[_gameState.value.blueprintIndex]
        _gameState.value = _gameState.value.copy(
            blueprintIndex = _gameState.value.blueprintIndex + 1
        )
        return BlueprintStatus(
            blueprintId,
            usable = (allBlueprintsList[blueprintId].onClick != null)
        )
    }

    private fun saveState() {
        viewModelScope.launch {
            gameStateStorage.saveState(_gameState.value)
        }
    }
}