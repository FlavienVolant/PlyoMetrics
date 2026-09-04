package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.exporter.CsvSessionSerializer
import com.example.plyometrics.model.MotionSensorManager
import com.example.plyometrics.model.RawJump
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val jumpDetector = JumpDetector()
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _jumpResult = MutableStateFlow<JumpResult?>(null)
    val jumpResult = _jumpResult.asStateFlow()

    private val _sessions = MutableStateFlow<List<RawJump>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private lateinit var finishedRawJump: RawJump

    private val manager = MotionSensorManager(application) { finishedSession ->
        _isRunning.value = false

        this.finishedRawJump = RawJump(points = finishedSession)

        _jumpResult.value = jumpDetector.analyze(finishedSession)

        _sessions.value += listOf(this.finishedRawJump)
    }

    fun start() {
        _jumpResult.value = null
        _isRunning.value = true
        manager.start()
    }

    fun stop() {
        manager.stop()
    }

    fun exportSession() = CsvSessionSerializer().serialize(finishedRawJump.points)
}