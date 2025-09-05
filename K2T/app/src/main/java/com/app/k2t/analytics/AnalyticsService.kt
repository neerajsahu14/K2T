package com.app.k2t.analytics

import com.app.k2t.firebase.model.Food
import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.firebase.model.Order
import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.utils.OrderStatus
import java.util.*
import kotlin.collections.forEach
import kotlin.math.min

class AnalyticsService {

    // Time ranges
    fun isToday(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    }

    fun isYesterday(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
    }

    fun isThisWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val current = Calendar.getInstance()

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                calendar.get(Calendar.WEEK_OF_YEAR) == current.get(Calendar.WEEK_OF_YEAR))
    }

    fun isLastWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val lastWeek = Calendar.getInstance()
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1)

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == lastWeek.get(Calendar.YEAR) &&
                calendar.get(Calendar.WEEK_OF_YEAR) == lastWeek.get(Calendar.WEEK_OF_YEAR))
    }

    fun isThisMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val current = Calendar.getInstance()

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == current.get(Calendar.MONTH))
    }

    fun isLastMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val lastMonth = Calendar.getInstance()
        lastMonth.add(Calendar.MONTH, -1)

        calendar.time = date

        return (calendar.get(Calendar.YEAR) == lastMonth.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == lastMonth.get(Calendar.MONTH))
    }

    fun isThisYear(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val current = Calendar.getInstance()

        calendar.time = date

        return calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR)
    }

    // Revenue analysis
    fun calculateRevenueByTimeRange(orders: List<Order>): RevenueByTimeRange {
        val todayRevenue = orders.filter { it.createdAt?.let { date -> isToday(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val yesterdayRevenue = orders.filter { it.createdAt?.let { date -> isYesterday(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val thisWeekRevenue = orders.filter { it.createdAt?.let { date -> isThisWeek(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val lastWeekRevenue = orders.filter { it.createdAt?.let { date -> isLastWeek(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val thisMonthRevenue = orders.filter { it.createdAt?.let { date -> isThisMonth(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val lastMonthRevenue = orders.filter { it.createdAt?.let { date -> isLastMonth(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        val thisYearRevenue = orders.filter { it.createdAt?.let { date -> isThisYear(date) } ?: false }
            .sumOf { it.totalPrice ?: 0.0 }

        return RevenueByTimeRange(
            today = todayRevenue,
            yesterday = yesterdayRevenue,
            thisWeek = thisWeekRevenue,
            lastWeek = lastWeekRevenue,
            thisMonth = thisMonthRevenue,
            lastMonth = lastMonthRevenue,
            thisYear = thisYearRevenue,
            total = orders.sumOf { it.totalPrice ?: 0.0 }
        )
    }

    // Order count analysis
    fun calculateOrderCountsByTimeRange(orders: List<Order>): OrderCountsByTimeRange {
        val todayOrders = orders.count { it.createdAt?.let { date -> isToday(date) } ?: false }
        val yesterdayOrders = orders.count { it.createdAt?.let { date -> isYesterday(date) } ?: false }
        val thisWeekOrders = orders.count { it.createdAt?.let { date -> isThisWeek(date) } ?: false }
        val lastWeekOrders = orders.count { it.createdAt?.let { date -> isLastWeek(date) } ?: false }
        val thisMonthOrders = orders.count { it.createdAt?.let { date -> isThisMonth(date) } ?: false }
        val lastMonthOrders = orders.count { it.createdAt?.let { date -> isLastMonth(date) } ?: false }
        val thisYearOrders = orders.count { it.createdAt?.let { date -> isThisYear(date) } ?: false }

        return OrderCountsByTimeRange(
            today = todayOrders,
            yesterday = yesterdayOrders,
            thisWeek = thisWeekOrders,
            lastWeek = lastWeekOrders,
            thisMonth = thisMonthOrders,
            lastMonth = lastMonthOrders,
            thisYear = thisYearOrders,
            total = orders.size
        )
    }

    // Food performance analysis - Modified to use historical food prices from order items
    fun calculateTopPerformingFoods(
        orderItems: List<OrderItem>,
        foods: List<Food>,
        limit: Int = 10
    ): List<FoodPerformance> {
        val foodPerformanceMap = mutableMapOf<String, FoodPerformance>()

        // Process each order item
        orderItems.forEach { item ->
            val foodId = item.foodId ?: return@forEach
            val quantity = item.quantity ?: 0

            // Use the historical unit price from the order item instead of current price
            val unitPrice = item.unitPrice ?: 0.0
            val revenue = unitPrice * quantity

            // Still get the food name from the list or from the item itself
            val foodName = item.foodName ?: foods.find { it.foodId == foodId }?.name ?: "Unknown"

            val existing = foodPerformanceMap[foodId]
            if (existing != null) {
                foodPerformanceMap[foodId] = existing.copy(
                    revenue = existing.revenue + revenue,
                    orderCount = existing.orderCount + 1,
                    quantitySold = existing.quantitySold + quantity
                )
            } else {
                foodPerformanceMap[foodId] = FoodPerformance(
                    foodId = foodId,
                    foodName = foodName,
                    revenue = revenue,
                    orderCount = 1,
                    quantitySold = quantity
                )
            }
        }

        // Sort by revenue (highest first) and limit to requested count
        return foodPerformanceMap.values
            .sortedByDescending { it.revenue }
            .take(min(limit, foodPerformanceMap.size))
    }

    // Category performance analysis
    fun calculateCategoryPerformance(
        orderItems: List<OrderItem>,
        categories: List<FoodCategory>
    ): List<CategoryPerformance> {
        val categoryMap = mutableMapOf<String, CategoryPerformance>()

        // Create a map from foodId to its categories
        val foodToCategoriesMap = mutableMapOf<String, List<String>>()
        categories.forEach { category ->
            category.foodsIds.forEach { foodId ->
                val currentCategories = foodToCategoriesMap[foodId] ?: emptyList()
                foodToCategoriesMap[foodId] = currentCategories + category.id!!
            }
        }

        // Process each order item
        orderItems.forEach { item ->
            val foodId = item.foodId ?: return@forEach
            // Always use the item's stored unit price rather than looking up current price
            val unitPrice = item.unitPrice ?: 0.0
            val quantity = item.quantity ?: 0
            val revenue = unitPrice * quantity

            // Find which categories this food belongs to
            val categoriesForFood = foodToCategoriesMap[foodId] ?: emptyList()

            // Update stats for each category this food belongs to
            categoriesForFood.forEach { categoryId ->
                val category = categories.find { it.id == categoryId } ?: return@forEach

                val existing = categoryMap[categoryId]
                if (existing != null) {
                    categoryMap[categoryId] = existing.copy(
                        revenue = existing.revenue + revenue,
                        orderCount = existing.orderCount + 1,
                        quantitySold = existing.quantitySold + quantity
                    )
                } else {
                    categoryMap[categoryId] = CategoryPerformance(
                        categoryId = categoryId,
                        categoryName = category.name,
                        revenue = revenue,
                        orderCount = 1,
                        quantitySold = quantity
                    )
                }
            }
        }

        // Sort by revenue (highest first)
        return categoryMap.values.sortedByDescending { it.revenue }
    }

    // Daily revenue analysis for charts
    fun calculateDailyRevenue(orders: List<Order>, orderItems: List<OrderItem>, days: Int = 7): List<DailyRevenue> {
        val result = mutableListOf<DailyRevenue>()
        val calendar = Calendar.getInstance()

        // If no orders available, return empty days but with structure
        if (orders.isEmpty() && orderItems.isEmpty()) {
            // Start from [days] days ago
            calendar.add(Calendar.DAY_OF_YEAR, -(days - 1))

            // For each day, create empty revenue data
            for (i in 0 until days) {
                val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val dateString = "$month/$day"

                result.add(DailyRevenue(dateString, 0.0))
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return result
        }

        // Reset calendar for normal processing
        calendar.add(Calendar.DAY_OF_YEAR, -(days - 1))

        // Set to the beginning of that day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Create a map of order ID to date for filtering order items
        val orderDates = orders.filter { it.orderId != null && it.createdAt != null }
            .associate { it.orderId!! to it.createdAt!! }

        // For each day
        for (i in 0 until days) {
            val dayStart = calendar.time

            // Move to end of the day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.time

            // Calculate revenue for this day using order items with accurate historical prices
            val dailyRevenue = if (orderItems.isNotEmpty()) {
                // First try with orderItems.addedAt date if available
                val itemsWithDatesInRange = orderItems.filter { item ->
                    val itemDate = item.addedAt
                    itemDate != null && itemDate >= dayStart && itemDate < dayEnd
                }

                // If no items found with their own dates, try using the parent order date
                if (itemsWithDatesInRange.isNotEmpty()) {
                    itemsWithDatesInRange.sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }
                } else {
                    // Find items belonging to orders created on this day
                    orderItems.filter { item ->
                        val orderId = item.orderId
                        val orderDate = orderId?.let { orderDates[it] }
                        orderDate != null && orderDate >= dayStart && orderDate < dayEnd
                    }.sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }
                }
            } else {
                // Fallback to orders if no order items available
                orders.filter { order ->
                    val orderDate = order.createdAt
                    orderDate != null && orderDate >= dayStart && orderDate < dayEnd
                }.sumOf { it.totalPrice ?: 0.0 }
            }

            // Format the date as MM/dd
            val month = dayStart.month + 1 // Month is 0-indexed
            val day = dayStart.date
            val dateString = "$month/$day"

            result.add(DailyRevenue(dateString, dailyRevenue))
        }

        return result
    }

    // Revenue by hour of day (for identifying peak hours)
    fun calculateRevenueByHourOfDay(orders: List<Order>, orderItems: List<OrderItem>): List<HourlyRevenue> {
        val hourlyRevenue = Array(24) { 0.0 }

        // If no orders and no items, return the empty hour structure
        if (orders.isEmpty() && orderItems.isEmpty()) {
            return hourlyRevenue.mapIndexed { index, _ ->
                HourlyRevenue(hour = index, revenue = 0.0)
            }
        }

        // Create a map of order ID to creation timestamp
        val orderCreationTime = orders.filter { it.orderId != null && it.createdAt != null }
            .associate { it.orderId!! to it.createdAt!! }

        if (orderItems.isNotEmpty()) {
            // Prefer using order items with their accurate historical prices
            orderItems.forEach { item ->
                // First try using the item's own timestamp
                val itemDate = item.addedAt
                // If item has no timestamp, try using parent order timestamp
                val date = itemDate ?: item.orderId?.let { orderCreationTime[it] }

                date?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hourlyRevenue[hour] += (item.unitPrice ?: 0.0) * (item.quantity ?: 0)
                }
            }
        } else {
            // Fallback to orders if no order items available
            orders.forEach { order ->
                order.createdAt?.let { date ->
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hourlyRevenue[hour] += order.totalPrice ?: 0.0
                }
            }
        }

        return hourlyRevenue.mapIndexed { index, revenue ->
            HourlyRevenue(hour = index, revenue = revenue)
        }
    }

    // Calculate average order value
    fun calculateAverageOrderValue(orders: List<Order>): Double {
        if (orders.isEmpty()) return 0.0
        return orders.sumOf { it.totalPrice ?: 0.0 } / orders.size
    }

    // Calculate completed vs canceled orders
    fun calculateOrderStatusDistribution(orders: List<Order>): OrderStatusDistribution {
        val completed = orders.count { it.statusCode == OrderStatus.COMPLETED.code }
        val inProgress = orders.count { it.statusCode == OrderStatus.IN_PROGRESS.code }
        val canceled = orders.count { it.statusCode == OrderStatus.CANCELLED.code }

        return OrderStatusDistribution(
            completed = completed,
            inProgress = inProgress,
            canceled = canceled,
            total = orders.size
        )
    }

    // New method to calculate revenue from order items
    fun calculateRevenueByTimeRangeFromOrderItems(orderItems: List<OrderItem>): RevenueByTimeRange {
        val todayRevenue = orderItems.filter { it.addedAt?.let { date -> isToday(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val yesterdayRevenue = orderItems.filter { it.addedAt?.let { date -> isYesterday(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val thisWeekRevenue = orderItems.filter { it.addedAt?.let { date -> isThisWeek(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val lastWeekRevenue = orderItems.filter { it.addedAt?.let { date -> isLastWeek(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val thisMonthRevenue = orderItems.filter { it.addedAt?.let { date -> isThisMonth(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val lastMonthRevenue = orderItems.filter { it.addedAt?.let { date -> isLastMonth(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val thisYearRevenue = orderItems.filter { it.addedAt?.let { date -> isThisYear(date) } ?: false }
            .sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        val totalRevenue = orderItems.sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

        return RevenueByTimeRange(
            today = todayRevenue,
            yesterday = yesterdayRevenue,
            thisWeek = thisWeekRevenue,
            lastWeek = lastWeekRevenue,
            thisMonth = thisMonthRevenue,
            lastMonth = lastMonthRevenue,
            thisYear = thisYearRevenue,
            total = totalRevenue
        )
    }

    // New method to calculate order counts from order items
    fun calculateOrderCountsByTimeRangeFromOrderItems(orderItems: List<OrderItem>): OrderCountsByTimeRange {
        // Group order items by orderId to count unique orders
        val uniqueOrderIds = orderItems.mapNotNull { it.orderId }.distinct()

        // Count orders by date using a map of orderId to date
        val orderDates = orderItems.filter { it.orderId != null && it.addedAt != null }
            .groupBy { it.orderId }
            .mapValues { entry ->
                entry.value.firstOrNull()?.addedAt
            }

        val todayOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isToday(date) } ?: false
        }

        val yesterdayOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isYesterday(date) } ?: false
        }

        val thisWeekOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isThisWeek(date) } ?: false
        }

        val lastWeekOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isLastWeek(date) } ?: false
        }

        val thisMonthOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isThisMonth(date) } ?: false
        }

        val lastMonthOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isLastMonth(date) } ?: false
        }

        val thisYearOrders = uniqueOrderIds.count { orderId ->
            orderDates[orderId]?.let { date -> isThisYear(date) } ?: false
        }

        return OrderCountsByTimeRange(
            today = todayOrders,
            yesterday = yesterdayOrders,
            thisWeek = thisWeekOrders,
            lastWeek = lastWeekOrders,
            thisMonth = thisMonthOrders,
            lastMonth = lastMonthOrders,
            thisYear = thisYearOrders,
            total = uniqueOrderIds.size
        )
    }

    // Daily revenue analysis from order items
    fun calculateDailyRevenueFromOrderItems(orderItems: List<OrderItem>, days: Int = 7): List<DailyRevenue> {
        val result = mutableListOf<DailyRevenue>()
        val calendar = Calendar.getInstance()

        // Start from [days] days ago
        calendar.add(Calendar.DAY_OF_YEAR, -(days - 1))

        // For each day
        for (i in 0 until days) {
            val dayStart = calendar.clone() as Calendar
            dayStart.set(Calendar.HOUR_OF_DAY, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MILLISECOND, 0)

            val startDate = dayStart.time

            val dayEnd = dayStart.clone() as Calendar
            dayEnd.add(Calendar.DAY_OF_YEAR, 1)
            val endDate = dayEnd.time

            // Calculate revenue for this day
            val dailyRevenue = orderItems.filter { item ->
                val orderDate = item.addedAt
                orderDate != null && orderDate >= startDate && orderDate < endDate
            }.sumOf { (it.unitPrice ?: 0.0) * (it.quantity ?: 0) }

            // Format the date as MM/dd
            val month = startDate.month + 1 // Month is 0-indexed
            val day = startDate.date
            val dateString = "$month/$day"

            result.add(DailyRevenue(dateString, dailyRevenue))

            // Move to next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result
    }

    // Revenue by hour of day from order items
    fun calculateRevenueByHourOfDayFromOrderItems(orderItems: List<OrderItem>): List<HourlyRevenue> {
        val hourlyRevenue = Array(24) { 0.0 }

        orderItems.forEach { item ->
            item.addedAt?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                hourlyRevenue[hour] += (item.unitPrice ?: 0.0) * (item.quantity ?: 0)
            }
        }

        return hourlyRevenue.mapIndexed { index, revenue ->
            HourlyRevenue(hour = index, revenue = revenue)
        }
    }
}

// Data classes for analytics results

data class RevenueByTimeRange(
    val today: Double,
    val yesterday: Double,
    val thisWeek: Double,
    val lastWeek: Double,
    val thisMonth: Double,
    val lastMonth: Double,
    val thisYear: Double,
    val total: Double
)

data class OrderCountsByTimeRange(
    val today: Int,
    val yesterday: Int,
    val thisWeek: Int,
    val lastWeek: Int,
    val thisMonth: Int,
    val lastMonth: Int,
    val thisYear: Int,
    val total: Int
)

data class FoodPerformance(
    val foodId: String,
    val foodName: String,
    val revenue: Double,
    val orderCount: Int,
    val quantitySold: Int
)

data class CategoryPerformance(
    val categoryId: String,
    val categoryName: String,
    val revenue: Double,
    val orderCount: Int,
    val quantitySold: Int
)

data class DailyRevenue(
    val date: String,
    val revenue: Double
)

data class HourlyRevenue(
    val hour: Int,
    val revenue: Double
)

data class OrderStatusDistribution(
    val completed: Int,
    val inProgress: Int,
    val canceled: Int,
    val total: Int
)
