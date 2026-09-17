package com.example.plyometrics.analysis

import com.example.plyometrics.analysis.filters.MovingAverageFilter
import com.example.plyometrics.analysis.filters.SavitzkyGolayFilter
import com.example.plyometrics.model.RawJump

class JumpDetector (
    private val impulseThreshold: Float = 13f,
    private val flightThreshold: Float = 5f,
    private val landingThreshold: Float = 13f,

    private val confirmationDurationMs: Long = 50L,
    private val minimumFlightTimeMs: Long = 150L
)

{
    private val transformer = SensorFrameTransformer()
    private val accelerationFilter = SavitzkyGolayFilter(
        windowSize = 10,
        polynomialOrder = 2
    )

    /**
     * Return the jump of a sensor session
     *
     * A jump if detected by finding:
     * - the impulse
     * - the take-off
     * - the landing
     *
     * Returns a [JumpResult] if all events are found, null otherwise
     */
    fun analyze(rawJump: RawJump): JumpResult? {

        if (rawJump.points.isEmpty())
            return null

        val worldPoints = transformer.toWorldFrame(rawJump.points)

        val filteredPoints = accelerationFilter.filter(worldPoints)

        return analyzeVerticalAccelerationPoint(filteredPoints)
    }

    fun analyzeVerticalAccelerationPoint(points: List<VerticalAccelerationPoint>): JumpResult? {
        val impulseIndex = findImpulse(points) ?: return null
        val takeOffIndex = findTakeOff(points, impulseIndex) ?: return null
        val landingIndex = findLanding(points, takeOffIndex) ?: return null

        val flightTime = points[landingIndex].timestamp - points[takeOffIndex].timestamp

        if (flightTime < minimumFlightTimeMs * 1_000_000)
            return null

        return JumpResult(
            points[takeOffIndex].timestamp,
            points[landingIndex].timestamp
        )
    }

    /**
     * Find the first local maximum above IMPULSE_THRESHOLD
     */
    fun findImpulse(points: List<VerticalAccelerationPoint>): Int? {

        for (i in points.indices) {

            val current = points[i]

            if (current.value < impulseThreshold)
                continue

            val confirmationEnd = current.timestamp + confirmationDurationMs * 1_000_000

            var isPeak = true

            for (j in i + 1 until points.size) {

                if (points[j].timestamp > confirmationEnd)
                    break

                if (points[j].value >= current.value) {
                    isPeak = false
                    break
                }
            }

            if (isPeak)
                return i
        }

        return null
    }

    fun findTakeOff(points: List<VerticalAccelerationPoint>, impulseIndex: Int): Int? {

        for (i in impulseIndex + 1 until points.size) {

            if (points[i].value >= flightThreshold)
                continue

            val confirmationEnd = points[i].timestamp + confirmationDurationMs * 1_000_000

            var confirmed = true

            for (j in i + 1 until points.size) {

                if (points[j].timestamp > confirmationEnd)
                    break

                if (points[j].value > flightThreshold + 2f) {
                    confirmed = false
                    break
                }
            }

            if (confirmed)
                return i
        }

        return null
    }

    fun findLanding(points: List<VerticalAccelerationPoint>, takeOffIndex: Int): Int? {

        for (i in takeOffIndex + 1 until points.size) {

            // Ignore unrealistic short flight phases
            val flightTime = points[i].timestamp - points[takeOffIndex].timestamp

            if (flightTime < 150_000_000)
                continue

            if (points[i].value < landingThreshold)
                continue

            if (!isPeak(points, i))
                continue

            return findLandingStart(points, i)
        }

        return null
    }

    fun isPeak(points: List<VerticalAccelerationPoint>, index: Int): Boolean {

        val peak = points[index]

        val endTime = peak.timestamp + confirmationDurationMs * 1_000_000

        for (i in index + 1 until points.size) {

            if (points[i].timestamp > endTime)
                break

            if (points[i].value >= peak.value)
                return false
        }

        return true
    }

    fun findLandingStart(points: List<VerticalAccelerationPoint>, peakIndex: Int): Int {

        var i = peakIndex - 1

        while (i > 0) {

            val current = points[i]
            val previous = points[i - 1]

            val dt = (current.timestamp - previous.timestamp) / 1_000_000_000f

            if (dt <= 0f) {
                i--
                continue
            }

            val slope = (current.value - previous.value) / dt

            if (current.value < 5f && slope > 50f)
                return i

            i--
        }

        return peakIndex
    }
}