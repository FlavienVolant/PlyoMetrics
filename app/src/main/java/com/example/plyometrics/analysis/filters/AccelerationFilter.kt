package com.example.plyometrics.analysis.filters

import com.example.plyometrics.analysis.VerticalAccelerationPoint

interface AccelerationFilter {

    /**
     * Filters a sequence of vertical acceleration points.
     *
     * The timestamps are preserved.
     */
    fun filter(
        points: List<VerticalAccelerationPoint>
    ): List<VerticalAccelerationPoint>
}