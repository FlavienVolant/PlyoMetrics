package com.example.plyometrics.model

data class RotationSample(
    val timestamp: Long,
    val rotationVector: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RotationSample

        if (timestamp != other.timestamp) return false
        if (!rotationVector.contentEquals(other.rotationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + rotationVector.contentHashCode()
        return result
    }
}