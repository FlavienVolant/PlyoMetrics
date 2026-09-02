package com.example.plyometrics.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MotionSensorManager(
    context: Context,
    private val onSessionFinished: (List<SensorPoint>) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /**
     * Use the standard rotation vector when available.
     * Fall back to the geomagnetic rotation vector otherwise.
     */
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)

    private val accelerationSamples = mutableListOf<AccelerationSample>()

    private val rotationSamples = mutableListOf<RotationSample>()

    private var startTimestamp = 0L

    fun start() {
        accelerationSamples.clear()
        rotationSamples.clear()
        startTimestamp = 0L

        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        sensorManager.registerListener(
            this,
            rotationSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        onSessionFinished(
            SampleMatcher.match(
                accelerationSamples,
                rotationSamples
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {

                accelerationSamples += AccelerationSample(
                    event.timestamp,
                    Acceleration(
                        event.values[0],
                        event.values[1],
                        event.values[2]
                    )
                )
            }

            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_ROTATION_VECTOR -> {
                rotationSamples += RotationSample(
                    event.timestamp,
                    event.values.clone()
                )
            }
        }
    }
}