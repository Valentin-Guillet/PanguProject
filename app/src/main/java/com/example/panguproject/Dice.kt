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