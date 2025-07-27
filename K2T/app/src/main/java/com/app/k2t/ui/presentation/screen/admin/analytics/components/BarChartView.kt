package com.app.k2t.ui.presentation.screen.admin.analytics.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.viewinterop.AndroidView
import com.app.k2t.analytics.DailyRevenue
import com.app.k2t.analytics.HourlyRevenue
import kotlin.math.max
import androidx.core.graphics.toColorInt

/**
 * Custom Android View for bar charts
 */
class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Chart data
    var data: List<BarData> = emptyList()
        set(value) {
            field = value
            invalidate() // Redraw when data changes
        }

    // Chart styling
    var barColor: Int = "#6200EE".toColorInt() // Default purple color
    var barWidth: Float = 0f // Will be calculated based on view width and data size
    var barSpacing: Float = 0f // Will be calculated based on view width
    var textColor: Int = android.graphics.Color.BLACK
    var axisColor: Int = android.graphics.Color.LTGRAY
    var labelTextSize: Float = 36f
    var valueTextSize: Float = 30f

    // Cached objects for drawing efficiency
    private val barPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val axisPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = axisColor
        isAntiAlias = true
    }

    // Animation
    private var animProgress = 0f
    private val maxAnimProgress = 1f
    private val animDuration = 1000L // 1 second
    private val startTime = System.currentTimeMillis()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        // Calculate animation progress
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - startTime
        animProgress = (elapsed.toFloat() / animDuration).coerceAtMost(maxAnimProgress)

        // Calculate margins and chart area
        val axisMargin = height * 0.1f
        val horizontalMargin = width * 0.05f  // Add horizontal margins (5% on each side)
        val chartBottom = height - axisMargin
        val chartTop = axisMargin
        val chartHeight = chartBottom - chartTop

        // Calculate bar width and spacing - improved to use full available width
        val availableWidth = width - (2 * horizontalMargin)
        val totalBars = data.size

        // Adjust the ratio between bar width and spacing
        barWidth = (availableWidth / (totalBars * 1.3f)).coerceAtMost(availableWidth / 10)  // Max 1/10 of width
        barSpacing = barWidth * 0.3f

        // Find the maximum value for scaling
        val maxValue = data.maxOfOrNull { it.value } ?: 0f

        // Set up paints
        barPaint.color = barColor
        textPaint.color = textColor
        textPaint.textSize = labelTextSize
        axisPaint.color = axisColor

        // Draw X and Y axes
        canvas.drawLine(horizontalMargin, chartBottom, width - horizontalMargin, chartBottom, axisPaint)

        // Draw each bar
        var x = horizontalMargin
        data.forEach { barData ->
            val barHeight = if (maxValue > 0) {
                (barData.value / maxValue) * chartHeight * animProgress
            } else {
                0f
            }

            // Draw the bar
            val barLeft = x
            val barRight = barLeft + barWidth
            val barTop = chartBottom - barHeight
            canvas.drawRect(barLeft, barTop, barRight, chartBottom, barPaint)

            // Calculate text size based on available space
            val maxLabelWidth = barWidth * 1.5f
            val textSize = calculateFittingTextSize(barData.label, maxLabelWidth, labelTextSize)

            // Draw the label below the X axis
            textPaint.textSize = textSize
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                barData.label,
                barLeft + barWidth / 2,
                chartBottom + textSize + 8f,
                textPaint
            )

            // Draw the value above the bar if there's enough space
            if (barHeight > valueTextSize) {
                textPaint.textSize = valueTextSize
                canvas.drawText(
                    barData.displayValue ?: String.format("%.0f", barData.value),
                    barLeft + barWidth / 2,
                    barTop - valueTextSize / 2,
                    textPaint
                )
            }

            x += barWidth + barSpacing
        }

        // Continue animation if not finished
        if (animProgress < maxAnimProgress) {
            invalidate()
        }
    }

    // Helper method to calculate text size that fits within a given width
    private fun calculateFittingTextSize(text: String, maxWidth: Float, defaultSize: Float): Float {
        var size = defaultSize
        textPaint.textSize = size
        val textWidth = textPaint.measureText(text)

        // If text is too wide, reduce the text size
        if (textWidth > maxWidth && size > 8f) {  // Don't go smaller than 8sp
            size *= maxWidth / textWidth
            size = size.coerceAtLeast(8f)  // Ensure minimum readable size
        }

        return size
    }

    data class BarData(
        val label: String,
        val value: Float,
        val displayValue: String? = null
    )
}

/**
 * Compose wrapper for the BarChartView
 */
@Composable
fun DailyRevenueChart(
    data: List<DailyRevenue>,
    modifier: Modifier = Modifier,
    barColor: Int
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val axisColor = MaterialTheme.colorScheme.outline.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChartView(context).apply {
                this.barColor = barColor
                this.textColor = textColor
                this.axisColor = axisColor
            }
        },
        update = { view ->
            val chartData = data.map { dailyData ->
                BarChartView.BarData(
                    label = dailyData.date,
                    value = dailyData.revenue.toFloat(),
                    displayValue = "₹${dailyData.revenue.toInt()}"
                )
            }
            view.data = chartData
            view.barColor = barColor
            view.textColor = textColor
            view.axisColor = axisColor
        }
    )
}

/**
 * Compose wrapper for hourly revenue chart
 */
@Composable
fun HourlyRevenueChart(
    data: List<HourlyRevenue>,
    modifier: Modifier = Modifier,
    barColor: Int
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val axisColor = MaterialTheme.colorScheme.outline.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChartView(context).apply {
                this.barColor = barColor
                this.textColor = textColor
                this.axisColor = axisColor
            }
        },
        update = { view ->
            // Format hour labels to prevent overlap by only showing every 4th hour
            // and adding AM/PM designation for better readability
            val chartData = data.mapIndexed { index, hourlyData ->
                val hour = hourlyData.hour
                val formattedLabel = when {
                    // Show major hours with AM/PM
                    hour % 4 == 0 -> {
                        when (hour) {
                            0 -> "12 AM"
                            12 -> "12 PM"
                            else -> {
                                val displayHour = if (hour > 12) hour - 12 else hour
                                "$displayHour ${if (hour < 12) "AM" else "PM"}"
                            }
                        }
                    }
                    // For other hours, just show a tick mark
                    else -> ""
                }

                BarChartView.BarData(
                    label = formattedLabel,
                    value = hourlyData.revenue.toFloat(),
                    displayValue = "₹${hourlyData.revenue.toInt()}"
                )
            }
            view.data = chartData
            view.barColor = barColor
            view.textColor = textColor
            view.axisColor = axisColor
        }
    )
}
