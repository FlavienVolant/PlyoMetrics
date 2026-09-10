package com.example.plyometrics.analysis

import com.example.plyometrics.model.RawJump

data class AnalyzedJump (
    val rawJump: RawJump,
    val result: JumpResult?
)
