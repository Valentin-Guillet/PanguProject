package com.vguillet.panguproject.model

import com.vguillet.panguproject.GameViewModel
import kotlinx.serialization.Serializable


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

@Serializable
data class ProjectStatus(
    val id: ProjectId,
    val built: Boolean = false
)