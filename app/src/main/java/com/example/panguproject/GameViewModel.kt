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

    fun selectDice(dice: Dice) {
        val gameCopy = _game.value.copy()
        val diceIndex = gameCopy.diceList.indexOfFirst { it == dice }
        if (diceIndex != -1) {
            gameCopy.diceList[diceIndex] = dice.copy(selected = !dice.selected)
            _game.value = gameCopy
        }
    }
}