package com.example.plyometrics.exporter

import com.example.plyometrics.model.SensorPoint

class CsvSessionSerializer : SessionSerializer {
    override fun serialize(session: List<SensorPoint>): String =
        buildString {
            appendLine("time, x, y, z")
            for (point in session) {
                appendLine(
                    "${point.time}," +
                            "${point.acceleration.x}," +
                            "${point.acceleration.y}," +
                            "${point.acceleration.z}"
                )
            }
        }

    override fun deserialize(data: String): List<SensorPoint> {
        TODO("Not yet implemented")
    }
}