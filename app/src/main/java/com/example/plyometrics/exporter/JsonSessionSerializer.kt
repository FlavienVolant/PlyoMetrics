package com.example.plyometrics.exporter

import com.example.plyometrics.model.RawSensorPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonSessionSerializer: SessionSerializer {
    override fun serialize(session: List<RawSensorPoint>): String = Json.encodeToString(session)

    override fun deserialize(data: String): List<RawSensorPoint> = Json.decodeFromString(data)
}