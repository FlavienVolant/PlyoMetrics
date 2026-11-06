package com.example.plyometrics.analysis.transform

import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.measurement.RawSensorPoint
import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Rotation

class WorldFrameTransformer: SensorDataTransformer {
    override fun transform(data: List<RawSensorPoint>): List<TransformedSensorPoint> {
        return data.map { point ->
            TransformedSensorPoint(
                timestamp = point.timestamp,
                acceleration = transformAcceleration(
                    point.acceleration,
                    point.rotation
                )
            )
        }
    }

    private fun transformAcceleration(acceleration: Acceleration, rotation: Rotation): Acceleration {

        val qx = rotation.qx
        val qy = rotation.qy
        val qz = rotation.qz
        val qw = rotation.qw

        // Quaternion to rotation matrix
        val r00 = 1f - 2f * (qy * qy + qz * qz)
        val r01 = 2f * (qx * qy - qz * qw)
        val r02 = 2f * (qx * qz + qy * qw)

        val r10 = 2f * (qx * qy + qz * qw)
        val r11 = 1f - 2f * (qx * qx + qz * qz)
        val r12 = 2f * (qy * qz - qx * qw)

        val r20 = 2f * (qx * qz - qy * qw)
        val r21 = 2f * (qy * qz + qx * qw)
        val r22 = 1f - 2f * (qx * qx + qy * qy)

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