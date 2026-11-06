package com.example.plyometrics.analysis.simulation

import com.example.plyometrics.analysis.model.MotionPoint
import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.model.Position
import com.example.plyometrics.model.Velocity

class MotionSimulator {
    fun simulate(data: List<TransformedSensorPoint>): List<MotionPoint> {
        if (data.isEmpty()) return emptyList()

        var velocity = Velocity(0f, 0f, 0f)
        var position = Position(0f, 0f, 0f)

        val result = mutableListOf<MotionPoint>()

        result.add(
            MotionPoint(
                timestamp = data[0].timestamp,
                acceleration = data[0].acceleration,
                velocity = velocity,
                position = position
            )
        )

        for (i in 1 until data.size) {
            val previous = data[i - 1]
            val current = data[i]

            val dt = (current.timestamp - previous.timestamp) / 1_000_000_000f

            val newVelocity = Velocity(
                x = velocity.x + (previous.acceleration.x + current.acceleration.x) / 2f * dt,
                y = velocity.y + (previous.acceleration.y + current.acceleration.y) / 2f * dt,
                z = velocity.z + (previous.acceleration.z + current.acceleration.z) / 2f * dt
            )

            val newPosition = Position(
                x = position.x + (velocity.x + newVelocity.x) / 2f * dt,
                y = position.y + (velocity.y + newVelocity.y) / 2f * dt,
                z = position.z + (velocity.z + newVelocity.z) / 2f * dt
            )

            velocity = newVelocity
            position = newPosition

            result.add(
                MotionPoint(
                    timestamp = current.timestamp,
                    acceleration = current.acceleration,
                    velocity = velocity,
                    position = position
                )
            )
        }

        return result
    }
}