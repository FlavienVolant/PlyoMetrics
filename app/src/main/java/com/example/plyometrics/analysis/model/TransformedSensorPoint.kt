package com.example.plyometrics.analysis.model

import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.AccelerationPoint

data class TransformedSensorPoint(
    override val timestamp: Long,
    override val acceleration: Acceleration
): AccelerationPoint