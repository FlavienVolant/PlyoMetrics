package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.SensorFrameTransformer
import com.example.plyometrics.exporter.CsvSessionSerializer
import com.example.plyometrics.model.JumpPoint
import com.example.plyometrics.model.MotionSensorManager
import com.example.plyometrics.model.SensorPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val jumpDetector = JumpDetector()
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _jumpResult = MutableStateFlow<JumpResult?>(null)
    val jumpResult = _jumpResult.asStateFlow()

    private val _sessions =
        MutableStateFlow<List<List<JumpPoint>>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private var finishedSession: List<SensorPoint> = listOf()

    private val manager = MotionSensorManager(application) { finishedSession ->
        _isRunning.value = false
        this.finishedSession = finishedSession

        _jumpResult.value = jumpDetector.analyze(finishedSession)

        val worldPoints = SensorFrameTransformer().toWorldFrame(finishedSession)

        val jumpPoints = worldPoints.map {
            JumpPoint(
                it.timestamp,
                it.acceleration.z
            )
        }

        _sessions.value += listOf(jumpPoints)
    }

    fun start() {
        _jumpResult.value = null
        _isRunning.value = true
        manager.start()
    }

    fun stop() {
        manager.stop()
    }

    fun exportSession() = CsvSessionSerializer().serialize(finishedSession)
}