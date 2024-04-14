package com.vguillet.panguproject.model

import com.vguillet.panguproject.GameViewModel
import kotlinx.serialization.Serializable


typealias BlueprintId = Int

class Blueprint(
    val id: BlueprintId,
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
    val isDefault: Boolean = false,
) : DetailCard(
    name,
    costDescription,
    shortCostDescription,
    effectDescription,
    shortEffectDescription,
)

@Serializable
data class BlueprintStatus(
    val id: BlueprintId,
    val usable: Boolean = false,
)