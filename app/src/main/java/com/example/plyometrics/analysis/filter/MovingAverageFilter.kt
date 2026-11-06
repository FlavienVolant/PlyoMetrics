package com.example.plyometrics.analysis.filter

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.model.Acceleration

class MovingAverageFilter(private var windowSize: Int = 5): AccelerationFilter {

    init {
        if (windowSize < 1)
            windowSize = 1
        if(windowSize % 2 == 0)
            windowSize += 1
    }

    override fun filter(points: List<TransformedSensorPoint>): List<TransformedSensorPoint> {
        if (points.size < windowSize)
            return points

        val halfWindow = windowSize / 2

        return points.mapIndexed { index, point ->

            val start = maxOf(0, index - halfWindow)
            val end = minOf(points.size - 1, index + halfWindow)

            val averageX = points
                .subList(start, end + 1)
                .map { it.acceleration.x }
                .average()
                .toFloat()

            val averageY = points
                .subList(start, end + 1)
                .map { it.acceleration.y }
                .average()
                .toFloat()

            val averageZ = points
                .subList(start, end + 1)
                .map { it.acceleration.z }
                .average()
                .toFloat()

            TransformedSensorPoint(
                timestamp = point.timestamp,
                acceleration = Acceleration(averageX, averageY, averageZ)
            )
        }
    }
}