package com.example.plyometrics.exporter

import com.example.plyometrics.model.SensorPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonSessionSerializer: SessionSerializer {
    override fun serialize(session: List<SensorPoint>): String = Json.encodeToString(session)

    override fun deserialize(data: String): List<SensorPoint> = Json.decodeFromString(data)
}