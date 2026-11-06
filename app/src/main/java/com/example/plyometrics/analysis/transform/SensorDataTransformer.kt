package com.example.plyometrics.analysis.transform

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.measurement.RawSensorPoint

interface SensorDataTransformer {
    fun transform(data: List<RawSensorPoint>): List<TransformedSensorPoint>
}