package com.app.k2t.ui.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.analytics.*
import com.app.k2t.firebase.model.Order
import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.repositoryimpl.FoodCategoryRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.FoodRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.OrderItemRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.OrderRepositoryImpl
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class AnalyticsViewModel : ViewModel(), KoinComponent {
    private val TAG = "AnalyticsViewModel"

    private val orderRepository: OrderRepositoryImpl by inject()
    private val orderItemRepository: OrderItemRepositoryImpl by inject()
    private val foodRepository: FoodRepositoryImpl by inject()
    private val categoryRepository: FoodCategoryRepositoryImpl by inject()
    private val analyticsService = AnalyticsService()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Time range analytics
    private val _revenueByTimeRange = MutableStateFlow<RevenueByTimeRange?>(null)
    val revenueByTimeRange: StateFlow<RevenueByTimeRange?> = _revenueByTimeRange

    private val _orderCountsByTimeRange = MutableStateFlow<OrderCountsByTimeRange?>(null)
    val orderCountsByTimeRange: StateFlow<OrderCountsByTimeRange?> = _orderCountsByTimeRange

    // Food performance analytics
    private val _topPerformingFoods = MutableStateFlow<List<FoodPerformance>>(emptyList())
    val topPerformingFoods: StateFlow<List<FoodPerformance>> = _topPerformingFoods

    // Category performance analytics
    private val _categoryPerformance = MutableStateFlow<List<CategoryPerformance>>(emptyList())
    val categoryPerformance: StateFlow<List<CategoryPerformance>> = _categoryPerformance

    // Chart data
    private val _dailyRevenue = MutableStateFlow<List<DailyRevenue>>(emptyList())
    val dailyRevenue: StateFlow<List<DailyRevenue>> = _dailyRevenue

    private val _hourlyRevenue = MutableStateFlow<List<HourlyRevenue>>(emptyList())
    val hourlyRevenue: StateFlow<List<HourlyRevenue>> = _hourlyRevenue

    // Other metrics
    private val _averageOrderValue = MutableStateFlow(0.0)
    val averageOrderValue: StateFlow<Double> = _averageOrderValue

    private val _orderStatusDistribution = MutableStateFlow<OrderStatusDistribution?>(null)
    val orderStatusDistribution: StateFlow<OrderStatusDistribution?> = _orderStatusDistribution

    // Full data collections
    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    private val _allOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())

    init {
        Log.d(TAG, "ViewModel initialized, loading data...")
        loadAllData()
    }

    fun loadAllData() {
        Log.d(TAG, "loadAllData() called, setting loading state to TRUE")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "Loading state set to: ${_isLoading.value}")
                _error.value = null

                // Collect all orders and order items
                Log.d(TAG, "Starting to collect orders and order items")
                collectOrders()
                collectOrderItems()

                // When data is loaded, compute analytics
                Log.d(TAG, "Data loaded, updating analytics")
                updateAllAnalytics()

            } catch (e: Exception) {
                Log.e(TAG, "Error loading analytics data: ${e.message}", e)
                _error.value = "Failed to load analytics data: ${e.message}"
            } finally {
                Log.d(TAG, "Setting loading state to FALSE")
                _isLoading.value = false
                Log.d(TAG, "Loading state after setting: ${_isLoading.value}")
            }
        }
    }

    private suspend fun collectOrders() {
        try {
            Log.d(TAG, "collectOrders() started")
            val task = orderRepository.getAllOrders()
            Log.d(TAG, "getAllOrders() task created, awaiting...")

            // Use withContext to move the blocking Tasks.await() call to the IO dispatcher
            val snapshot = withContext(Dispatchers.IO) {
                Tasks.await(task)
            }

            Log.d(TAG, "Task completed, documents count: ${snapshot.documents.size}")
            val orders = snapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)?.apply {
                    orderId = document.id
                }
            }
            Log.d(TAG, "Mapped ${orders.size} orders from documents")
            _allOrders.value = orders
            Log.d(TAG, "Orders collection complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error collecting orders: ${e.message}", e)
            _error.value = "Failed to load orders: ${e.message}"
        }
    }

    private suspend fun collectOrderItems() {
        Log.d(TAG, "collectOrderItems() started")
        try {
            // Use withContext and a timeout to ensure we don't block indefinitely
            withContext(Dispatchers.IO) {
                // Use timeout to ensure we don't block indefinitely
                withTimeoutOrNull(5000) { // 5 seconds timeout
                    orderItemRepository.getAllOrderItems().collect { items ->
                        Log.d(TAG, "Collected ${items.size} order items")
                        _allOrderItems.value = items
                        // Cancel collection after first emission to prevent indefinite waiting
                        currentCoroutineContext().cancel()
                    }
                } ?: run {
                    Log.w(TAG, "Order items collection timed out, using last received data")
                }
            }
            Log.d(TAG, "Order items collection complete")
        } catch (e: Exception) {
            // Ignore cancellation exceptions as they're expected when we cancel the coroutine
            if (e is kotlinx.coroutines.CancellationException) {
                Log.d(TAG, "Order items collection was cancelled as expected")
            } else {
                Log.e(TAG, "Error collecting order items: ${e.message}", e)
                _error.value = "Failed to load order items: ${e.message}"
            }
        }
    }

    fun updateAllAnalytics() {
        Log.d(TAG, "updateAllAnalytics() started")
        viewModelScope.launch {
            try {
                val orders = _allOrders.value
                val orderItems = _allOrderItems.value
                Log.d(TAG, "Analytics input: ${orders.size} orders, ${orderItems.size} order items")

                // Get foods and categories
                Log.d(TAG, "Fetching foods and categories")
                val foods = foodRepository.getAllFoods().first()
                val categories = categoryRepository.getAllFoodCategories().first()
                Log.d(TAG, "Fetched ${foods.size} foods and ${categories.size} categories")

                // Calculate time-based metrics
                Log.d(TAG, "Calculating time-based metrics")
                if (orders.isEmpty() && orderItems.isNotEmpty()) {
                    // If no orders but we have order items, calculate from order items
                    Log.d(TAG, "No orders found, calculating metrics from ${orderItems.size} order items")
                    _revenueByTimeRange.value = analyticsService.calculateRevenueByTimeRangeFromOrderItems(orderItems)
                    _orderCountsByTimeRange.value = analyticsService.calculateOrderCountsByTimeRangeFromOrderItems(orderItems)
                    _dailyRevenue.value = analyticsService.calculateDailyRevenueFromOrderItems(orderItems)
                    _hourlyRevenue.value = analyticsService.calculateRevenueByHourOfDayFromOrderItems(orderItems)
                } else {
                    // Use orders for calculations if available
                    _revenueByTimeRange.value = analyticsService.calculateRevenueByTimeRange(orders)
                    _orderCountsByTimeRange.value = analyticsService.calculateOrderCountsByTimeRange(orders)
                    _dailyRevenue.value = analyticsService.calculateDailyRevenue(orders, orderItems)
                    _hourlyRevenue.value = analyticsService.calculateRevenueByHourOfDay(orders, orderItems)
                }

                // Log the values to help debug
                Log.d(TAG, "Revenue by time: today=${_revenueByTimeRange.value?.today}, week=${_revenueByTimeRange.value?.thisWeek}, month=${_revenueByTimeRange.value?.thisMonth}, total=${_revenueByTimeRange.value?.total}")
                Log.d(TAG, "Order counts by time: today=${_orderCountsByTimeRange.value?.today}, week=${_orderCountsByTimeRange.value?.thisWeek}, month=${_orderCountsByTimeRange.value?.thisMonth}, total=${_orderCountsByTimeRange.value?.total}")

                // Ensure we have default values for time-based metrics
                if (_revenueByTimeRange.value == null) {
                    Log.d(TAG, "Creating default revenue by time object")
                    _revenueByTimeRange.value = RevenueByTimeRange(
                        today = 0.0,
                        yesterday = 0.0,
                        thisWeek = 0.0,
                        lastWeek = 0.0,
                        thisMonth = 0.0,
                        lastMonth = 0.0,
                        thisYear = 0.0,
                        total = 0.0
                    )
                }

                if (_orderCountsByTimeRange.value == null) {
                    Log.d(TAG, "Creating default order counts by time object")
                    _orderCountsByTimeRange.value = OrderCountsByTimeRange(
                        today = 0,
                        yesterday = 0,
                        thisWeek = 0,
                        lastWeek = 0,
                        thisMonth = 0,
                        lastMonth = 0,
                        thisYear = 0,
                        total = 0
                    )
                }

                // Calculate food performance metrics
                Log.d(TAG, "Calculating food performance metrics")
                _topPerformingFoods.value = analyticsService.calculateTopPerformingFoods(orderItems, foods)

                // Calculate category performance
                Log.d(TAG, "Calculating category performance")
                _categoryPerformance.value = analyticsService.calculateCategoryPerformance(
                    orderItems, foods, categories
                )

                // Calculate other metrics
                Log.d(TAG, "Calculating other metrics")

                // Average order value - use order items if no orders
                _averageOrderValue.value = if (orders.isNotEmpty()) {
                    calculateSafeAverageOrderValue(orders)
                } else if (orderItems.isNotEmpty()) {
                    // Group by order ID to get average order value
                    val orderTotals = orderItems
                        .filter { it.orderId != null }
                        .groupBy { it.orderId }
                        .map { entry -> entry.value.sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) } }

                    if (orderTotals.isNotEmpty()) {
                        orderTotals.average()
                    } else {
                        0.0
                    }
                } else {
                    0.0
                }

                // Create a basic order status distribution if no orders
                _orderStatusDistribution.value = if (orders.isNotEmpty()) {
                    analyticsService.calculateOrderStatusDistribution(orders)
                } else {
                    OrderStatusDistribution(completed = 0, inProgress = 0, canceled = 0, total = 0)
                }

                Log.d(TAG, "Analytics update completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating analytics: ${e.message}", e)
                _error.value = "Error updating analytics: ${e.message}"
            }
        }
    }

    // Helper function to safely calculate average order value
    private fun calculateSafeAverageOrderValue(orders: List<Order>): Double {
        return if (orders.isNotEmpty()) {
            orders.sumOf { it.totalPrice ?: 0.0 } / orders.size
        } else {
            0.0 // Return 0.0 if there are no orders
        }
    }

    fun refreshData() {
        Log.d(TAG, "refreshData() called")
        loadAllData()
    }

    fun clearError() {
        _error.value = null
    }

    // Specialized methods for specific time ranges
    fun getTodayRevenue(): Double = _revenueByTimeRange.value?.today ?: 0.0
    fun getThisWeekRevenue(): Double = _revenueByTimeRange.value?.thisWeek ?: 0.0
    fun getThisMonthRevenue(): Double = _revenueByTimeRange.value?.thisMonth ?: 0.0

    fun getTodayOrderCount(): Int = _orderCountsByTimeRange.value?.today ?: 0
    fun getThisWeekOrderCount(): Int = _orderCountsByTimeRange.value?.thisWeek ?: 0
    fun getThisMonthOrderCount(): Int = _orderCountsByTimeRange.value?.thisMonth ?: 0

    // Get top X foods by revenue
    fun getTopFoodsByRevenue(count: Int = 5): List<FoodPerformance> {
        return _topPerformingFoods.value.take(count)
    }

    // Get top X categories by revenue
    fun getTopCategoriesByRevenue(count: Int = 5): List<CategoryPerformance> {
        return _categoryPerformance.value.take(count)
    }
}
