package com.example.plyometrics.model

import kotlinx.serialization.Serializable

/**
 * m
 */
@Serializable
data class Position(
    val x: Float,
    val y: Float,
    val z: Float
)
