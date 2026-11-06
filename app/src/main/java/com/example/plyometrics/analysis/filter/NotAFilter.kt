package com.example.plyometrics.analysis.filter

import com.example.plyometrics.analysis.model.TransformedSensorPoint

class NotAFilter: AccelerationFilter {
    override fun filter(points: List<TransformedSensorPoint>): List<TransformedSensorPoint> {
        return points
    }
}