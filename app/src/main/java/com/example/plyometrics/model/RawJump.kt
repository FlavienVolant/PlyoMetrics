package com.example.plyometrics.model

import java.util.Date

/**
 * This class is a raw jump data, this is the source of truth in the app
 */
data class RawJump(
    val date: Date = Date(),
    val points: List<RawSensorPoint>
)