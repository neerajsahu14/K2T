package com.app.k2t.ui.presentation.screen.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.k2t.R
import com.app.k2t.firebase.model.Food
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shared content for displaying food details that can be reused by both table and admin interfaces
 */
@Composable
fun FoodDetailsContent(
    food: Food,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
    isAdminView: Boolean = false,
    onPlayVideo: () -> Unit = {},
    showNutritionDetails: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = contentPadding.calculateBottomPadding())
            .verticalScroll(scrollState)
    ) {
        // Food Image/Video Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            if (food.imageUrl != null) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_fastfood_24),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = food.name ?: "Food Item",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Video play button
            if (food.videoUrl != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = onPlayVideo,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Veg/Non-Veg Indicator
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                color = if (food.isVeg == true)
                    Color(0xFF00C853).copy(alpha = 0.9f)
                else
                    Color(0xFFD50000).copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp),
                content = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                    }
                }
            )

            // Availability Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = if (food.availability == true)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = if (food.availability == true) "Available" else "Sold Out",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (food.availability == true)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Content Section
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Food Name and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.name ?: "Unknown Dish",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    food.details?.description?.let { description ->
                        if (description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format(Locale.getDefault(),"%.0f", food.price ?: 0.0)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    // Only show ratings in customer view
                    if (!isAdminView) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "4.5 (128 reviews)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Info Cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    InfoCard(
                        icon = if (food.isVeg == true)
                            R.drawable.veg
                        else
                            R.drawable.non_veg,
                        title = "Type",
                        value = if (food.isVeg == true) "Vegetarian" else "Non-Vegetarian"
                    )
                }

                item {
                    InfoCard(
                        icon = R.drawable.baseline_local_fire_department_24,
                        title = "Spice Level",
                        value = "Medium" // This can be made dynamic if added to data model
                    )
                }

                // Show nutrition info in info cards if available
                food.nutrition?.calories?.let {
                    item {
                        InfoCard(
                            icon = R.drawable.baseline_local_fire_department_24,
                            title = "Calories",
                            value = "${it.toInt()} kcal"
                        )
                    }
                }

                food.nutrition?.servingSize?.let {
                    item {
                        InfoCard(
                            icon = R.drawable.restaurant_menu,
                            title = "Serving",
                            value = it
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ingredients Section
            food.details?.ingredients?.let { ingredients ->
                if (ingredients.isNotEmpty()) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(((ingredients.size / 2 + ingredients.size % 2) * 50).dp)
                    ) {
                        items(ingredients.size) { index ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = ingredients[index],
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Allergens Section
            food.details?.allergens?.let { allergens ->
                if (allergens.isNotEmpty()) {
                    Text(
                        text = "Allergens",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allergens.forEach { allergen ->
                            AssistChip(
                                onClick = { },
                                label = { Text(allergen) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.warning),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                                    labelColor = MaterialTheme.colorScheme.onErrorContainer,
                                    leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Nutrition Information (full details)
            if (showNutritionDetails && (food.nutrition?.calories != null || food.nutrition?.protein != null ||
                food.nutrition?.carbohydrates != null || food.nutrition?.fat != null)) {

                Text(
                    text = "Nutrition Information",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        food.nutrition?.servingSize?.let {
                            NutritionRow("Serving Size", it)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        food.nutrition?.calories?.let {
                            NutritionRow("Calories", "${it.toInt()} kcal")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        food.nutrition?.protein?.let {
                            NutritionRow("Protein", "${it}g")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        food.nutrition?.carbohydrates?.let {
                            NutritionRow("Carbohydrates", "${it}g")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }

                        food.nutrition?.fat?.let {
                            NutritionRow("Fat", "${it}g")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Created Date (shown in both views)
            food.createdAt?.let { createdAt ->
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                Text(
                    text = "Added on ${dateFormat.format(createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun InfoCard(
    icon: Int,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalSpacing = when (horizontalArrangement) {
            is Arrangement.HorizontalOrVertical -> horizontalArrangement.spacing.roundToPx()
            else -> 0
        }
        val verticalSpacing = when (verticalArrangement) {
            is Arrangement.HorizontalOrVertical -> verticalArrangement.spacing.roundToPx()
            else -> 0
        }
        val sequences = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val currentSequence = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentWidth = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (currentWidth + placeable.width + (if (currentSequence.isEmpty()) 0 else horizontalSpacing) > constraints.maxWidth) {
                sequences.add(currentSequence.toList())
                currentSequence.clear()
                currentWidth = 0
            }

            currentWidth += placeable.width + (if (currentSequence.isEmpty()) 0 else horizontalSpacing)
            currentSequence.add(placeable)
        }

        if (currentSequence.isNotEmpty()) {
            sequences.add(currentSequence)
        }

        val totalHeight = sequences.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 } +
                (sequences.size - 1).coerceAtLeast(0) * verticalSpacing

        layout(constraints.maxWidth, totalHeight) {
            var y = 0

            sequences.forEach { row ->
                var x = 0

                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + horizontalSpacing
                }

                y += row.maxOfOrNull { it.height } ?: 0
                y += verticalSpacing
            }
        }
    }
}
