package com.example.panguproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameOver = MutableStateFlow(false)
    val gameOver: StateFlow<Boolean> = _gameOver.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _turn = MutableStateFlow(0)
    val turn: StateFlow<Int> = _turn.asStateFlow()

    private val _diceList = MutableStateFlow(mutableListOf<Dice>())
    val diceList: StateFlow<List<Dice>> = _diceList.asStateFlow()

    private val _nbRerolls = MutableStateFlow(2)
    val nbRerolls: StateFlow<Int> = _nbRerolls.asStateFlow()

    private val _nbMod = MutableStateFlow(0)
    val nbMod: StateFlow<Int> = _nbMod.asStateFlow()
    private var baseModDelta = 1

    private val _projectList = MutableStateFlow(mutableListOf<Project>())
    val projectList: StateFlow<List<Project>> = _projectList.asStateFlow()

    private val _blueprintList = MutableStateFlow(mutableListOf<Blueprint>())
    val blueprintList: StateFlow<List<Blueprint>> = _blueprintList.asStateFlow()

    private val _buildingList = MutableStateFlow(mutableListOf<Blueprint>())
    val buildingList: StateFlow<List<Blueprint>> = _buildingList.asStateFlow()

    private val _logMsg = MutableStateFlow("")
    val logMsg: StateFlow<String> = _logMsg.asStateFlow()

    private lateinit var remainingBlueprints: List<Blueprint>
    private var blueprintIndex: Int = allBlueprintsList.size

    private val initNbBlueprints = 3
    var blueprintBuilt = false
        private set
    var nbDiceEndOfTurn = 0
        private set
    var usedWildDice = false
        private set
    var hasBasicDiceLeft = false
        private set
    private var wrappingAllowed = false


    init {
        resetGame()
    }

    fun resetGame() {
        _gameOver.value = false
        _score.value = 0
        _turn.value = 0
        _nbMod.value = 0
        blueprintIndex = allBlueprintsList.size
        baseModDelta = 1
        wrappingAllowed = false

        _projectList.value = allProjectsList.shuffled().take(3).toMutableList()
        _buildingList.value = defaultBuildingsList.toMutableList()

        _blueprintList.value.clear()
        repeat(initNbBlueprints) { _blueprintList.value.add(getNextBlueprint()) }

        _diceList.value.clear()

        nextTurn()
    }

    fun nextTurn() {
        if (_turn.value == 10) {
            _gameOver.value = true
            return
        }

        nbDiceEndOfTurn = _diceList.value.size
        hasBasicDiceLeft = _diceList.value.any { !it.stored }

        _turn.value++
        _nbRerolls.value = 2

        _diceList.value = _diceList.value.filter { it.stored }.toMutableList()
        _diceList.value = _diceList.value.map { it.copy(selected = false) }.toMutableList()

        repeat(4) { rollDice() }

        val newBuildingList = _buildingList.value.toMutableList().map { it.copy(used = false) }
        _buildingList.value = newBuildingList.toMutableList()

        for (building in _buildingList.value)
            building.onStartTurn?.invoke(this)

        _logMsg.value = ""

        blueprintBuilt = false
        usedWildDice = false
        drawBlueprint()
    }

    fun rollDice(
        value: Int? = null,
        wild: Boolean = false,
        fixed: Boolean = false,
        stored: Boolean = false,
    ) {
        _logMsg.value = ""
        val dieValue = value ?: (1..6).random()
        _diceList.value.add(Dice(dieValue, wild, fixed, stored))
    }

    fun decreaseDiceValue() {
        modifySelectedDice(-1)
    }

    fun increaseDiceValue() {
        modifySelectedDice(1)
    }

    fun gainMod(delta: Int) {
        _logMsg.value = ""
        _nbMod.value += delta
    }

    fun increaseBaseMod() {
        baseModDelta++
    }

    fun consumeDice() {
        _logMsg.value = ""
        val (selectedDice, unselectedDice) = _diceList.value.partition { it.selected }
        usedWildDice = usedWildDice || selectedDice.any { it.wild }

        _diceList.value = unselectedDice.toMutableList()
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        _logMsg.value = ""
        var newDiceList = _diceList.value.toMutableList()
        if (selectOnly)
            newDiceList = newDiceList.map { it.copy(selected = false) }.toMutableList()
        val diceIndex: Int = newDiceList.indexOf(clickedDice)
        if (diceIndex == -1)
            return
        newDiceList[diceIndex] = clickedDice.copy(selected = !clickedDice.selected)
        _diceList.value = newDiceList
    }

    fun getSelectedDice(): List<Dice> {
        return _diceList.value.filter { it.selected }
    }

    fun rerollDice(force: Boolean = false, useReroll: Boolean = true) {
        if (_diceList.value.none { it.selected && !it.wild && (force || !it.fixed) }) {
            _logMsg.value = "No basic dice to reroll"
            return
        }
        _logMsg.value = ""

        val newDiceList = _diceList.value.toMutableList()
        for (i in 0 until newDiceList.size) {
            val dice = newDiceList[i]
            if (dice.selected && !dice.wild && (force || !dice.fixed))
                newDiceList[i] = newDiceList[i].copy(value = (1..6).random())
        }
        _diceList.value = newDiceList

        for (building in _buildingList.value) {
            building.onReroll?.invoke(this)
        }

        if (useReroll)
            _nbRerolls.value--
    }

    fun buildProject(project: Project) {
        val newProjectList = _projectList.value.toMutableList()
        val projectIndex = newProjectList.indexOf(project)
        if (projectIndex == -1 || !project.costFunction(this)) {
            _logMsg.value = "Invalid requirements"
            return
        }

        _logMsg.value = ""
        consumeDice()
        newProjectList[projectIndex] = project.copy(built = true)
        _projectList.value = newProjectList

        _score.value += 3 * (11 - turn.value) + 1

        if (_projectList.value.all { it.built })
            _gameOver.value = true
    }

    fun drawBlueprint() {
        val newBlueprintList = _blueprintList.value.toMutableList()
        val newBlueprint = getNextBlueprint()
        if (newBlueprintList.size >= 12) {
            _logMsg.value = "Too many blueprints"
            gainMod(baseModDelta)
            return
        }

        _logMsg.value = ""
        newBlueprintList.add(newBlueprint)
        _blueprintList.value = newBlueprintList
    }

    fun buildBlueprint(blueprint: Blueprint) {
        if (!blueprint.costFunction?.invoke(this)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        consumeDice()

        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.remove(blueprint)
        _blueprintList.value = newBlueprintList

        val newBuildingList = _buildingList.value.toMutableList()
        newBuildingList.add(blueprint)
        newBuildingList.sortBy { it.onClick == null }
        _buildingList.value = newBuildingList

        blueprint.onBuy?.invoke(this)

        blueprintBuilt = true
        _score.value++
    }

    fun discardBlueprint(blueprint: Blueprint) {
        gainMod(baseModDelta)

        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.remove(blueprint)
        _blueprintList.value = newBlueprintList
    }

    fun useBuilding(blueprint: Blueprint) {
        val selectedDice = getSelectedDice()
        if (!blueprint.clickCostFunction?.invoke(selectedDice)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        blueprint.onClick?.invoke(this)

        val newBuildingList = _buildingList.value.toMutableList()
        val buildingIndex: Int = newBuildingList.indexOf(blueprint)
        if (buildingIndex == -1)
            return
        newBuildingList[buildingIndex] = blueprint.copy(used = true)
        _buildingList.value = newBuildingList
    }

    fun allowWrapping() {
        wrappingAllowed = true
    }


    private fun modifySelectedDice(delta: Int) {
        val selectedDice = getSelectedDice()
        if (selectedDice.size != 1) {
            _logMsg.value = "Too many dice"
            return
        }

        val dice = selectedDice[0]

        if (!dice.wild && _nbMod.value == 0) {
            _logMsg.value = "No more modifiers"
            return
        }
        _logMsg.value = ""

        if (!wrappingAllowed && (dice.value + delta < 1 || dice.value + delta > 6))
            return

        val newValue = (dice.value + delta + 5) % 6 + 1

        val newDiceList = _diceList.value.toMutableList()
        val diceIndex = newDiceList.indexOf(dice)
        if (diceIndex == -1)
            return
        newDiceList[diceIndex] = dice.copy(value = newValue)
        _diceList.value = newDiceList.toMutableList()

        if (!dice.wild)
            _nbMod.value--
    }

    private fun getNextBlueprint(): Blueprint {
        if (blueprintIndex == allBlueprintsList.size) {
            remainingBlueprints = allBlueprintsList.shuffled()
            blueprintIndex = 0
        }

        return remainingBlueprints[blueprintIndex++]
    }
}