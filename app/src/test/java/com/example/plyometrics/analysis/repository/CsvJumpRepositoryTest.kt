package com.example.plyometrics.analysis.repository

import com.example.plyometrics.repository.CsvJumpRepository
import com.example.plyometrics.repository.JumpRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class CsvJumpRepositoryTest: JumpRepositoryTest() {

    @TempDir
    lateinit var temporaryFolder: Path

    override lateinit var repository: JumpRepository

    @BeforeEach
    fun setUp() {
        repository = CsvJumpRepository(
            temporaryFolder.resolve("jumps").toFile()
        )
    }
}