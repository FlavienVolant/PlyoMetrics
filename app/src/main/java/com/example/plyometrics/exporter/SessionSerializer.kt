package com.example.plyometrics.exporter

import com.example.plyometrics.model.RawSensorPoint

interface SessionSerializer {
    fun serialize(session: List<RawSensorPoint>): String
    fun deserialize(data: String): List<RawSensorPoint>
}