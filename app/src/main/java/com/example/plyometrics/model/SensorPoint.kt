package com.example.plyometrics.model

import kotlinx.serialization.Serializable

@Serializable
data class SensorPoint(
    val time: Long,
    val acceleration: Acceleration
)
