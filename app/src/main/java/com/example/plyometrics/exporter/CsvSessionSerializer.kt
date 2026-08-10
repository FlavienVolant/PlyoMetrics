package com.example.plyometrics.exporter

import android.util.Log
import com.example.plyometrics.model.SensorPoint

class CsvSessionSerializer : SessionSerializer {
    override fun serialize(session: List<SensorPoint>): String =
        buildString {
            appendLine("timestamp, x, y, z, rx, ry, rz, rw")
            for (point in session) {
                Log.d("SensorSerializer", "rotationVector size: ${point.rotationVector.size}")
                appendLine(
                    "${point.timestamp}," +
                            "${point.acceleration.x}," +
                            "${point.acceleration.y}," +
                            "${point.acceleration.z}," +
                            "${point.rotationVector[0]}," +
                            "${point.rotationVector[1]}," +
                            "${point.rotationVector[2]}," +
                            "${point.rotationVector[3]}"
                )
            }
        }

    override fun deserialize(data: String): List<SensorPoint> {
        TODO("Not yet implemented")
    }
}