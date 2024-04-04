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
        costFunction = { true },
    ),
    Project(
        "Dazbog Project",
        costDescription = "Seven dice of a kind.",
        shortCostDescription = "Seven of a kind",
        costFunction = { true },
    ),
    Project(
        "Freya Project",
        costDescription = "Four pairs of dice in a row.\n\tExample: 2, 2, 3, 3, 4, 4, 5, 5",
        shortCostDescription = "Four pairs\nin a row",
        costFunction = { true },
    ),
    Project(
        "Herus Project",
        costDescription = "Six in a row (so 1, 2, 3, 4, 5, 6)\n\tThis project can't be purchased if a reroll has been used this turn.",
        shortCostDescription = "Six in a row\n(no reroll)",
        costFunction = { true },
    ),
    Project(
        "Inari Project",
        costDescription = "Six dice of value 1.",
        shortCostDescription = "Six 1s",
        costFunction = { true },
    ),
    Project(
        "Pangu Project",
        costDescription = "A set of dice that amounts to exactly 40.",
        shortCostDescription = "Sum = 40",
        costFunction = { true },
    ),
    Project(
        "Quetzalcoatl Project",
        costDescription = "Ten even dice OR ten odd dice. Each stored die count as two.",
        shortCostDescription = "10 even OR odd\n(stored count x2)",
        costFunction = { true },
    ),
    Project(
        "Te Kore Project",
        costDescription = "Two sets of four of a kind dice.",
        shortCostDescription = "Two sets of\nfour of a kind",
        costFunction = { true },
    ),
    Project(
        "Vesta Project",
        costDescription = "Two dice triples. Can only be purchased if a building has been built this turn.",
        shortCostDescription = "Two triples\nafter building",
        costFunction = { true },
    ),
    Project(
        "Vishnu Project",
        costDescription = "Six dice of value 6.",
        shortCostDescription = "Six 6s",
        costFunction = { true },
    ),
)