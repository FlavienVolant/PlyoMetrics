package com.example.plyometrics.analysis

import com.example.plyometrics.model.RawJump

class JumpDetector (
    private val impulseThreshold: Float = 13f,
    private val takeOffThreshold: Float = 5f,
    private val landingThreshold: Float = 13f,
    private val peakConfirmationPoints: Int = 10
)

{
    private val transformer = SensorFrameTransformer()

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

        return analyzeVerticalAccelerationPoint(transformer.toWorldFrame(rawJump.points))
    }

    fun analyzeVerticalAccelerationPoint(points: List<VerticalAccelerationPoint>): JumpResult? {
        val impulse = findImpulse(points) ?: return null
        val takeOff = findTakeOff(points, impulse) ?: return null
        val landing = findLanding(points, takeOff) ?: return null

        return JumpResult(takeOff.timestamp, landing.timestamp)
    }

    /**
     * Find the first local maximum above IMPULSE_THRESHOLD
     */
    fun findImpulse(points: List<VerticalAccelerationPoint>): VerticalAccelerationPoint? {
        for(i in 0 .. points.size - peakConfirmationPoints) {
            val current = points[i]

            if(current.value < impulseThreshold)
                continue

            // confirm that the signal decreases after the peak
            var isPeak = true
            for(j in 1..peakConfirmationPoints) {
                if (points[i + j].value >= current.value){
                    isPeak = false
                    break
                }
            }

            if(!isPeak)
                continue

            return current
        }

        return null
    }

    fun findTakeOff(points: List<VerticalAccelerationPoint>, impulse: VerticalAccelerationPoint): VerticalAccelerationPoint? {
        val impulseIndex = points.indexOfFirst { it.timestamp == impulse.timestamp }

        if (impulseIndex < 0)
            return null

        return points
            .drop(impulseIndex + 1)
            .firstOrNull{ it.value < takeOffThreshold }
    }

    fun findLanding(points: List<VerticalAccelerationPoint>, takeOff: VerticalAccelerationPoint): VerticalAccelerationPoint? {
        val takeOffIndex = points.indexOfFirst { it.timestamp == takeOff.timestamp }

        if (takeOffIndex < 0)
            return null

        for(i in takeOffIndex + 1 until points.size - peakConfirmationPoints) {
            val current = points[i]

            if(current.value < landingThreshold)
                continue

            var isPeak = true

            for(j in 1..peakConfirmationPoints) {
                if (points[i + j].value >= current.value) {
                    isPeak = false
                    break
                }
            }

            if(!isPeak)
                continue

            /*
             * We found the landing peak.
             *
             * Now walk backwards through the increasing slope
             * to find where this rise started.
             */
            var j = i - 1

            while (points[j].value > takeOffThreshold)
                j --

            return points[j]
        }

        return null
    }
}