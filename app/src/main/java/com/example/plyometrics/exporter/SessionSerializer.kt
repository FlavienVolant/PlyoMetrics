package com.example.plyometrics.exporter

import com.example.plyometrics.model.SensorPoint

interface SessionSerializer {
    fun serialize(session: List<SensorPoint>): String
    fun deserialize(data: String): List<SensorPoint>
}