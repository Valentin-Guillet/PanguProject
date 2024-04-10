package com.example.panguproject

import androidx.lifecycle.ViewModel
import com.example.panguproject.model.Blueprint
import com.example.panguproject.model.Dice
import com.example.panguproject.model.Project
import com.example.panguproject.data.allBlueprintsList
import com.example.panguproject.data.allProjectsList
import com.example.panguproject.model.BlueprintId
import com.example.panguproject.model.BlueprintStatus
import com.example.panguproject.model.ProjectStatus
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

    private val _projectStatusList = MutableStateFlow(mutableListOf<ProjectStatus>())
    val projectStatusList: StateFlow<List<ProjectStatus>> = _projectStatusList.asStateFlow()

    private val _blueprintStatusList = MutableStateFlow(mutableListOf<BlueprintStatus>())
    val blueprintStatusList: StateFlow<List<BlueprintStatus>> = _blueprintStatusList.asStateFlow()

    private val _buildingStatusList = MutableStateFlow(mutableListOf<BlueprintStatus>())
    val buildingStatusList: StateFlow<List<BlueprintStatus>> = _buildingStatusList.asStateFlow()

    private val _logMsg = MutableStateFlow("")
    val logMsg: StateFlow<String> = _logMsg.asStateFlow()

    private var remainingBlueprints: List<BlueprintId> = listOf()
    private var blueprintIndex: Int = 0

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
        blueprintIndex = remainingBlueprints.size
        baseModDelta = 1
        wrappingAllowed = false

        _projectStatusList.value =
            allProjectsList.indices.shuffled().take(3).map { ProjectStatus(it, false) }
                .toMutableList()
        _buildingStatusList.value =
            allBlueprintsList.filter { it.isDefault }.indices.map { BlueprintStatus(it, false) }
                .toMutableList()

        _blueprintStatusList.value.clear()
        repeat(initNbBlueprints) { _blueprintStatusList.value.add(getNextBlueprint()) }

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

        val newBuildingList =
            _buildingStatusList.value.toMutableList()
                .map { it.copy(usable = (allBlueprintsList[it.id].onClick != null)) }
        _buildingStatusList.value = newBuildingList.toMutableList()

        for (building in _buildingStatusList.value)
            allBlueprintsList[building.id].onStartTurn?.invoke(this)

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

        for (building in _buildingStatusList.value)
            allBlueprintsList[building.id].onReroll?.invoke(this)

        if (useReroll)
            _nbRerolls.value--
    }

    fun buildProject(project: Project) {
        val newProjectList = _projectStatusList.value.toMutableList()
        if (!project.costFunction(this)) {
            _logMsg.value = "Invalid requirements"
            return
        }

        _logMsg.value = ""
        consumeDice()
        newProjectList[project.id] = newProjectList[project.id].copy(built = true)
        _projectStatusList.value = newProjectList

        _score.value += 3 * (11 - turn.value) + 1

        if (_projectStatusList.value.all { it.built })
            _gameOver.value = true
    }

    fun drawBlueprint() {
        val newBlueprintList = _blueprintStatusList.value.toMutableList()
        val newBlueprint = getNextBlueprint()
        if (newBlueprintList.size >= 12) {
            _logMsg.value = "Too many blueprints"
            gainMod(baseModDelta)
            return
        }

        _logMsg.value = ""
        newBlueprintList.add(newBlueprint)
        _blueprintStatusList.value = newBlueprintList
    }

    fun buildBlueprint(blueprint: Blueprint) {
        if (!blueprint.costFunction?.invoke(this)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        consumeDice()

        val newBlueprintList =
            _blueprintStatusList.value.filter { it.id != blueprint.id }.toMutableList()
        _blueprintStatusList.value = newBlueprintList

        val newBuildingList = _buildingStatusList.value.toMutableList()
        newBuildingList.add(
            BlueprintStatus(
                blueprint.id,
                usable = (allBlueprintsList[blueprint.id].onClick != null)
            )
        )
        newBuildingList.sortBy { allBlueprintsList[it.id].onClick == null }
        _buildingStatusList.value = newBuildingList

        blueprint.onBuy?.invoke(this)

        blueprintBuilt = true
        _score.value++
    }

    fun discardBlueprint(blueprint: Blueprint) {
        gainMod(baseModDelta)

        val newBlueprintList =
            _blueprintStatusList.value.filter { it.id != blueprint.id }.toMutableList()
        _blueprintStatusList.value = newBlueprintList
    }

    fun useBuilding(blueprint: Blueprint) {
        val selectedDice = getSelectedDice()
        if (!blueprint.clickCostFunction?.invoke(selectedDice)!!) {
            _logMsg.value = "Invalid requirements"
            return
        }
        _logMsg.value = ""

        blueprint.onClick?.invoke(this)

        val newBuildingList = _buildingStatusList.value.toMutableList()
        val buildingIndex: Int = newBuildingList.indexOfFirst { it.id == blueprint.id }
        if (buildingIndex == -1)
            return
        newBuildingList[buildingIndex] = newBuildingList[buildingIndex].copy(usable = false)
        _buildingStatusList.value = newBuildingList
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

    private fun getNextBlueprint(): BlueprintStatus {
        if (blueprintIndex == remainingBlueprints.size) {
            remainingBlueprints =
                allBlueprintsList.filter { !it.isDefault }.map { it.id }.shuffled()
            blueprintIndex = 0
        }

        val blueprintId = remainingBlueprints[blueprintIndex++]
        return BlueprintStatus(
            blueprintId,
            usable = (allBlueprintsList[blueprintId].onClick != null)
        )
    }
}