package com.example.panguproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _game = MutableStateFlow(Game())
    val game: StateFlow<Game> = _game.asStateFlow()

    init {
        resetGame()
    }

    fun resetGame() {
        _game.value = Game()
        _game.value.newTurn()
    }

    fun nextTurn() {
        _game.value.newTurn()
    }

    fun selectDice(clickedDice: Dice, selectOnly: Boolean) {
        clickedDice.selected = !clickedDice.selected
        _game.value = _game.value.copy()
    }

    fun reroll() {
        if (getSelectedDice().isEmpty()) return

        for (dice in _game.value.diceList) {
            if (dice.selected) dice.value = (1..6).random()
        }
        _game.value.nbRerolls--
        _game.value = _game.value.copy()
    }

    fun modMinus() {
        val gameCopy = _game.value.copy()
        gameCopy.mod--
        _game.value = gameCopy
    }

    fun modPlus() {
        val gameCopy = _game.value.copy()
        gameCopy.mod++
        _game.value = gameCopy
    }

    private fun getSelectedDice(): List<Dice> = _game.value.diceList.filter { it.selected }
}