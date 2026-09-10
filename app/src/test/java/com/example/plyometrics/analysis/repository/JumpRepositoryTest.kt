package com.example.plyometrics.analysis.repository

import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import com.example.plyometrics.repository.JumpRepository
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.Date

abstract class JumpRepositoryTest {

    abstract var repository: JumpRepository

    private fun createRawJump(id: String, date: Date = Date()): RawJump {
        val points = listOf(
            RawSensorPoint(
                10L,
                Acceleration(0f, 0f, 0f),
                Rotation(0f, 0f, 0f, 0f)
            ),
            RawSensorPoint(
                20L,
                Acceleration(10f, 10f, 10f),
                Rotation(0f, 0f, 1f, 0f)
            )
        )

        return RawJump(id, date, points)
    }

    @Test
    fun `save and get`() = runTest {
        val jump = createRawJump("jump-01")

        repository.save(jump)

        repository.get("jump-01") shouldBe jump
    }

    @Test
    fun `get unknown jump returns null`() = runTest {
        repository.get("unknown") shouldBe null
    }

    @Test
    fun `save and getAll`() = runTest {
        val jump = createRawJump("jump-1")

        repository.save(jump)

        repository.getAll() shouldContain jump
    }

    @Test
    fun `delete `() = runTest {
        val jump = createRawJump("jump-1")

        repository.save(jump)
        repository.delete("jump-1")

        repository.get("jump-1") shouldBe null
    }

    @Test
    fun `getAll returns jumps ordered by date`() = runTest {

        val older = createRawJump(id = "older", date = Date(1_000))
        val newer = createRawJump(id = "newer", date = Date(2_000))

        repository.save(newer)
        repository.save(older)

        repository.getAll() shouldBe listOf(older, newer)
    }
}