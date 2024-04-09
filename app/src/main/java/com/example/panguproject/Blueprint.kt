package com.example.panguproject

class Blueprint(
    name: String,
    costDescription: String? = null,
    shortCostDescription: String? = null,
    effectDescription: String? = null,
    shortEffectDescription: String? = null,
    val costFunction: ((gameViewModel: GameViewModel) -> Boolean)? = null,
    val clickCostFunction: ((diceList: List<Dice>) -> Boolean)? = null,
    val onClick: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val onStartTurn: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val onBuy: ((gameViewModel: GameViewModel) -> Unit)? = null,
    val onReroll: ((gameViewModel: GameViewModel) -> Unit)? = null,
) : DetailCard(
    name,
    costDescription,
    shortCostDescription,
    effectDescription,
    shortEffectDescription,
) {
    var usable: Boolean = (onClick != null)

    fun copy(used: Boolean): Blueprint {
        val blueprint = Blueprint(
            name,
            costDescription,
            shortCostDescription,
            effectDescription,
            shortEffectDescription,
            costFunction,
            clickCostFunction,
            onClick,
            onStartTurn,
            onBuy,
            onReroll,
        )
        if (used)
            blueprint.usable = false
        return blueprint
    }
}

val defaultBuildingsList: List<Blueprint> = listOf(
//    Blueprint(
//        "Reset",
//        effectDescription = "Reset debug",
//        shortEffectDescription = "Reset debug",
//        clickCostFunction = { true },
//        onClick = GameViewModel::resetGame,
//    ),
    Blueprint(
        "Laboratory",
        effectDescription = "Consume a pair of dice to draw a blueprint.",
        shortEffectDescription = "Pair →\nDraw a blueprint",
        clickCostFunction = { Dice.isOfAKind(2)(it) },
        onClick = { it.consumeDice(); it.drawBlueprint() },
    ),
    Blueprint(
        "Forge",
        effectDescription = "Consume three in a row to gain a stored wild die.",
        shortEffectDescription = "Three in a row →\nStored wild die",
        clickCostFunction = { Dice.isInARow(3)(it) },
        onClick = { it.consumeDice(); it.rollDice(wild = true, stored = true) },
    ),
)

val allBlueprintsList: List<Blueprint> = listOf(
    Blueprint(
        "3D Printer",
        costDescription = "Three dice of value 1, 2 and 3.",
        shortCostDescription = "1, 2, 3",
        effectDescription = "On click: turn a die of value 1 into a wild die.",
        shortEffectDescription = "Click:\n1 → wild",
        costFunction = { Dice.isSet(listOf(1, 2, 3))(it.getSelectedDice()) },
        clickCostFunction = { Dice.isSet(listOf(1))(it) },
        onClick = { it.consumeDice(); it.rollDice(1, wild = true) },
    ),
    Blueprint(
        "AutoMiner",
        costDescription = "Three dice of value 2, 4 and 6.",
        shortCostDescription = "2, 4, 6",
        effectDescription = "On start of turn: gain +1 MOD.",
        shortEffectDescription = "Start of turn:\n+1 MOD",
        costFunction = { Dice.isSet(listOf(2, 4, 6))(it.getSelectedDice()) },
        onStartTurn = { it.gainMod(1) },
    ),
    Blueprint(
        "Backup Plan",
        costDescription = "Three dice of a kind.",
        shortCostDescription = "Three of a kind",
        effectDescription = "When built: gain a stored wild die.\nOn end of turn: gain a stored wild die if no buildings were purchased this turn.",
        shortEffectDescription = "EOT: +1 stored wild\nif no card built",
        costFunction = { Dice.isOfAKind(3)(it.getSelectedDice()) },
        onBuy = { it.rollDice(wild = true, stored = true) },
        onStartTurn = { if (!it.blueprintBuilt) it.rollDice(wild = true, stored = true) },
    ),
    Blueprint(
        "Battery",
        costDescription = "Four dice of a kind.",
        shortCostDescription = "Four of a kind",
        effectDescription = "On end of turn: if at least two dice remain, roll two extra basic dice on the next turn.",
        shortEffectDescription = "EOT: ≥2 dice →\n+2 basic dice",
        costFunction = { Dice.isOfAKind(4)(it.getSelectedDice()) },
        onStartTurn = {
            if (it.nbDiceEndOfTurn >= 2) {
                it.rollDice()
                it.rollDice()
            }
        }
    ),
    Blueprint(
        "Bionic Robot",
        costDescription = "Four dice of a kind.",
        shortCostDescription = "Four of a kind",
        effectDescription = "When rolling, if exactly one die is rerolled, gain an additional basic die.",
        shortEffectDescription = "Reroll one die:\n+1 basic die",
        costFunction = { Dice.isOfAKind(4)(it.getSelectedDice()) },
        onReroll = { if (it.getSelectedDice().size == 1) it.rollDice() },
    ),
    Blueprint(
        "Cloner",
        costDescription = "Five dice in a row.",
        shortCostDescription = "Five in a row",
        effectDescription = "On click: generate a fixed copy of a selected die.",
        shortEffectDescription = "Click: 1 die →\nfixed copy",
        costFunction = { Dice.isInARow(5)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 1 },
        onClick = { it.rollDice(it.getSelectedDice()[0].value, fixed = true) },
    ),
) + (1..2).map {
    Blueprint(
        "Cryosleep",
        costDescription = "Three dice of value 1, 3 and 5.",
        shortCostDescription = "1, 3, 5",
        effectDescription = "On click: fix a selected die and store it.",
        shortEffectDescription = "Click: a die →\nfix and store",
        costFunction = { Dice.isSet(listOf(1, 3, 5))(it.getSelectedDice()) },
        clickCostFunction = { it.size == 1 },
        onClick = { gameViewModel ->
            val dice = gameViewModel.getSelectedDice().first()
            gameViewModel.consumeDice()
            gameViewModel.rollDice(dice.value, fixed = true, stored = true)
        }
    )
} + listOf(
    Blueprint(
        "Dome",
        costDescription = "A full-house of dice. Example: 4, 4, 4, 2, 2",
        shortCostDescription = "Full-house",
        effectDescription = "On start of turn: roll a basic die and store it.",
        shortEffectDescription = "Start of turn:\nroll a stored die",
        costFunction = { Dice.isFullhouse(it.getSelectedDice()) },
        onStartTurn = { it.rollDice(stored = true) },
    )
) + (1..6).map { value ->
    Blueprint(
        "Drone",
        costDescription = "A triple of dice of value $value.",
        shortCostDescription = "$value, $value, $value",
        effectDescription = "On start of turn: gain a fixed die of value $value.",
        shortEffectDescription = "Start of turn:\ngain a fixed $value",
        costFunction = { Dice.isSet(listOf(value, value, value))(it.getSelectedDice()) },
        onStartTurn = { it.rollDice(value, fixed = true) },
    )
} + listOf(
    Blueprint(
        "Extractor",
        costDescription = "Three dice of value 4, 5, 6.",
        shortCostDescription = "4, 5, 6",
        effectDescription = "On click: consume a pair to gain a stored wild die.",
        shortEffectDescription = "Click: a pair →\nstored wild die",
        costFunction = { Dice.isSet(listOf(4, 5, 6))(it.getSelectedDice()) },
        clickCostFunction = { Dice.isOfAKind(2)(it) },
        onClick = { it.consumeDice(); it.rollDice(wild = true, stored = true) },
    ),
    Blueprint(
        "Fission",
        costDescription = "A set of dice that amounts to exactly 20.",
        shortCostDescription = "Sum = 20",
        effectDescription = "On click: split a die into two dice.",
        shortEffectDescription = "Click: a die →\ntwo dice w/ same",
        costFunction = { Dice.sumsTo(20)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 1 && it[0].value > 1 },
        onClick = {
            val value = it.getSelectedDice().first().value
            it.consumeDice()
            it.rollDice(value / 2)
            it.rollDice((value + 1) / 2)
        },
    ),
    Blueprint(
        "Fusion",
        costDescription = "A set of dice that amounts to exactly 12.",
        shortCostDescription = "Sum = 12",
        effectDescription = "On click: combine two selected dice into a die of their sum.",
        shortEffectDescription = "Click: 2 dice →\na die of their sum",
        costFunction = { Dice.sumsTo(12)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 2 && it.sumOf { dice -> dice.value } <= 6 },
        onClick = {
            val value = it.getSelectedDice().sumOf { dice -> dice.value }
            it.consumeDice()
            it.rollDice(value)
        },
    ),
    Blueprint(
        "Monopole",
        costDescription = "Three dice of even value AND all the remaining dice must be of even value too.",
        shortCostDescription = "Three even\nand all even",
        effectDescription = "On start of turn: gain a fixed die of even value.",
        shortEffectDescription = "Start of turn:\ngain a fixed even",
        costFunction = { it.getSelectedDice().size == 3 && it.diceList.value.all { dice -> dice.value % 2 == 0 } },
        onStartTurn = { it.rollDice(2 * (1..3).random(), fixed = true) },
    ),
    Blueprint(
        "Monopole",
        costDescription = "Three dice of odd value AND all the remaining dice must be of odd value too.",
        shortCostDescription = "Three odd\nand all odd",
        effectDescription = "On start of turn: gain a fixed die of odd value.",
        shortEffectDescription = "Start of turn:\ngain a fixed odd",
        costFunction = { it.getSelectedDice().size == 3 && it.diceList.value.all { dice -> dice.value % 2 == 1 } },
        onStartTurn = { it.rollDice(2 * (0..2).random() + 1, fixed = true) },
    ),
    Blueprint(
        "Nanobots",
        costDescription = "Three dice in a row.",
        shortCostDescription = "Three in a row",
        effectDescription = "When built: gain a fixed stored die.\nOn click: reroll a basic or fixed die.",
        shortEffectDescription = "Click: reroll basic\nor fixed die",
        costFunction = { Dice.isInARow(3)(it.getSelectedDice()) },
        onBuy = { it.rollDice(stored = true, fixed = true) },
        clickCostFunction = { it.size == 1 },
        onClick = { it.rerollDice(force = true, useReroll = false) },
    ),
    Blueprint(
        "O.M.N.I.",
        costDescription = "Five dice of a kind.",
        shortCostDescription = "Five of a kind",
        effectDescription = "On start of turn: gain a stored wild die.",
        shortEffectDescription = "Start of turn:\ngain stored wild",
        costFunction = { Dice.isOfAKind(5)(it.getSelectedDice()) },
        onStartTurn = { it.rollDice(wild = true, stored = true) },
    ),
    Blueprint(
        "Observatory",
        costDescription = "Three dice of value 4, 5 and 6.",
        shortCostDescription = "4, 5, 6",
        effectDescription = "When built: draw a blueprint.\nOn discard: gain one more MOD.",
        shortEffectDescription = "Discard:\n+1 MOD",
        costFunction = { Dice.isSet(listOf(4, 5, 6))(it.getSelectedDice()) },
        onBuy = { it.drawBlueprint(); it.increaseBaseMod() },
    ),
    Blueprint(
        "Outpost",
        costDescription = "Three dice in a row.",
        shortCostDescription = "Three in a row",
        effectDescription = "On start of turn: if the turn number is even, roll a basic die.",
        shortEffectDescription = "Even turn:\n+1 basic die",
        costFunction = { Dice.isInARow(3)(it.getSelectedDice()) },
        onStartTurn = { if (it.turn.value % 2 == 0) it.rollDice() },
    ),
    Blueprint(
        "Prospector",
        costDescription = "Four dice of a kind.",
        shortCostDescription = "Four of a kind",
        effectDescription = "On start of turn: gain a stored fixed die.",
        shortEffectDescription = "Start of turn:\ngain stored fixed",
        costFunction = { Dice.isOfAKind(4)(it.getSelectedDice()) },
        onStartTurn = { it.rollDice(fixed = true, stored = true) },
    ),
    Blueprint(
        "Prototype",
        costDescription = "Three dice in a row.",
        shortCostDescription = "Three in a row",
        effectDescription = "On start of turn: gain a fixed die of random value.",
        shortEffectDescription = "Start of turn:\ngain fixed die",
        costFunction = { Dice.isInARow(3)(it.getSelectedDice()) },
        onStartTurn = { it.rollDice(fixed = true) },
    ),
    Blueprint(
        "Qbit devices",
        costDescription = "Two pairs of dice in a row. Example: 2, 2, 3, 3.",
        shortCostDescription = "Two pairs\nin a row",
        effectDescription = "On click: reroll all selected basic dice.",
        shortEffectDescription = "Click: reroll all\nselected dice",
        costFunction = { Dice.isOfAKindInARow(2, 2)(it.getSelectedDice()) },
        clickCostFunction = { it.any { dice -> !dice.fixed && !dice.wild } },
        onClick = { it.rerollDice(useReroll = false) },
    ),
    Blueprint(
        "Radiation",
        costDescription = "A set of dice that amounts to exactly 25.",
        shortCostDescription = "Sum = 25",
        effectDescription = "On click: consume a die to generate two fixed dice of value +1 and -1.",
        shortEffectDescription = "Click: 1 die →\n2 fixed +1/-1",
        costFunction = { Dice.sumsTo(25)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 1 && 1 < it[0].value && it[0].value < 6 },
        onClick = {
            val value = it.getSelectedDice()[0].value
            it.consumeDice()
            it.rollDice(value - 1)
            it.rollDice(value + 1)
        },
    ),
    Blueprint(
        "Reactor",
        costDescription = "A set of dice that amounts to exactly 16.",
        shortCostDescription = "Sum = 16",
        effectDescription = "On click: equalize the value of two selected dice.",
        shortEffectDescription = "Click: 2 dice →\nequalize",
        costFunction = { Dice.sumsTo(16)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 2 && it.none { dice -> dice.fixed || dice.wild } },
        onClick = {
            val value = it.getSelectedDice().sumOf { dice -> dice.value }
            it.consumeDice()
            it.rollDice((value + 1) / 2)
            it.rollDice(value / 2)
        }
    ),
    Blueprint(
        "Recycler",
        costDescription = "Three dice of a kind.",
        shortCostDescription = "Three of a kind",
        effectDescription = "When spending a wild die: roll an extra basic die at the start of next turn.",
        shortEffectDescription = "Use wild die →\n+1 die next turn",
        costFunction = { Dice.isOfAKind(3)(it.getSelectedDice()) },
        onStartTurn = { if (it.usedWildDice) it.rollDice() },
    ),
    Blueprint(
        "Replicator",
        costDescription = "Two wild dice.",
        shortCostDescription = "Two wilds",
        effectDescription = "On start of turn: gain a wild die.",
        shortEffectDescription = "Start of turn:\ngain wild die",
        costFunction = {
            it.getSelectedDice().size == 2 && it.getSelectedDice().all { dice -> dice.wild }
        },
        onStartTurn = { it.rollDice(wild = true) },
    ),
    Blueprint(
        "Self-Repair",
        costDescription = "Four dice of a kind.",
        shortCostDescription = "Four of a kind",
        effectDescription = "On end of turn: if all non-stored diced have been used, gain two basic dice next turn.",
        shortEffectDescription = "EOT: 0 dice →\n+2 dice next turn",
        costFunction = { Dice.isOfAKind(4)(it.getSelectedDice()) },
        onStartTurn = {
            if (!it.hasBasicDiceLeft) {
                it.rollDice()
                it.rollDice()
            }
        },
    ),
) + (1..2).map {
    Blueprint(
        "Settlement",
        costDescription = "Four dice in a row.",
        shortCostDescription = "Four in a row",
        effectDescription = "On start of turn: gain a basic die.",
        shortEffectDescription = "Start of turn:\ngain basic die",
        costFunction = { Dice.isInARow(4)(it.getSelectedDice()) },
        onStartTurn = { it.rollDice() },
    )
} + listOf(
    Blueprint(
        "Shuttle",
        costDescription = "Three dice of a kind.",
        shortCostDescription = "Three of a kind",
        effectDescription = "When built: gain +2 MOD.\nDice might be MODed from 1 to 6 and 6 to 1.",
        shortEffectDescription = "Can MOD 1 ↔ 6",
        costFunction = { Dice.isOfAKind(3)(it.getSelectedDice()) },
        onBuy = { it.gainMod(2); it.allowWrapping() },
    ),
    Blueprint(
        "Treadmill",
        costDescription = "Three dice of value 1, 2 and 3.",
        shortCostDescription = "1, 2, 3",
        effectDescription = "On start of turn: roll one extra basic die per project built.",
        shortEffectDescription = "Start of turn:\n+1 die /-> project",
        costFunction = { Dice.isSet(listOf(1, 2, 3))(it.getSelectedDice()) },
        onStartTurn = {
            val n = it.projectList.value.count { project -> project.built }
            for (i in 1..n) it.rollDice()
        },
    ),
    Blueprint(
        "Tunneler",
        costDescription = "Two pairs of dice in a row.",
        shortCostDescription = "Two pairs\nin a row",
        effectDescription = "On click: flip a selected dice to its opposite value (1 ↔ 6, 2 ↔ 5...).",
        shortEffectDescription = "Click: flip a die",
        costFunction = { Dice.isOfAKindInARow(2, 2)(it.getSelectedDice()) },
        clickCostFunction = { it.size == 1 && !it[0].wild },
        onClick = {
            val value = it.getSelectedDice()[0].value
            it.consumeDice()
            it.rollDice(7 - value)
        },
    ),
)