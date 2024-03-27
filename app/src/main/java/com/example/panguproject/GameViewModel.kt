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

    init {
        resetGame()
    }

    fun resetGame() {
        _turn.value = 0
        nextTurn()
    }

    fun nextTurn() {
        _turn.value++
        _nbRerolls.value = 2
        createAndRollDice()
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        val newDiceList = _diceList.value.toMutableList()
        if (selectOnly)
            newDiceList.forEach { it.selected = false }
        val diceIndex: Int = newDiceList.indexOf(clickedDice)
        newDiceList[diceIndex] = clickedDice.copy(selected = !clickedDice.selected)
        _diceList.value = newDiceList.toMutableList()
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

    private fun createAndRollDice() {
        val newDiceList = _diceList.value.filter { it.stored }.toMutableList()
        repeat(4) { newDiceList.add(Dice((1..6).random())) }

        _diceList.value = newDiceList
    }
}