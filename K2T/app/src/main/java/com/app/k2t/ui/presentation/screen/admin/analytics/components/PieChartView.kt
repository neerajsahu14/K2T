package com.app.k2t.ui.presentation.screen.admin.analytics.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.app.k2t.analytics.CategoryPerformance
import com.app.k2t.analytics.FoodPerformance
import com.app.k2t.analytics.OrderStatusDistribution
import java.text.NumberFormat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Custom Android View for pie charts
 */
class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Chart data
    var segments: List<PieSegment> = emptyList()
        set(value) {
            field = value
            calculateTotalValue()
            invalidate() // Redraw when data changes
        }

    // Chart styling
    var textColor: Int = android.graphics.Color.BLACK
    var labelTextSize: Float = 36f
    var valueTextSize: Float = 32f
    private var totalValue: Float = 0f
    private var radius: Float = 0f

    // Legend settings
    var showLegend: Boolean = true
    var legendPosition: LegendPosition = LegendPosition.BOTTOM

    // Cached objects for drawing efficiency
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        color = textColor
    }

    // Animation
    private var animProgress = 0f
    private val maxAnimProgress = 1f
    private val animDuration = 1000L // 1 second
    private val startTime = System.currentTimeMillis()

    private fun calculateTotalValue() {
        totalValue = segments.sumOf { it.value.toDouble() }.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (segments.isEmpty()) return

        // Calculate animation progress
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - startTime
        animProgress = (elapsed.toFloat() / animDuration).coerceAtMost(maxAnimProgress)

        // Calculate chart dimensions
        val legendHeight = if (showLegend && (legendPosition == LegendPosition.BOTTOM || legendPosition == LegendPosition.TOP)) {
            height * 0.3f
        } else {
            0f
        }

        val legendWidth = if (showLegend && (legendPosition == LegendPosition.LEFT || legendPosition == LegendPosition.RIGHT)) {
            width * 0.3f
        } else {
            0f
        }

        // Calculate the available space for the chart, accounting for the legend
        val availableWidth = width - legendWidth
        val availableHeight = height - legendHeight

        // Use the smaller dimension to ensure the chart fits on screen
        val chartSize = min(availableWidth, availableHeight)

        // Calculate radius based on available space
        radius = chartSize * 0.4f // Reduced from 0.45f to ensure it fits

        // Calculate the center coordinates to ensure the chart is centered
        val centerX = when (legendPosition) {
            LegendPosition.RIGHT -> availableWidth / 2f
            LegendPosition.LEFT -> (width - availableWidth) + (availableWidth / 2f)
            else -> width / 2f
        }

        val centerY = when (legendPosition) {
            LegendPosition.BOTTOM -> availableHeight / 2f
            LegendPosition.TOP -> (height - availableHeight) + (availableHeight / 2f)
            else -> height / 2f
        }

        // Draw the pie chart
        var startAngle = 0f
        segments.forEach { segment ->
            val sweepAngle = if (totalValue > 0) {
                (segment.value / totalValue) * 360f * animProgress
            } else {
                0f
            }

            paint.color = segment.color
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                paint
            )

            // Draw label in the middle of the segment if large enough
            if (sweepAngle > 15) {
                val midAngleRadians = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val labelRadius = radius * 0.7f
                val labelX = centerX + cos(midAngleRadians).toFloat() * labelRadius
                val labelY = centerY + sin(midAngleRadians).toFloat() * labelRadius

                textPaint.color = getContrastColor(segment.color)
                textPaint.textSize = valueTextSize
                canvas.drawText(
                    String.format("%.0f%%", (segment.value / totalValue * 100)),
                    labelX,
                    labelY,
                    textPaint
                )
            }

            startAngle += sweepAngle
        }

        // Draw the legend if needed
        if (showLegend) {
            drawLegend(canvas, centerX, centerY)
        }

        // Continue animation if not finished
        if (animProgress < maxAnimProgress) {
            invalidate()
        }
    }

    private fun drawLegend(canvas: Canvas, centerX: Float, centerY: Float) {
        textPaint.color = textColor
        textPaint.textSize = labelTextSize
        textPaint.textAlign = Paint.Align.LEFT

        val legendItemHeight = labelTextSize * 1.5f
        val legendItemWidth = width * 0.25f
        val colorBoxSize = labelTextSize * 0.8f

        val startX = when (legendPosition) {
            LegendPosition.LEFT -> 10f
            LegendPosition.RIGHT -> centerX + radius + 20f
            else -> 20f
        }

        val startY = when (legendPosition) {
            LegendPosition.TOP -> 20f
            LegendPosition.BOTTOM -> centerY + radius + 20f
            else -> (height - segments.size * legendItemHeight) / 2
        }

        segments.forEachIndexed { index, segment ->
            val itemY = startY + index * legendItemHeight

            // Draw color box
            paint.color = segment.color
            canvas.drawRect(
                startX,
                itemY,
                startX + colorBoxSize,
                itemY + colorBoxSize,
                paint
            )

            // Draw label
            textPaint.textSize = labelTextSize * 0.9f
            canvas.drawText(
                segment.label,
                startX + colorBoxSize + 10f,
                itemY + labelTextSize,
                textPaint
            )
        }
    }

    private fun getContrastColor(color: Int): Int {
        val r = android.graphics.Color.red(color)
        val g = android.graphics.Color.green(color)
        val b = android.graphics.Color.blue(color)

        // Calculate luminance - simplified version
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255

        // Return white for dark colors, black for light colors
        return if (luminance < 0.6) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    }

    data class PieSegment(
        val label: String,
        val value: Float,
        val color: Int
    )

    enum class LegendPosition {
        TOP, RIGHT, BOTTOM, LEFT
    }
}

/**
 * Compose wrapper for the PieChartView showing category performance
 */
@Composable
fun CategoryPieChart(
    data: List<CategoryPerformance>,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    // Define vibrant colors for pie chart segments
    val colors = listOf(
        Color(0xFFFF5252).toArgb(), // Red
        Color(0xFF448AFF).toArgb(), // Blue
        Color(0xFFFFEB3B).toArgb(), // Yellow
        Color(0xFF4CAF50).toArgb(), // Green
        Color(0xFFFF9800).toArgb(), // Orange
        Color(0xFF9C27B0).toArgb(), // Purple
        Color(0xFF607D8B).toArgb(), // Blue Grey
        Color(0xFFE91E63).toArgb()  // Pink
    )

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChartView(context).apply {
                this.showLegend = true
                this.legendPosition = PieChartView.LegendPosition.RIGHT
                this.textColor = textColor
            }
        },
        update = { view ->
            val chartData = data.mapIndexed { index, category ->
                PieChartView.PieSegment(
                    label = category.categoryName,
                    value = category.revenue.toFloat(),
                    color = colors[index % colors.size]
                )
            }
            view.segments = chartData
            view.textColor = textColor
        }
    )
}

/**
 * Compose wrapper for the PieChartView showing order status distribution
 */
@Composable
fun OrderStatusPieChart(
    data: OrderStatusDistribution,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    // Vibrant colors for order status
    val completedColor = Color(0xFF4CAF50).toArgb()  // Green
    val inProgressColor = Color(0xFF2196F3).toArgb() // Blue
    val canceledColor = Color(0xFFF44336).toArgb()   // Red

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChartView(context).apply {
                this.showLegend = true
                this.legendPosition = PieChartView.LegendPosition.BOTTOM
                this.textColor = textColor
            }
        },
        update = { view ->
            val chartData = listOf(
                PieChartView.PieSegment(
                    label = "Completed",
                    value = data.completed.toFloat(),
                    color = completedColor
                ),
                PieChartView.PieSegment(
                    label = "In Progress",
                    value = data.inProgress.toFloat(),
                    color = inProgressColor
                ),
                PieChartView.PieSegment(
                    label = "Canceled",
                    value = data.canceled.toFloat(),
                    color = canceledColor
                )
            ).filter { it.value > 0 } // Only show segments with values > 0

            view.segments = chartData
            view.textColor = textColor
        }
    )
}
