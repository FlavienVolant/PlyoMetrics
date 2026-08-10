package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.exporter.CsvSessionSerializer
import com.example.plyometrics.model.AccelerometerManager
import com.example.plyometrics.model.SensorPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val jumpDetector = JumpDetector()
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _jumpResult = MutableStateFlow<JumpResult?>(null)
    val jumpResult = _jumpResult.asStateFlow()

    private var finishedSession: List<SensorPoint> = listOf()

    private val manager = AccelerometerManager(application) { finishedSession ->
        _isRunning.value = false
        this.finishedSession = finishedSession
        _jumpResult.value = jumpDetector.analyze(finishedSession)
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