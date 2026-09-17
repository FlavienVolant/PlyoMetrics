package com.example.plyometrics.analysis.filters

import com.example.plyometrics.analysis.VerticalAccelerationPoint
import kotlin.math.pow

class SavitzkyGolayFilter(
    private var windowSize: Int = 11,
    private var polynomialOrder: Int = 2
) : AccelerationFilter {

    private var coefficients: DoubleArray

    init {
        if(polynomialOrder < 0)
            polynomialOrder = 0

        if(windowSize <= polynomialOrder)
            windowSize = polynomialOrder + 1

        if(windowSize % 2 == 0)
            windowSize += 1

        coefficients = computeCoefficients()
    }

    override fun filter(points: List<VerticalAccelerationPoint>): List<VerticalAccelerationPoint> {
        if (points.size < windowSize)
            return points

        val halfWindow = windowSize / 2

        return points.mapIndexed { index, point ->

            if (index < halfWindow ||
                index >= points.size - halfWindow
            ) {
                point
            } else {

                var value = 0.0

                for (j in -halfWindow..halfWindow) {
                    value +=
                        coefficients[j + halfWindow] *
                                points[index + j].value
                }

                VerticalAccelerationPoint(
                    timestamp = point.timestamp,
                    value = value.toFloat()
                )
            }
        }
    }

    private fun computeCoefficients(): DoubleArray {
        val halfWindow = windowSize / 2

        // A[i][j] = x^j
        // where x is the position relative to the center of the window.
        val matrix = Array(windowSize) { row ->
            DoubleArray(polynomialOrder + 1) { column ->
                val x = (row - halfWindow).toDouble()
                x.pow(column)
            }
        }
        // A^T
        val transpose = transpose(matrix)

        // A^T * A
        val ata = multiply(transpose, matrix)

        // (A^T * A)^-1
        val inverse = invert(ata)

        // (A^T * A)^-1 * A^T
        val coefficientsMatrix = multiply(inverse, transpose)

        // We want the polynomial value at x = 0.
        // This corresponds to the first row.
        return coefficientsMatrix[0]
    }

    private fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {

        val rows = matrix.size
        val columns = matrix[0].size

        return Array(columns) { column ->
            DoubleArray(rows) { row ->
                matrix[row][column]
            }
        }
    }

    private fun multiply(left: Array<DoubleArray>, right: Array<DoubleArray>): Array<DoubleArray> {

        require(left[0].size == right.size)

        val rows = left.size
        val columns = right[0].size
        val common = right.size

        return Array(rows) { i ->
            DoubleArray(columns) { j ->

                var sum = 0.0

                for (k in 0 until common) {
                    sum += left[i][k] * right[k][j]
                }

                sum
            }
        }
    }


    private fun invert(matrix: Array<DoubleArray>): Array<DoubleArray> {

        val n = matrix.size

        val augmented = Array(n) { row ->
            DoubleArray(2 * n) { column ->

                when {
                    column < n ->
                        matrix[row][column]

                    column - n == row ->
                        1.0

                    else ->
                        0.0
                }
            }
        }

        for (column in 0 until n) {

            // Find the row with the largest pivot.
            var pivotRow = column

            for (row in column + 1 until n) {
                if (kotlin.math.abs(augmented[row][column]) >
                    kotlin.math.abs(augmented[pivotRow][column])) {
                    pivotRow = row
                }
            }

            require(
                kotlin.math.abs(augmented[pivotRow][column]) > 1e-12
            ) {
                "Matrix is singular"
            }

            // Swap rows if necessary.
            if (pivotRow != column) {
                val temp = augmented[column]
                augmented[column] = augmented[pivotRow]
                augmented[pivotRow] = temp
            }

            // Normalize the pivot row.
            val pivot = augmented[column][column]

            for (j in 0 until 2 * n) {
                augmented[column][j] /= pivot
            }

            // Eliminate this column from every other row.
            for (row in 0 until n) {

                if (row == column)
                    continue

                val factor = augmented[row][column]

                for (j in 0 until 2 * n) {
                    augmented[row][j] -=
                        factor * augmented[column][j]
                }
            }
        }

        // Extract the right half.
        return Array(n) { row ->
            DoubleArray(n) { column ->
                augmented[row][column + n]
            }
        }
    }
}