package com.example.panguproject

class Dice(
    private val wild: Boolean = false,
    private val fixed: Boolean = false) {

    private var value: Int = 0

    init {
        roll()
    }
    fun roll() {
        value = (1..6).random()
    }
}