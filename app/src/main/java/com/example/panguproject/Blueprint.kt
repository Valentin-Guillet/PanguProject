package com.example.panguproject

class Blueprint(
    val name: String,
    val costFunction: ((diceList: List<Dice>) -> Boolean)? = null,
    val clickCostFunction: ((diceList: List<Dice>) -> Boolean)? = null,
    val click: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val startTurn: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val onBuy: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val canApply: ((gameViewModel: GameViewModel) -> Boolean) = { true },
) {
    var usable: Boolean = (click != null)

    fun copy(used: Boolean): Blueprint {
        val blueprint = Blueprint(
            name, costFunction, clickCostFunction, click, startTurn, onBuy, canApply,
        )
        if (used)
            blueprint.usable = false
        return blueprint
    }
}

val listDefaultBuildings: List<Blueprint> = listOf(
    Blueprint("test1"),
    Blueprint("Draw", clickCostFunction = ::isPair, click = GameViewModel::drawBlueprint),
)

val listAllBlueprints: List<Blueprint> = listOf(
    Blueprint("Mod-", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modMinus),
    Blueprint("Mod+", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
    Blueprint("B1", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
    Blueprint("B2", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
    Blueprint("B3", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
    Blueprint("B4", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
    Blueprint("B5", costFunction = ::isPair, clickCostFunction = ::isPair, click = GameViewModel::modPlus),
)