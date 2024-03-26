package com.example.panguproject

data class Dice(
    var value: Int,
    var wild: Boolean = false,
    var fixed: Boolean = false,
    var selected: Boolean = false,
)

data class Game(
    var turn: Int = 0,
    var diceList: MutableList<Dice> = mutableListOf()
) {
    fun copy(): Game {
        return Game(turn, diceList.toMutableList())
    }

    fun newTurn() {
        val nbDice: Int = computeNbDice()
        diceList = List(nbDice) { Dice(it + 1) }.toMutableList()
    }

    private fun computeNbDice(): Int {
        return 2
    }
}