package com.example.plyometrics.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccelerometerManager (
    context: Context,
    private val onSessionFinished: (List<SensorPoint>) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _acceleration = MutableStateFlow(Acceleration(0f, 0f, 0f))

    val acceleration = _acceleration.asStateFlow()

    private val session = mutableListOf<SensorPoint>()

    private var startTimestamp = 0L

    fun start() {
        session.clear()
        startTimestamp = 0L
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stop() {
        sensorManager.unregisterListener(this)

        for (point in session) {
            Log.d("SensorSession", "${point.time}, ${point.acceleration.magnitude}")
        }

        onSessionFinished(session.toList())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(startTimestamp == 0L)
            startTimestamp = event.timestamp

        val elapsed = (event.timestamp - startTimestamp) / 1_000_000

        val acceleration = Acceleration(
            event.values[0],
            event.values[1],
            event.values[2])

        _acceleration.value = acceleration

        session += SensorPoint(elapsed, acceleration)
    }
}