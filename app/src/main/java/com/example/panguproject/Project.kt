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
        costFunction = ::isPair
    ),
    Project(
        "Dazbog Project",
        costFunction = ::isPair
    ),
    Project(
        "Freya Project",
        costFunction = ::isPair
    ),
    Project(
        "Herus Project",
        costFunction = ::isPair
    ),
    Project(
        "Inari Project",
        costFunction = ::isPair
    ),
    Project(
        "Pangu Project",
        costFunction = ::isPair
    ),
    Project(
        "Quetzalcoatl Project",
        costFunction = ::isPair
    ),
    Project(
        "Te Kore Project",
        costFunction = ::isPair
    ),
    Project(
        "Vesta Project",
        costFunction = ::isPair
    ),
    Project(
        "Vishnu Project",
        costFunction = ::isPair
    ),
)