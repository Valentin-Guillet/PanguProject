package com.vguillet.panguproject.model

abstract class DetailCard(
    val name: String,
    val costDescription: String? = null,
    val shortCostDescription: String? = null,
    val effectDescription: String? = null,
    val shortEffectDescription: String? = null,
)