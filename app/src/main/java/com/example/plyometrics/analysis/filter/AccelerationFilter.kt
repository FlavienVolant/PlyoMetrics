package com.example.plyometrics.analysis.filter

import com.example.plyometrics.analysis.model.TransformedSensorPoint

interface AccelerationFilter {

    /**
     * Filters a sequence of TransformedSensorPoint.
     *
     * The timestamps are preserved.
     */
    fun filter(
        points: List<TransformedSensorPoint>
    ): List<TransformedSensorPoint>
}