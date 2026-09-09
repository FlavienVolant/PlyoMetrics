package com.example.plyometrics.model

import java.util.Date
import java.util.UUID

/**
 * This class is a raw jump data, this is the source of truth in the app
 */
data class RawJump(
    val id: String = UUID.randomUUID().toString(),
    val date: Date = Date(),
    val points: List<RawSensorPoint>
)