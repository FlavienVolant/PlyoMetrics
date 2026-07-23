package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.plyometrics.model.AccelerometerManager
import com.example.plyometrics.model.SensorPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorViewModel (application: Application) : AndroidViewModel(application){
    private val _session = MutableStateFlow<List<SensorPoint>>(emptyList())
    val session = _session.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val manager = AccelerometerManager(application) {
        finishedSession -> _session.value = finishedSession
        _isRunning.value = false
    }

    val acceleration = manager.acceleration

    fun start() {
        _session.value = emptyList()
        _isRunning.value = true
        manager.start()
    }

    fun stop() {
        manager.stop()
    }
}