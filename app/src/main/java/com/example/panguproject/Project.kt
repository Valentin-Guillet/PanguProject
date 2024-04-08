package com.example.panguproject

class Project(
    name: String,
    costDescription: String? = null,
    shortCostDescription: String? = null,
    val costFunction: (gameViewModel: GameViewModel) -> Boolean,
    val built: Boolean = false,
) : DetailCard(
    name,
    costDescription = costDescription,
    shortCostDescription = shortCostDescription,
) {
    fun copy(built: Boolean): Project {
        return Project(
            name,
            costDescription,
            shortCostDescription,
            costFunction,
            built,
        )
    }
}

val allProjectsList: List<Project> = listOf(
    Project(
        "Athena Project",
        costDescription = "Four wild dice.",
        shortCostDescription = "Four wilds",
        costFunction = {
            it.getSelectedDice().size == 4 && it.getSelectedDice().all { dice -> dice.wild }
        },
    ),
    Project(
        "Dazbog Project",
        costDescription = "Seven dice of a kind.",
        shortCostDescription = "Seven of a kind",
        costFunction = { Dice.isOfAKind(7)(it.getSelectedDice()) },
    ),
    Project(
        "Freya Project",
        costDescription = "Four pairs of dice in a row.\n\tExample: 2, 2, 3, 3, 4, 4, 5, 5",
        shortCostDescription = "Four pairs\nin a row",
        costFunction = { Dice.isOfAKindInARow(2, 4)(it.getSelectedDice()) },
    ),
    Project(
        "Herus Project",
        costDescription = "Six in a row (so 1, 2, 3, 4, 5, 6)\n\tThis project can't be purchased" +
                "if a reroll has been used this turn, (building effects do not count as rerolls).",
        shortCostDescription = "Six in a row\n(no reroll)",
        costFunction = { Dice.isInARow(6)(it.getSelectedDice()) && it.nbRerolls.value == 2 },
    ),
    Project(
        "Inari Project",
        costDescription = "Six dice of value 1.",
        shortCostDescription = "Six 1s",
        costFunction = { Dice.isSet(listOf(1, 1, 1, 1, 1, 1))(it.getSelectedDice()) },
    ),
    Project(
        "Pangu Project",
        costDescription = "A set of dice that amounts to exactly 40.",
        shortCostDescription = "Sum = 40",
        costFunction = { Dice.sumsTo(40)(it.getSelectedDice()) },
    ),
    Project(
        "Quetzalcoatl Project",
        costDescription = "Ten even dice OR ten odd dice. Each stored die count as two.",
        shortCostDescription = "10 even OR odd\n(stored count x2)",
        costFunction = {
            val selectedDice = it.getSelectedDice()
            val count = selectedDice.size + selectedDice.filter { dice -> dice.stored }.size
            count == 10 && selectedDice.map { dice -> dice.value % 2 }.toSet().size == 1
        },
    ),
    Project(
        "Te Kore Project",
        costDescription = "Two sets of four of a kind dice.",
        shortCostDescription = "Two sets of\nfour of a kind",
        costFunction = { Dice.isSetsOfAKind(2, 4)(it.getSelectedDice()) },
    ),
    Project(
        "Vesta Project",
        costDescription = "Two dice triples. Can only be purchased if a building has been built this turn.",
        shortCostDescription = "Two triples\nafter building",
        costFunction = {
            Dice.isSetsOfAKind(2, 3)(it.getSelectedDice()) && it.blueprintBuilt
        },
    ),
    Project(
        "Vishnu Project",
        costDescription = "Six dice of value 6.",
        shortCostDescription = "Six 6s",
        costFunction = { Dice.isSet(listOf(6, 6, 6, 6, 6, 6))(it.getSelectedDice()) },
    ),
)