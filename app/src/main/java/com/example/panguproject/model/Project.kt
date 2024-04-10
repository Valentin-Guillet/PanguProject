package com.example.panguproject.model

import com.example.panguproject.GameViewModel


typealias ProjectId = Int

class Project(
    val id: ProjectId,
    name: String,
    costDescription: String? = null,
    shortCostDescription: String? = null,
    val costFunction: (gameViewModel: GameViewModel) -> Boolean,
) : DetailCard(
    name,
    costDescription = costDescription,
    shortCostDescription = shortCostDescription,
)

data class ProjectStatus(
    val id: ProjectId,
    val built: Boolean = false
)