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

    companion object {
        fun isOfAKind(number: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != number)
                    return false

                return diceList.all { it.value == diceList[0].value }
            }
        }

        fun isInARow(number: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != number)
                    return false

                val sortedDiceList = diceList.sortedBy { it.value }
                for (i in 0 until number - 1) {
                    if (sortedDiceList[i].value != sortedDiceList[i + 1].value - 1)
                        return false
                }
                return true
            }
        }

        fun isSet(values: List<Int>): (List<Dice>) -> Boolean {
            val sortedValues = values.sorted()
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != values.size)
                    return false

                return (diceList.map { it.value }.sorted() == sortedValues)
            }
        }

        fun isFullhouse(diceList: List<Dice>): Boolean {
            if (diceList.size != 5)
                return false

            val sortedDiceList = diceList.sortedBy { it.value }
            return sortedDiceList[0].value == sortedDiceList[1].value &&
                    sortedDiceList[3].value == sortedDiceList[4].value &&
                    sortedDiceList[0].value != sortedDiceList[4].value &&
                    (sortedDiceList[2].value == sortedDiceList[1].value ||
                            sortedDiceList[2].value == sortedDiceList[3].value)
        }

        fun sumsTo(value: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                return diceList.sumOf { it.value } == value
            }
        }

        fun isOfAKindInARow(nbKind: Int, nbRow: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != nbKind * nbRow)
                    return false

                val sortedDiceList = diceList.map { it.value }.sorted()
                val correctValues =
                    List(nbRow) { i -> List(nbKind) { sortedDiceList[0] + i } }.flatten()

                return sortedDiceList == correctValues
            }
        }

        fun isSetsOfAKind(nbSets: Int, nbKind: Int): (List<Dice>) -> Boolean {
            return fun(diceList: List<Dice>): Boolean {
                if (diceList.size != nbSets * nbKind)
                    return false

                val freqMap = diceList.groupingBy { it.value }.eachCount()
                return freqMap.size == nbSets && freqMap.all { it.value == nbKind }
            }
        }
    }
}