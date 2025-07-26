package com.app.k2t.ui.presentation.screen.admin.analytics

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Analytics") },
                actions = {
                    IconButton(onClick = {
                        Log.d(TAG, "Refresh button clicked")
                        viewModel.refreshData()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Loading State
            if (isLoading) {
                Log.d(TAG, "Rendering loading state")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading analytics data...")
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            Log.d(TAG, "Retry button clicked")
                            viewModel.refreshData()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            // Content State
            else {
                Log.d(TAG, "Rendering content state")
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Time Period Selector Tabs
                    TabRow(
                        selectedTabIndex = selectedTimeTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        timeTabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTimeTab == index,
                                onClick = { selectedTimeTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Summary Cards based on selected time period
                    SummarySection(
                        revenueByTime = revenueByTime,
                        orderCountsByTime = orderCountsByTime,
                        selectedPeriod = selectedTimeTab,
                        currencyFormat = currencyFormat
                    )

                    // Daily Revenue Chart
                    Section(title = "Daily Revenue (Last 7 Days)") {
                        if (dailyRevenue.isEmpty()) {
                            EmptyDataMessage("No daily revenue data available")
                        } else {
                            DailyRevenueChart(
                                data = dailyRevenue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                barColor = AndroidColor.parseColor("#6200EE") // Purple
                            )
                        }
                    }

                    // Top Selling Items
                    Section(title = "Top Selling Items") {
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
                    Section(title = "Category Performance") {
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
                    Section(title = "Revenue by Hour") {
                        if (hourlyRevenue.isEmpty()) {
                            EmptyDataMessage("No hourly revenue data available")
                        } else {
                            HourlyRevenueChart(
                                data = hourlyRevenue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                barColor = AndroidColor.parseColor("#03DAC5") // Teal
                            )
                        }
                    }

                    // Order Status Distribution
                    Section(title = "Order Status Distribution") {
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
                    Section(title = "Average Order Value") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
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
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    // Time Range Data Table
                    TimeRangeDataTable(
                        revenueByTime = revenueByTime,
                        orderCountsByTime = orderCountsByTime,
                        currencyFormat = currencyFormat
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
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

    Column {
        Text(
            text = "$periodName Summary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

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

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            // Food Rows
            foods.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = food.foodName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = currencyFormat.format(food.revenue),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = food.quantitySold.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
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

    Section(title = "Revenue & Orders by Time Period") {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                // Time Periods Data
                TimeRangeRow("Today", revenueByTime.today, orderCountsByTime.today, currencyFormat)
                TimeRangeRow("Yesterday", revenueByTime.yesterday, orderCountsByTime.yesterday, currencyFormat)
                TimeRangeRow("This Week", revenueByTime.thisWeek, orderCountsByTime.thisWeek, currencyFormat)
                TimeRangeRow("Last Week", revenueByTime.lastWeek, orderCountsByTime.lastWeek, currencyFormat)
                TimeRangeRow("This Month", revenueByTime.thisMonth, orderCountsByTime.thisMonth, currencyFormat)
                TimeRangeRow("Last Month", revenueByTime.lastMonth, orderCountsByTime.lastMonth, currencyFormat)
                TimeRangeRow("This Year", revenueByTime.thisYear, orderCountsByTime.thisYear, currencyFormat)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 2.dp,
                    color = DividerDefaults.color
                )

                // Total Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
    }
}

@Composable
fun TimeRangeRow(
    period: String,
    revenue: Double,
    orders: Int,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = orders.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        thickness = DividerDefaults.Thickness,
        color = DividerDefaults.color
    )
}

@Composable
fun EmptyDataMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
