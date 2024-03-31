package com.example.panguproject

class Project(
    name: String,
    costDescription: String? = null,
    shortCostDescription: String? = null,
    val costFunction: ((diceList: List<Dice>) -> Boolean)? = null,
    var built: Boolean = false,
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
        costFunction = Dice::isPair,
    ),
    Project(
        "Dazbog Project",
        costDescription = "Seven dice of a kind.",
        shortCostDescription = "Seven of a kind",
        costFunction = Dice::isPair,
    ),
    Project(
        "Freya Project",
        costDescription = "Four pairs of dice in a row.\n\tExample: 2, 2, 3, 3, 4, 4, 5, 5",
        shortCostDescription = "Four pairs\nin a row",
        costFunction = Dice::isPair,
    ),
    Project(
        "Herus Project",
        costDescription = "Six in a row (so 1, 2, 3, 4, 5, 6)\n\tThis project can't be purchased if a reroll has been used this turn.",
        shortCostDescription = "Six in a row\n(no reroll)",
        costFunction = Dice::isPair,
    ),
    Project(
        "Inari Project",
        costDescription = "Six dice of value 1.",
        shortCostDescription = "Six 1s",
        costFunction = Dice::isPair,
    ),
    Project(
        "Pangu Project",
        costDescription = "A set of dice that amounts to exactly 40.",
        shortCostDescription = "Sum = 40",
        costFunction = Dice::isPair,
    ),
    Project(
        "Quetzalcoatl Project",
        costDescription = "Ten even dice OR ten odd dice. Each stored die count as two.",
        shortCostDescription = "10 even OR odd\n(stored count x2)",
        costFunction = Dice::isPair,
    ),
    Project(
        "Te Kore Project",
        costDescription = "Two sets of four of a kind dice.",
        shortCostDescription = "Two sets of\nfour of a kind",
        costFunction = Dice::isPair,
    ),
    Project(
        "Vesta Project",
        costDescription = "Two dice triples. Can only be purchased if a building has been built this turn.",
        shortCostDescription = "Two triples after\nbuilding a blueprint",
        costFunction = Dice::isPair,
    ),
    Project(
        "Vishnu Project",
        costDescription = "Six dice of value 6.",
        shortCostDescription = "Six 6s",
        costFunction = Dice::isPair,
    ),
)