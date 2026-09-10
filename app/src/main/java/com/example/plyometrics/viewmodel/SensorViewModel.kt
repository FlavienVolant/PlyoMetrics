package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.serializer.CsvJumpSerializer
import com.example.plyometrics.model.MotionSensorManager
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.repository.CsvJumpRepository
import com.example.plyometrics.repository.JumpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val jumpDetector = JumpDetector()

    private val repository: JumpRepository = CsvJumpRepository(application.filesDir)

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _jumpResult = MutableStateFlow<JumpResult?>(null)
    val jumpResult = _jumpResult.asStateFlow()

    private val _sessions = MutableStateFlow<List<RawJump>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _selectedJump = MutableStateFlow<RawJump?>(null)
    val selectedJump = _selectedJump.asStateFlow()

    private val manager = MotionSensorManager(application) { finishedSession ->
        _isRunning.value = false

        _jumpResult.value = jumpDetector.analyze(finishedSession)

        viewModelScope.launch {
            repository.save(RawJump(points = finishedSession))
            _sessions.value = repository.getAll()
        }
    }

    init {
        viewModelScope.launch {
            _sessions.value = repository.getAll()
        }
    }

    fun start() {
        _jumpResult.value = null
        _isRunning.value = true
        manager.start()
    }

    fun stop() {
        manager.stop()
    }

    fun selectedJump(rawJump: RawJump) {
        _selectedJump.value = rawJump
    }

    @Deprecated("For testing purpose only")
    fun exportSession(rawJump: RawJump) = CsvJumpSerializer().serialize(rawJump)
}