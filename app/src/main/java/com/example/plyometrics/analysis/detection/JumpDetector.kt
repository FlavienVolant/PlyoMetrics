package com.example.plyometrics.analysis.detection

import com.example.plyometrics.analysis.AnalyzedJump
import com.example.plyometrics.analysis.JumpAnalysisStrategy
import com.example.plyometrics.analysis.JumpResult
import com.example.plyometrics.analysis.filter.AccelerationFilter
import com.example.plyometrics.analysis.filter.SavitzkyGolayFilter
import com.example.plyometrics.analysis.model.TransformedSensorPoint
import com.example.plyometrics.analysis.transform.SensorDataTransformer
import com.example.plyometrics.analysis.transform.WorldFrameTransformer
import com.example.plyometrics.analysis.visualization.DetectionChartPoint
import com.example.plyometrics.analysis.visualization.JumpChartData
import com.example.plyometrics.measurement.RawJump

class JumpDetector (
    private val transformer: SensorDataTransformer = WorldFrameTransformer(),
    private val filter: AccelerationFilter = SavitzkyGolayFilter(),

    private val impulseThreshold: Float = 13f,
    private val flightThreshold: Float = 5f,
    private val landingThreshold: Float = 13f,

    private val confirmationDurationMs: Long = 50L,
    private val minimumFlightTimeMs: Long = 150L
): JumpAnalysisStrategy

{

    override fun analyze(rawJump: RawJump): AnalyzedJump {

        if (rawJump.points.isEmpty())
            return AnalyzedJump(rawJump, null, null)

        val transformedPoints = transformer.transform(rawJump.points)
        val filteredPoints = filter.filter(transformedPoints)

        return AnalyzedJump(
            rawJump,
            analyze(filteredPoints),
            JumpChartData.Detection(
                filteredPoints.map {
                    DetectionChartPoint(
                        it.timestamp,
                        it.acceleration.z
                    )
                }
            )
        )
    }

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
    fun analyze(points: List<TransformedSensorPoint>): JumpResult? {

        if (points.isEmpty())
            return null

        val impulseIndex = findImpulse(points) ?: return null
        val takeOffIndex = findTakeOff(points, impulseIndex) ?: return null
        val landingIndex = findLanding(points, takeOffIndex) ?: return null

        val flightTime = points[landingIndex].timestamp - points[takeOffIndex].timestamp

        if(flightTime < minimumFlightTimeMs * 1_000_000)
            return null

        val flightTimeSeconds = flightTime / 1_000_000_000f

        val height = 9.81f * flightTimeSeconds * flightTimeSeconds / 8f

        return JumpResult(
            takeOffTime = points[takeOffIndex].timestamp,
            landingTime = points[landingIndex].timestamp,
            height = height
        )
    }

    private fun verticalAcceleration(point: TransformedSensorPoint): Float = point.acceleration.z

    /**
     * Find the first local maximum above IMPULSE_THRESHOLD
     */
    fun findImpulse(points: List<TransformedSensorPoint>): Int? {

        for (i in points.indices) {

            val current = points[i]
            val currentValue = verticalAcceleration(current)

            if (currentValue < impulseThreshold)
                continue

            val confirmationEnd = current.timestamp + confirmationDurationMs * 1_000_000

            var isPeak = true

            for (j in i + 1 until points.size) {

                if (points[j].timestamp > confirmationEnd)
                    break

                if (verticalAcceleration(points[j]) >= currentValue) {
                    isPeak = false
                    break
                }
            }

            if (isPeak)
                return i
        }

        return null
    }

    fun findTakeOff(points: List<TransformedSensorPoint>, impulseIndex: Int): Int? {

        for (i in impulseIndex + 1 until points.size) {

            if (verticalAcceleration(points[i]) >= flightThreshold)
                continue

            val confirmationEnd = points[i].timestamp + confirmationDurationMs * 1_000_000

            var confirmed = true

            for (j in i + 1 until points.size) {

                if (points[j].timestamp > confirmationEnd)
                    break

                if ( verticalAcceleration(points[j]) > flightThreshold + 2f) {
                    confirmed = false
                    break
                }
            }

            if (confirmed)
                return i
        }

        return null
    }

    fun findLanding(points: List<TransformedSensorPoint>, takeOffIndex: Int): Int? {

        for (i in takeOffIndex + 1 until points.size) {

            val current = points[i]
            val currentValue = verticalAcceleration(current)

            // Ignore unrealistic short flight phases
            val flightTime = current.timestamp - points[takeOffIndex].timestamp

            if (flightTime < minimumFlightTimeMs * 1_000_000L)
                continue

            if (currentValue < landingThreshold)
                continue

            if (!isPeak(points, i))
                continue

            return findLandingStart(points, i)
        }

        return null
    }

    fun isPeak(points: List<TransformedSensorPoint>, index: Int): Boolean {

        val peak = points[index]
        val peakValue = verticalAcceleration(peak)

        val endTime = peak.timestamp + confirmationDurationMs * 1_000_000

        for (i in index + 1 until points.size) {

            val current = points[i]
            val currentValue = verticalAcceleration(current)

            if (current.timestamp > endTime)
                break

            if (currentValue >= peakValue)
                return false
        }

        return true
    }

    fun findLandingStart(points: List<TransformedSensorPoint>, peakIndex: Int): Int {

        var i = peakIndex - 1

        while (i > 0) {

            val current = points[i]
            val currentValue = verticalAcceleration(current)
            val previous = points[i - 1]
            val previousValue = verticalAcceleration(previous)

            val dt = (current.timestamp - previous.timestamp) / 1_000_000_000f

            if (dt <= 0f) {
                i--
                continue
            }

            val slope = (currentValue - previousValue) / dt

            if (currentValue < 5f && slope > 50f)
                return i

            i--
        }

        return peakIndex
    }
}