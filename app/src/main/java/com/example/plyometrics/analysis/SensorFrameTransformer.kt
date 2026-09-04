package com.example.plyometrics.analysis

import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation

class SensorFrameTransformer {
    fun toWorldFrame(rawPoint: RawSensorPoint): VerticalAccelerationPoint {
        return VerticalAccelerationPoint(
            timestamp = rawPoint.timestamp,
            value = transformAcceleration(rawPoint.acceleration, rawPoint.rotation).z // only take z
        )
    }

    fun toWorldFrame(points: List<RawSensorPoint>): List<VerticalAccelerationPoint> = points.map(::toWorldFrame)

    private fun transformAcceleration(
        acceleration: Acceleration,
        rotation: Rotation
    ): Acceleration {

        val qx = rotation.qx
        val qy = rotation.qy
        val qz = rotation.qz
        val qw = rotation.qw

        // Quaternion -> rotation matrix
        // Trust gpt on that
        val r00 = 1 - 2 * (qy * qy + qz * qz)
        val r01 = 2 * (qx * qy - qz * qw)
        val r02 = 2 * (qx * qz + qy * qw)

        val r10 = 2 * (qx * qy + qz * qw)
        val r11 = 1 - 2 * (qx * qx + qz * qz)
        val r12 = 2 * (qy * qz - qx * qw)

        val r20 = 2 * (qx * qz - qy * qw)
        val r21 = 2 * (qy * qz + qx * qw)
        val r22 = 1 - 2 * (qx * qx + qy * qy)

        val x = acceleration.x
        val y = acceleration.y
        val z = acceleration.z

        return Acceleration(
            x = r00 * x + r01 * y + r02 * z,
            y = r10 * x + r11 * y + r12 * z,
            z = r20 * x + r21 * y + r22 * z
        )
    }
}