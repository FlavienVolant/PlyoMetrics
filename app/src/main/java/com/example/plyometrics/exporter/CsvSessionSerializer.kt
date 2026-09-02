package com.example.plyometrics.exporter

import com.example.plyometrics.model.SensorPoint

class CsvSessionSerializer : SessionSerializer {
    override fun serialize(session: List<SensorPoint>): String =
        buildString {
            appendLine("timestamp, x, y, z, rx, ry, rz, rw")
            for (point in session) {
                appendLine(
                    "${point.timestamp}," +
                            "${point.acceleration.x}," +
                            "${point.acceleration.y}," +
                            "${point.acceleration.z}," +
                            "${point.rotation.qx}," +
                            "${point.rotation.qy}," +
                            "${point.rotation.qz}," +
                            "${point.rotation.qw}"
                )
            }
        }

    override fun deserialize(data: String): List<SensorPoint> {
        TODO("Not yet implemented")
    }
}