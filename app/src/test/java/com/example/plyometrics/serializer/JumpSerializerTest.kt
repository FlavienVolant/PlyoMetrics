package com.example.plyometrics.serializer

import com.example.plyometrics.model.RawJump
import com.example.plyometrics.model.RawSensorPoint
import com.example.plyometrics.model.measure.Acceleration
import com.example.plyometrics.model.measure.Rotation
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.Date

abstract class JumpSerializerTest {

    abstract val serializer: JumpSerializer

    @Test
    fun `deserialize serialize is identity`() = runTest {
        checkAll(rawJumpArb) { jump ->
            serializer.deserialize(serializer.serialize(jump)) shouldBe jump
        }
    }


    val rawJumpArb: Arb<RawJump> = arbitrary {
        RawJump(
            id = Arb.string(1..50).bind(),
            date = Date(
                Arb.long(0L..2_000_000_000_000L).bind()
            ),
            points = Arb.list(
                rawSensorPointArb,
                0..100
            ).bind()
        )
    }
    val rawSensorPointArb: Arb<RawSensorPoint> = arbitrary {
        RawSensorPoint(
            timestamp = Arb.long(0L..10_000_000_000L).bind(),

            acceleration = Acceleration(
                x = Arb.float(-100f..100f).bind(),
                y = Arb.float(-100f..100f).bind(),
                z = Arb.float(-100f..100f).bind()
            ),

            rotation = Rotation(
                qx = Arb.float(-1f..1f).bind(),
                qy = Arb.float(-1f..1f).bind(),
                qz = Arb.float(-1f..1f).bind(),
                qw = Arb.float(-1f..1f).bind()
            )
        )
    }
}