package com.example.plyometrics.analysis.filters

import com.example.plyometrics.analysis.VerticalAccelerationPoint

class MovingAverageFilter(private var windowSize: Int = 5): AccelerationFilter {

    init {
        if (windowSize < 0)
            windowSize = 1
        if(windowSize % 2 == 0)
            windowSize += 1
    }

    override fun filter(points: List<VerticalAccelerationPoint>): List<VerticalAccelerationPoint> {
        if (points.size < windowSize)
            return points

        val halfWindow = windowSize / 2

        return points.mapIndexed { index, point ->

            val start = maxOf(0, index - halfWindow)
            val end = minOf(points.size - 1, index + halfWindow)

            val average = points
                .subList(start, end + 1)
                .map { it.value }
                .average()
                .toFloat()

            VerticalAccelerationPoint(
                timestamp = point.timestamp,
                value = average
            )
        }
    }
}