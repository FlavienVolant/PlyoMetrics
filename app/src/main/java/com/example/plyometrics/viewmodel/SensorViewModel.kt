package com.example.plyometrics.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpAnalysisMode
import com.example.plyometrics.analysis.JumpAnalysisStrategy
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.detection.JumpDetector
import com.example.plyometrics.analysis.filter.SavitzkyGolayFilter
import com.example.plyometrics.analysis.simulation.SimulationStrategy
import com.example.plyometrics.measurement.RawJump
import com.example.plyometrics.repository.CsvJumpRepository
import com.example.plyometrics.repository.JumpRepository
import com.example.plyometrics.sensor.MotionSensorManager
import com.example.plyometrics.serializer.CsvJumpSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private var analysisStrategy: JumpAnalysisStrategy = SimulationStrategy()

    private val _analysisMode = MutableStateFlow(JumpAnalysisMode.SIMULATION)
    val analysisMode = _analysisMode.asStateFlow()

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
        val analyzedJump = analysisStrategy.analyze(rawJump)

        _jumpResult.value = analyzedJump.result

        viewModelScope.launch {
            repository.save(rawJump)

            _jumps.update { jumps ->
                jumps + analyzedJump
            }
        }
    }

    init {
        viewModelScope.launch {
            _jumps.value = repository.getAll().map(analysisStrategy::analyze)
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

    fun exportSession(rawJump: RawJump): String {
        Log.d("JumpExport", "Number of points: ${rawJump.points.size}")
        return CsvJumpSerializer().serialize(rawJump)
    }

    fun deleteJump(rawJump: RawJump) {

        viewModelScope.launch {
            repository.delete(rawJump.id)
            _jumps.value = repository.getAll().map(analysisStrategy::analyze)
        }
    }

    fun selectAnalysisMode(mode: JumpAnalysisMode) {
        analysisStrategy = when (mode) {
            JumpAnalysisMode.DETECTION -> JumpDetector()
            JumpAnalysisMode.SIMULATION -> SimulationStrategy(filter = SavitzkyGolayFilter())
        }

        _analysisMode.value = mode

        viewModelScope.launch {
            _jumps.value = repository.getAll().map(analysisStrategy::analyze)
        }
    }
}