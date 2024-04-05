package com.example.panguproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
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

    private lateinit var remainingBlueprints: List<Blueprint>
    private var blueprintIndex: Int = allBlueprintsList.size

    private val initNbBlueprints = 4
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
        _buildingList.value.clear()

        val newProjectList = allProjectsList.shuffled().take(3).toMutableList()
        _projectList.value = newProjectList

        for (building in defaultBuildingsList)
            _buildingList.value.add(building)

        blueprintIndex = allBlueprintsList.size

        _blueprintList.value.clear()
        repeat(initNbBlueprints) { _blueprintList.value.add(getNextBlueprint()) }

        _diceList.value.clear()

        wrappingAllowed = false
        baseModDelta = 1
        _nbMod.value = 0
        _turn.value = 0
        nextTurn()
    }

    fun nextTurn() {
        nbDiceEndOfTurn = _diceList.value.size
        hasBasicDiceLeft = _diceList.value.any { !it.stored }

        _turn.value++
        _nbRerolls.value = 2

        _diceList.value = _diceList.value.filter { it.stored }.toMutableList()
        _diceList.value = _diceList.value.map { it.copy(selected = false) }.toMutableList()

        repeat(16) { rollDice() }

        val newBuildingList = _buildingList.value.toMutableList().map { it.copy(used = false) }
        _buildingList.value = newBuildingList.toMutableList()

        for (building in _buildingList.value) {
            building.onStartTurn?.invoke(this)
        }

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
        val dieValue = value ?: (1..6).random()
        _diceList.value.add(Dice(dieValue, wild, fixed, stored))
    }

    fun decreaseDiceValue(force: Boolean = false) {
        modifySelectedDice(-1, force = force)
    }

    fun increaseDiceValue(force: Boolean = false) {
        modifySelectedDice(1, force = force)
    }

    fun gainMod(delta: Int) {
        _nbMod.value += delta
    }

    fun increaseBaseMod() {
        baseModDelta++
    }

    fun consumeDice() {
        val (selectedDice, unselectedDice) = _diceList.value.partition { it.selected }
        usedWildDice = usedWildDice || selectedDice.any { it.wild }

        _diceList.value = unselectedDice.toMutableList()
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        val newDiceList = _diceList.value.toMutableList()
        if (selectOnly)
            newDiceList.forEach { it.selected = false }
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
        if (_diceList.value.none { it.selected && (force || !it.fixed) }) return

        val newDiceList = _diceList.value.toMutableList()
        for (i in 0 until newDiceList.size) {
            if (newDiceList[i].selected && (force || !newDiceList[i].fixed))
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
        if (projectIndex == -1 || !project.costFunction(this))
            return

        consumeDice()
        newProjectList[projectIndex] = project.copy(built = true)
        _projectList.value = newProjectList
    }

    fun drawBlueprint() {
        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.add(getNextBlueprint())
        _blueprintList.value = newBlueprintList
    }

    fun buildBlueprint(blueprint: Blueprint) {
        if (!blueprint.costFunction?.invoke(this)!!)
            return

        consumeDice()

        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.remove(blueprint)
        _blueprintList.value = newBlueprintList

        val newBuildingList = _buildingList.value.toMutableList()
        newBuildingList.add(blueprint)
        _buildingList.value = newBuildingList

        blueprint.onBuy?.invoke(this)

        blueprintBuilt = true
    }

    fun discardBlueprint(blueprint: Blueprint) {
        gainMod(baseModDelta)

        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.remove(blueprint)
        _blueprintList.value = newBlueprintList
    }

    fun useBuilding(blueprint: Blueprint) {
        val selectedDice = getSelectedDice()
        if (!blueprint.clickCostFunction?.invoke(selectedDice)!!)
            return

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


    private fun modifySelectedDice(delta: Int, force: Boolean = false) {
        val selectedDice = getSelectedDice()
        if (selectedDice.size != 1)
            return

        val dice = selectedDice[0]

        if (dice.fixed && !force)
            return

        if (!dice.wild && _nbMod.value == 0)
            return

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