package com.vguillet.panguproject.model

import kotlinx.serialization.Serializable


@Serializable
data class Dice(
    val value: Int,
    val wild: Boolean = false,
    val fixed: Boolean = false,
    val stored: Boolean = false,
    val selected: Boolean = false,
) {
    companion object {
        fun isOfAKind(number: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != number)
                    return false

                val nonWildDice = diceList.filter { !it.wild }
                return nonWildDice.all { it.value == nonWildDice[0].value }
            }
        }

        fun isInARow(number: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != number)
                    return false

                val nonWildDice = diceList.filter { !it.wild }
                val diceFreq = nonWildDice.groupingBy { it.value }.eachCount()

                if (diceFreq.any { it.value > 1 })
                    return false
                return nonWildDice.maxOf { it.value } - nonWildDice.minOf { it.value } + 1 <= number
            }
        }

        fun isSet(values: List<Int>): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != values.size)
                    return false

                val diceFreq = diceList.filter { !it.wild }.groupingBy { it.value }.eachCount()
                val valFreq = values.groupingBy { it }.eachCount()
                return diceFreq.all { it.value <= valFreq.getOrDefault(it.key, 0) }
            }
        }

        fun isFullhouse(diceList: List<Dice>): Boolean {
            if (diceList.size != 5)
                return false

            val diceFreq =
                diceList.filter { !it.wild }.groupingBy { it.value }.eachCount().values.sorted()
            return diceFreq.isEmpty() || (diceFreq.size == 1 && diceFreq[0] <= 3) ||
                    (diceFreq.size == 2 && diceFreq[0] <= 2 && diceFreq[1] <= 3)
        }

        fun sumsTo(value: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                val nonWildDice = diceList.filter { !it.wild }
                val sum = nonWildDice.sumOf { it.value }
                val nbWild = diceList.size - nonWildDice.size
                return value - sum in nbWild..6 * nbWild
            }
        }

        fun isOfAKindInARow(nbKind: Int, nbRow: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != nbKind * nbRow)
                    return false

                val nonWildDice = diceList.filter { !it.wild }
                val diceFreq = nonWildDice.groupingBy { it.value }.eachCount()
                return nonWildDice.maxOf { it.value } - nonWildDice.minOf { it.value } + 1 <= nbRow
                        && diceFreq.all { it.value <= nbKind }
            }
        }

        fun isSetsOfAKind(nbSets: Int, nbKind: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != nbSets * nbKind)
                    return false

                val diceFreq = diceList.filter { !it.wild }.groupingBy { it.value }.eachCount()
                return diceFreq.size <= nbSets && diceFreq.all { it.value <= nbKind }
            }
        }
    }
}