package com.example.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

object QrCodeGenerator {

    /**
     * Generates a deterministic high-density 2D matrix for any ID string
     */
    fun generateMatrix(data: String, size: Int = 21): Array<BooleanArray> {
        val matrix = Array(size) { BooleanArray(size) { false } }

        // Standard QR Finder Patterns (Top-Left, Top-Right, Bottom-Left 7x7)
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, size - 7, 0)
        drawFinderPattern(matrix, 0, size - 7)

        // Timing patterns
        for (i in 8 until size - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // Data encoding deterministic pseudo-bits based on String hash
        val bytes = data.toByteArray()
        var bitIndex = 0
        val totalBits = bytes.size * 8

        for (r in 0 until size) {
            for (c in 0 until size) {
                // Skip finder patterns & timing
                if ((r < 8 && c < 8) || (r < 8 && c >= size - 8) || (r >= size - 8 && c < 8)) continue
                if (r == 6 || c == 6) continue

                val hash = abs((data.hashCode() * 31 + r * 17 + c * 23 + (bytes.getOrNull(bitIndex % bytes.size)?.toInt() ?: 0)))
                matrix[r][c] = (hash % 3 == 0 || hash % 5 == 0)
                bitIndex++
            }
        }

        return matrix
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startR: Int, startC: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isOuter = (r == 0 || r == 6 || c == 0 || c == 6)
                val isInner = (r in 2..4 && c in 2..4)
                matrix[startR + r][startC + c] = (isOuter || isInner)
            }
        }
    }
}

@Composable
fun StudentQrCard(
    qrCodeId: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    pixelColor: Color = Color(0xFF0A2463)
) {
    val matrix = QrCodeGenerator.generateMatrix(qrCodeId, 21)

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val count = matrix.size
            val cellSize = size.width / count

            for (r in 0 until count) {
                for (c in 0 until count) {
                    if (matrix[r][c]) {
                        drawRect(
                            color = pixelColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 1.02f, cellSize * 1.02f)
                        )
                    }
                }
            }
        }
    }
}
