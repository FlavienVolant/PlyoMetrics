package com.example.plyometrics.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpDetector
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.model.MotionSensorManager
import com.example.plyometrics.model.RawJump
import com.example.plyometrics.repository.CsvJumpRepository
import com.example.plyometrics.repository.JumpRepository
import com.example.plyometrics.serializer.CsvJumpSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val jumpDetector = JumpDetector()

    private val repository: JumpRepository = CsvJumpRepository(application.filesDir)

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _jumpResult = MutableStateFlow<JumpResult?>(null)
    val jumpResult = _jumpResult.asStateFlow()

    private val _jumps = MutableStateFlow<List<AnalyzedJump>>(emptyList())
    val jumps = _jumps.asStateFlow()

    private val _selectedJump = MutableStateFlow<AnalyzedJump?>(null)
    val selectedJump = _selectedJump.asStateFlow()

    private val manager = MotionSensorManager(application) { finishedSession ->
        _isRunning.value = false

        val rawJump = RawJump(points = finishedSession)

        _jumpResult.value = jumpDetector.analyze(rawJump)

        viewModelScope.launch {

            repository.save(rawJump)

            _jumps.update { jumps ->
                jumps + toAnalyzedJump(rawJump)
            }
        }
    }

    init {
        viewModelScope.launch {
            _jumps.value = repository.getAll().map(::toAnalyzedJump)
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

    fun selectedJump(analyzedJump: AnalyzedJump) {
        _selectedJump.value = analyzedJump
    }

    @Deprecated("For testing purpose only")
    fun exportSession(rawJump: RawJump) = CsvJumpSerializer().serialize(rawJump)

    private fun toAnalyzedJump(rawJump: RawJump): AnalyzedJump {
        return AnalyzedJump(rawJump, jumpDetector.analyze(rawJump))
    }
}