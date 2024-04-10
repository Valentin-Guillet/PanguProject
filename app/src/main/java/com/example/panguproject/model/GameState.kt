package com.example.panguproject.model

data class GameState(
    val gameOver: Boolean = false,
    val score: Int = 0,
    val turn: Int = 0,
    val diceList: List<Dice> = listOf(),
    val nbRerolls: Int = 2,
    val nbMod: Int = 0,
    val projectStatusList: List<ProjectStatus> = listOf(),
    val blueprintStatusList: List<BlueprintStatus> = listOf(),
    val buildingStatusList: List<BlueprintStatus> = listOf(),
    val baseModDelta: Int = 1,
    val remainingBlueprints: List<BlueprintId> = listOf(),
    val blueprintIndex: Int = 0,
    val blueprintBuiltInTurn: Boolean = false,
    val usedWildDiceInTurn: Boolean = false,
    val wrappingAllowed: Boolean = false,
)
