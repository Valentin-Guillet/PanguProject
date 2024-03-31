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

    private val _projectList = MutableStateFlow(mutableListOf<Project>())
    val projectList: StateFlow<List<Project>> = _projectList.asStateFlow()

    private val _blueprintList = MutableStateFlow(mutableListOf<Blueprint>())
    val blueprintList: StateFlow<List<Blueprint>> = _blueprintList.asStateFlow()

    private val _buildingList = MutableStateFlow(mutableListOf<Blueprint>())
    val buildingList: StateFlow<List<Blueprint>> = _buildingList.asStateFlow()

    private lateinit var remainingBlueprints : List<Blueprint>
    private var blueprintIndex : Int = allBlueprintsList.size

    private val initNbBlueprints = 4


    init {
        resetGame()
    }

    fun resetGame() {
        _buildingList.value.clear()

        val newProjectList = allProjectsList.shuffled().take(3).toMutableList()
        _projectList.value = newProjectList

        for (building in defaultBuildingsList)
            _buildingList.value.add(building)

        _blueprintList.value.clear()
        repeat(initNbBlueprints) { _blueprintList.value.add(getNextBlueprint()) }

        _turn.value = 0
        nextTurn()
    }

    fun nextTurn() {
        _turn.value++
        _nbRerolls.value = 2

        val newDiceList = _diceList.value.filter { it.stored }.toMutableList()
        _diceList.value = newDiceList

        repeat(4) { rollDice() }

        val newBuildingList = _buildingList.value.toMutableList().map { it.copy(used = false) }
        _buildingList.value = newBuildingList.toMutableList()

        for (building in _buildingList.value) {
            if (building.canApply(this)) {
                building.startTurn?.invoke(this)
            }
        }
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        val newDiceList = _diceList.value.toMutableList()
        if (selectOnly)
            newDiceList.forEach { it.selected = false }
        val diceIndex: Int = newDiceList.indexOf(clickedDice)
        newDiceList[diceIndex] = clickedDice.copy(selected = !clickedDice.selected)
        _diceList.value = newDiceList
    }

    fun reroll() {
        if (_diceList.value.none { it.selected }) return

        val newDiceList = _diceList.value.toMutableList()
        newDiceList.forEach { if (it.selected) it.value = (1..6).random() }
        _diceList.value = newDiceList

        _nbRerolls.value--
    }

    fun modMinus() {
        _nbMod.value--
    }

    fun modPlus() {
        _nbMod.value++
    }

    fun buildProject(project: Project) {
        val newProjectList = _projectList.value.toMutableList()
        val projectIndex = newProjectList.indexOf(project)
        newProjectList[projectIndex] = project.copy(built = true)
        _projectList.value = newProjectList
    }

    fun drawBlueprint() {
        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.add(getNextBlueprint())
        _blueprintList.value = newBlueprintList
    }

    fun buyBlueprint(blueprint: Blueprint) {
        val selectedDice = getSelectedDice()
        if (!blueprint.costFunction?.invoke(selectedDice)!!)
            return

        val newBlueprintList = _blueprintList.value.toMutableList()
        newBlueprintList.remove(blueprint)
        _blueprintList.value = newBlueprintList

        val newBuildingList = _buildingList.value.toMutableList()
        newBuildingList.add(blueprint)
        _buildingList.value = newBuildingList

        blueprint.onBuy?.invoke(this)
    }

    fun useBuilding(blueprint: Blueprint) {
        if (!blueprint.canApply(this))
            return

        blueprint.click?.invoke(this)

        val newBuildingList = _buildingList.value.toMutableList()
        val buildingIndex: Int = newBuildingList.indexOf(blueprint)
        newBuildingList[buildingIndex] = blueprint.copy(used = true)
        _buildingList.value = newBuildingList
    }

    private fun getNextBlueprint(): Blueprint {
        if (blueprintIndex == allBlueprintsList.size) {
            remainingBlueprints = allBlueprintsList.shuffled()
            blueprintIndex = 0
        }
        return remainingBlueprints[blueprintIndex++]
    }

    private fun rollDice(
        wild: Boolean = false,
        fixed: Boolean = false,
        stored: Boolean = false,
    ) {
        _diceList.value.add(Dice((1..6).random(), wild, fixed, stored))
    }

    private fun getSelectedDice(): List<Dice> {
        return _diceList.value.filter { it.selected }
    }
}