package com.app.k2t.ui.presentation.screen.admin.analytics

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.k2t.R
import com.app.k2t.analytics.*
import com.app.k2t.ui.presentation.screen.admin.analytics.components.*
import com.app.k2t.ui.presentation.viewmodel.AnalyticsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.*
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val TAG = "AnalyticsScreen"

    val revenueByTime by viewModel.revenueByTimeRange.collectAsState()
    val orderCountsByTime by viewModel.orderCountsByTimeRange.collectAsState()
    val topFoods by viewModel.topPerformingFoods.collectAsState()
    val categoryPerformance by viewModel.categoryPerformance.collectAsState()
    val dailyRevenue by viewModel.dailyRevenue.collectAsState()
    val hourlyRevenue by viewModel.hourlyRevenue.collectAsState()
    val averageOrderValue by viewModel.averageOrderValue.collectAsState()
    val orderStatusDist by viewModel.orderStatusDistribution.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Log state changes
    LaunchedEffect(Unit) {
        Log.d(TAG, "AnalyticsScreen composable initialized")
    }

    LaunchedEffect(isLoading) {
        Log.d(TAG, "Loading state changed: isLoading = $isLoading")
    }

    LaunchedEffect(error) {
        Log.d(TAG, "Error state changed: error = $error")
    }

    LaunchedEffect(revenueByTime) {
        Log.d(TAG, "RevenueByTime received: ${revenueByTime != null}")
    }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance().apply { currency = Currency.getInstance("INR") } }

    var selectedTimeTab by remember { mutableIntStateOf(0) }
    val timeTabs = listOf("Today", "Week", "Month", "All Time")

    // Log render state
    Log.d(TAG, "Rendering with: isLoading=$isLoading, hasError=${error != null}, hasData=${revenueByTime != null}")

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
        ) {
            // Enhanced header with shadow and better alignment
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.analytics),
                        contentDescription = "Analytics",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Business Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    FilledIconButton(
                        onClick = {
                            Log.d(TAG, "Refresh button clicked")
                            viewModel.refreshData()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            ) {
                // Loading State
                if (isLoading) {
                    Log.d(TAG, "Rendering loading state")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(56.dp),
                                strokeWidth = 4.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Loading analytics data...",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                // Error State
                else if (error != null) {
                    Log.d(TAG, "Rendering error state: $error")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    painterResource(R.drawable.error),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error loading analytics",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error ?: "Unknown error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        Log.d(TAG, "Retry button clicked")
                                        viewModel.refreshData()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                // Content State with enhanced scrolling and card styling
                else {
                    Log.d(TAG, "Rendering content state")
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Enhanced Time Period Selector Tabs with animation
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            TabRow(
                                selectedTabIndex = selectedTimeTab,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicator = { tabPositions ->
                                    TabRowDefaults.PrimaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTimeTab]),
                                        width = tabPositions[selectedTimeTab].width,
                                        height = 3.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            ) {
                                timeTabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTimeTab == index,
                                        onClick = { selectedTimeTab = index },
                                        text = {
                                            Text(
                                                title,
                                                fontWeight = if (selectedTimeTab == index)
                                                    FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Summary Cards with enhanced style
                        SummarySection(
                            revenueByTime = revenueByTime,
                            orderCountsByTime = orderCountsByTime,
                            selectedPeriod = selectedTimeTab,
                            currencyFormat = currencyFormat
                        )

                        // Daily Revenue Chart
                        AnalyticsCard(title = "Daily Revenue (Last 7 Days)") {
                            if (dailyRevenue.isEmpty()) {
                                EmptyDataMessage("No daily revenue data available")
                            } else {
                                DailyRevenueChart(
                                    data = dailyRevenue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    barColor = AndroidColor.parseColor("#FF6D00") // Vibrant orange
                                )
                            }
                        }

                        // Top Selling Items with improved table design
                        AnalyticsCard(title = "Top Selling Items") {
                            if (topFoods.isEmpty()) {
                                EmptyDataMessage("No food sales data available")
                            } else {
                                // Log data for debugging
                                Log.d(TAG, "TopFoods data available: ${topFoods.size} items")
                                topFoods.forEachIndexed { index, food ->
                                    Log.d(TAG, "TopFood $index: ${food.foodName}, revenue: ${food.revenue}")
                                }
                                TopFoodsTable(
                                    foods = topFoods.take(5),
                                    currencyFormat = currencyFormat
                                )
                            }
                        }

                        // Category Performance
                        AnalyticsCard(title = "Category Performance") {
                            if (categoryPerformance.isEmpty()) {
                                EmptyDataMessage("No category performance data available")
                            } else {
                                CategoryPieChart(
                                    data = categoryPerformance.take(6),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                )
                            }
                        }

                        // Hourly Revenue
                        AnalyticsCard(title = "Revenue by Hour") {
                            if (hourlyRevenue.isEmpty()) {
                                EmptyDataMessage("No hourly revenue data available")
                            } else {
                                HourlyRevenueChart(
                                    data = hourlyRevenue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    barColor = AndroidColor.parseColor("#1E88E5") // Vibrant blue
                                )
                            }
                        }

                        // Order Status Distribution
                        AnalyticsCard(title = "Order Status Distribution") {
                            orderStatusDist?.let { statusData ->
                                if (statusData.total == 0) {
                                    EmptyDataMessage("No order status data available")
                                } else {
                                    OrderStatusPieChart(
                                        data = statusData,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    )
                                }
                            } ?: EmptyDataMessage("No order status data available")
                        }

                        // Average Order Value
                        AnalyticsCard(title = "Average Order Value") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = currencyFormat.format(averageOrderValue),
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "Average Order Value",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        // Time Range Data Table
                        AnalyticsCard(title = "Revenue & Orders by Time Period") {
                            TimeRangeDataTable(
                                revenueByTime = revenueByTime,
                                orderCountsByTime = orderCountsByTime,
                                currencyFormat = currencyFormat
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SummarySection(
    revenueByTime: RevenueByTimeRange?,
    orderCountsByTime: OrderCountsByTimeRange?,
    selectedPeriod: Int,
    currencyFormat: NumberFormat
) {
    // Create default values in case our data is null
    val defaultRevenue = RevenueByTimeRange(
        today = 0.0,
        yesterday = 0.0,
        thisWeek = 0.0,
        lastWeek = 0.0,
        thisMonth = 0.0,
        lastMonth = 0.0,
        thisYear = 0.0,
        total = 0.0
    )

    val defaultOrderCounts = OrderCountsByTimeRange(
        today = 0,
        yesterday = 0,
        thisWeek = 0,
        lastWeek = 0,
        thisMonth = 0,
        lastMonth = 0,
        thisYear = 0,
        total = 0
    )

    // Use the default values if our data is null
    val safeRevenueByTime = revenueByTime ?: defaultRevenue
    val safeOrderCountsByTime = orderCountsByTime ?: defaultOrderCounts

    // Now use the safe objects for our selection
    val (revenue, orders) = when (selectedPeriod) {
        0 -> Pair(safeRevenueByTime.today, safeOrderCountsByTime.today)
        1 -> Pair(safeRevenueByTime.thisWeek, safeOrderCountsByTime.thisWeek)
        2 -> Pair(safeRevenueByTime.thisMonth, safeOrderCountsByTime.thisMonth)
        else -> Pair(safeRevenueByTime.total, safeOrderCountsByTime.total)
    }

    val periodName = when (selectedPeriod) {
        0 -> "Today's"
        1 -> "This Week's"
        2 -> "This Month's"
        else -> "All Time"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(id = R.drawable.calendar_week),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$periodName Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Revenue",
                    value = currencyFormat.format(revenue),
                    icon = R.drawable.rupee,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Orders",
                    value = orders.toString(),
                    icon = R.drawable.restaurant,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TopFoodsTable(
    foods: List<FoodPerformance>,
    currencyFormat: NumberFormat
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Enhanced Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Item",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Revenue",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Food Rows with alternating backgrounds
        foods.forEachIndexed { index, food ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (index % 2 == 0)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = food.foodName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(2f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currencyFormat.format(food.revenue),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = food.quantitySold.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            if (index < foods.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun TimeRangeDataTable(
    revenueByTime: RevenueByTimeRange?,
    orderCountsByTime: OrderCountsByTimeRange?,
    currencyFormat: NumberFormat
) {
    if (revenueByTime == null || orderCountsByTime == null) return

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Enhanced Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Period",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = "Revenue",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = "Orders",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time Periods Data
        TimeRangeRow("Today", revenueByTime.today, orderCountsByTime.today, currencyFormat, false)
        TimeRangeRow("Yesterday", revenueByTime.yesterday, orderCountsByTime.yesterday, currencyFormat, true)
        TimeRangeRow("This Week", revenueByTime.thisWeek, orderCountsByTime.thisWeek, currencyFormat, false)
        TimeRangeRow("Last Week", revenueByTime.lastWeek, orderCountsByTime.lastWeek, currencyFormat, true)
        TimeRangeRow("This Month", revenueByTime.thisMonth, orderCountsByTime.thisMonth, currencyFormat, false)
        TimeRangeRow("Last Month", revenueByTime.lastMonth, orderCountsByTime.lastMonth, currencyFormat, true)
        TimeRangeRow("This Year", revenueByTime.thisYear, orderCountsByTime.thisYear, currencyFormat, false)

        Spacer(modifier = Modifier.height(16.dp))

        // Total Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Time",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = currencyFormat.format(revenueByTime.total),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = orderCountsByTime.total.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TimeRangeRow(
    period: String,
    revenue: Double,
    orders: Int,
    currencyFormat: NumberFormat,
    isAlternate: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isAlternate)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = period,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = currencyFormat.format(revenue),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = orders.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun EmptyDataMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(id = R.drawable.search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
