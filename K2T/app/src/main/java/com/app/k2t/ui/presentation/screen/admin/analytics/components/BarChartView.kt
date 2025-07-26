package com.app.k2t.ui.presentation.screen.admin.analytics.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.app.k2t.analytics.DailyRevenue
import com.app.k2t.analytics.HourlyRevenue
import kotlin.math.max

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
    var barColor: Int = Color.parseColor("#6200EE") // Default purple color
    var barWidth: Float = 0f // Will be calculated based on view width and data size
    var barSpacing: Float = 0f // Will be calculated based on view width
    var textColor: Int = Color.BLACK
    var axisColor: Int = Color.LTGRAY
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

        // Calculate bar width and spacing
        val availableWidth = width.toFloat()
        barWidth = availableWidth / (data.size * 2)
        barSpacing = barWidth / 2

        // Find the maximum value for scaling
        val maxValue = data.maxOfOrNull { it.value } ?: 0f

        // Set up paints
        barPaint.color = barColor
        textPaint.color = textColor
        textPaint.textSize = labelTextSize
        axisPaint.color = axisColor

        // Draw X and Y axes
        val axisMargin = height * 0.1f
        val chartBottom = height - axisMargin
        val chartTop = axisMargin
        val chartHeight = chartBottom - chartTop

        // Draw X axis
        canvas.drawLine(0f, chartBottom, width.toFloat(), chartBottom, axisPaint)

        // Draw each bar
        var x = barSpacing
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

            // Draw the label below the X axis
            textPaint.textSize = labelTextSize
            canvas.drawText(
                barData.label,
                barLeft + barWidth / 2,
                chartBottom + labelTextSize,
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
    barColor: Int = Color.parseColor("#6200EE")
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChartView(context).apply {
                this.barColor = barColor
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
    barColor: Int = Color.parseColor("#03DAC5")
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChartView(context).apply {
                this.barColor = barColor
            }
        },
        update = { view ->
            val chartData = data.map { hourlyData ->
                BarChartView.BarData(
                    label = "${hourlyData.hour}:00",
                    value = hourlyData.revenue.toFloat(),
                    displayValue = "₹${hourlyData.revenue.toInt()}"
                )
            }
            view.data = chartData
        }
    )
}
