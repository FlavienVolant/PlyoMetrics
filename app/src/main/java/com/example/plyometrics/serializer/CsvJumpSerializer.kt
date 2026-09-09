package com.example.plyometrics.serializer

import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import java.util.Date

class CsvJumpSerializer : JumpSerializer {
    override fun serialize(jump: RawJump): String =
        buildString {
            appendLine("date=" + jump.date.time)
            appendLine("id=" + jump.id)
            appendLine("timestamp,x,y,z,rx,ry,rz,rw")
            for (point in jump.points) {
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

    override fun deserialize(data: String): RawJump {
        val lines = data.lines()

        val date = Date(lines[0].removePrefix("date=").toLong())

        val id = lines[1].removePrefix("id=")

        val points = lines
            .drop(3)
            .filter { it.isNotBlank() }
            .map {line ->
                val values = line.split(",")

                RawSensorPoint(
                    values[0].toLong(),
                    Acceleration(
                        values[1].toFloat(),
                        values[2].toFloat(),
                        values[3].toFloat()
                    ),
                    Rotation(
                        values[4].toFloat(),
                        values[5].toFloat(),
                        values[6].toFloat(),
                        values[7].toFloat()
                    )
                )
            }

        return RawJump(id, date, points)
    }
}