package com.example.plyometrics.analysis

import com.example.plyometrics.measurement.RawJump

interface JumpAnalysisStrategy {
    fun analyze(rawJump: RawJump): AnalyzedJump
}