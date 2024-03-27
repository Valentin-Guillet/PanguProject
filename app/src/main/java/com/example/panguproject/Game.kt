package com.example.panguproject

class Dice(
    var value: Int,
    var wild: Boolean = false,
    var fixed: Boolean = false,
    var stored: Boolean = false,
    var selected: Boolean = false,
) {
    fun copy(
        value: Int = this.value,
        wild: Boolean = this.wild,
        fixed: Boolean = this.fixed,
        stored: Boolean = this.stored,
        selected: Boolean = this.selected,
    ): Dice {
        return Dice(value, wild, fixed, stored, selected)
    }
}

class Game(
    var turn: Int = 0,
    var diceList: MutableList<Dice> = mutableListOf(),
    var mod: Int = 0,
) {
    fun copy(): Game {
        return Game(turn, diceList.toMutableList(), mod)
    }

    fun newTurn() {
        turn++
        val nbDice: Int = computeNbDice()
        diceList = List(nbDice) { Dice((1..6).random()) }.toMutableList()
    }

    private fun computeNbDice(): Int {
        return 6
    }
}