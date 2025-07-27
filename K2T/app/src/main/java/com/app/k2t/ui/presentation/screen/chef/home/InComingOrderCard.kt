package com.app.k2t.ui.presentation.screen.chef.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.k2t.R
import com.app.k2t.firebase.model.OrderItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InComingOrderCard(
    modifier: Modifier = Modifier,
    item: OrderItem,
    onAcceptItem: () -> Unit
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    val gradientColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = !isPressed
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 16.dp
        ),
        colors = cardColors,
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            // Animated background pattern
            AnimatedBackgroundPattern(
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Food info section
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.restaurant),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.foodName ?: "Unknown Food",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    item.addedAt?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_access_time_filled_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = timeFormat.format(it),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Quantity badge
                QuantityBadge(
                    quantity = item.quantity ?: 1,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Accept button
                EnhancedAcceptButton(
                    onClick = onAcceptItem,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

@Composable
fun QuantityBadge(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quantity_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(pulseScale)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "×$quantity",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun EnhancedAcceptButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isClicked by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isClicked) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (isClicked)
            MaterialTheme.colorScheme.secondary else
            MaterialTheme.colorScheme.tertiary,
        animationSpec = tween(200),
        label = "button_color"
    )

    Button(
        onClick = {
            isClicked = true
            onClick()
        },
        modifier = modifier
            .scale(buttonScale)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Accept",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AnimatedBackgroundPattern(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_pattern")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(
        modifier = modifier.alpha(0.1f)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        rotate(rotation) {
            repeat(5) { i ->
                val radius = (i + 1) * 30f
                drawCircle(
                    color = Color.Blue,
                    radius = radius,
                    center = Offset(canvasWidth * 0.8f, canvasHeight * 0.3f)
                )
            }
        }
    }
}