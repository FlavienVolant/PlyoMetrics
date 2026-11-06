package com.example.plyometrics.analysis.model

import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Position
import com.example.plyometrics.model.Velocity

data class MotionPoint(
    val timestamp: Long,
    val acceleration: Acceleration,
    val velocity: Velocity,
    val position: Position
)