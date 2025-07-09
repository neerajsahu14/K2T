package com.app.k2t.ui.presentation.screen.table.cart

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.k2t.R
import com.app.k2t.local.model.FoodInCart
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun CartFoodCard(
    cartFood: FoodInCart,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var deletePressed by remember { mutableStateOf(false) }
    var increasePressed by remember { mutableStateOf(false) }
    var decreasePressed by remember { mutableStateOf(false) }

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val deleteScale by animateFloatAsState(
        targetValue = if (deletePressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val increaseScale by animateFloatAsState(
        targetValue = if (increasePressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val decreaseScale by animateFloatAsState(
        targetValue = if (decreasePressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
            )
            .scale(cardScale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // Subtle accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced food image with shimmer effect
                cartFood.imageUrl?.let { imageUrl ->
                    Surface(
                        modifier = Modifier
                            .size(85.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        tonalElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = cartFood.foodName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Enhanced food information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cartFood.foodName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Unit price with enhanced styling
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Unit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", cartFood.unitPrice ?: 0.0)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Enhanced total price
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%.2f", cartFood.totalPrice ?: 0.0)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Enhanced quantity controls
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Enhanced delete button
                    Surface(
                        onClick = {
                            deletePressed = true
                            onDelete()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .scale(deleteScale),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Enhanced quantity adjustment surface
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shadowElevation = 6.dp,
                        modifier = Modifier.background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surfaceContainer
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            // Decrease button
                            Surface(
                                onClick = {
                                    decreasePressed = true
                                    onDecrease()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(decreaseScale),
                                shape = RoundedCornerShape(12.dp),
                                color = if ((cartFood.quantity ?: 1) > 1)
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                else
                                    MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_remove_24),
                                        contentDescription = "Decrease",
                                        modifier = Modifier.size(18.dp),
                                        tint = if ((cartFood.quantity ?: 1) > 1)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Quantity display with enhanced styling
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainerHighest,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (cartFood.quantity ?: 1).toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 16.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Increase button
                            Surface(
                                onClick = {
                                    increasePressed = true
                                    onIncrease()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(increaseScale),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Increase",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Animated bottom accent
            androidx.compose.animation.AnimatedVisibility(
                visible = (cartFood.quantity ?: 0) > 0, // Only show if item is in cart
                enter = fadeIn(animationSpec = tween(1000)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }

    // Reset button press states
    LaunchedEffect(deletePressed) {
        if (deletePressed) {
            kotlinx.coroutines.delay(100)
            deletePressed = false
        }
    }

    LaunchedEffect(increasePressed) {
        if (increasePressed) {
            kotlinx.coroutines.delay(100)
            increasePressed = false
        }
    }

    LaunchedEffect(decreasePressed) {
        if (decreasePressed) {
            kotlinx.coroutines.delay(100)
            decreasePressed = false
        }
    }
}

@Preview(name = "CartFoodCard")
@Composable
private fun PreviewCartFoodCard() {
    CartFoodCard(
        cartFood = FoodInCart(
            foodId = "1",
            foodName = "Pizza Margherita",
            quantity = 2,
            unitPrice = 8.5,
            totalPrice = 17.0,
            imageUrl = null
        ),
        onIncrease = {},
        onDecrease = {},
        onDelete = {}
    )
}