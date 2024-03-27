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
        val gameCopy = _game.value.copy()
        if (selectOnly)
            gameCopy.diceList.forEach { it.selected = false }
        val diceIndex = gameCopy.diceList.indexOf(clickedDice)
        gameCopy.diceList[diceIndex] = clickedDice.copy(selected = !clickedDice.selected)
        _game.value = gameCopy
    }
}