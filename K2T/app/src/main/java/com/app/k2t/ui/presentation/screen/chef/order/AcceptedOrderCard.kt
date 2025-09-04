package com.app.k2t.ui.presentation.screen.chef.order

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.k2t.R
import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.utils.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AcceptedOrderCard(
    modifier: Modifier = Modifier,
    item: OrderItem,
    onItemCompleted: () -> Unit
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

    val isCompleted = item.statusCode == OrderStatus.COMPLETED.code

    val containerColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "container_color"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        animationSpec = tween(300),
        label = "border_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = !isPressed
            },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCompleted) 2.dp else 0.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated background for completed items
            if (isCompleted) {
                CompletedItemBackground(
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                            tint = if (isCompleted) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = item.foodName ?: "Unknown Food",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isCompleted) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    item.addedAt?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.schedule),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = timeFormat.format(it),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Quantity badge
                EnhancedQuantityBadge(
                    quantity = item.quantity ?: 1,
                    isCompleted = isCompleted,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Status button/indicator
                AnimatedContent(
                    targetState = item.statusCode,
                    label = "CompletionStatus"
                ) { statusCode ->
                    if (statusCode == OrderStatus.PREPARING.code) {
                        EnhancedDoneButton(
                            onClick = onItemCompleted,
                            modifier = Modifier.wrapContentWidth()
                        )
                    } else {
                        CompletedIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedQuantityBadge(
    quantity: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val badgeColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.tertiary,
        animationSpec = tween(300),
        label = "badge_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.onSecondary
        else MaterialTheme.colorScheme.onTertiary,
        animationSpec = tween(300),
        label = "text_color"
    )

    Surface(
        modifier = modifier
            .size(44.dp),
        shape = CircleShape,
        color = badgeColor,
        shadowElevation = if (isCompleted) 2.dp else 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×$quantity",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun EnhancedDoneButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        modifier = modifier
//            .scale(buttonScale)
            .height(40.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Mark as Done",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CompletedIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "completed_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = glowAlpha)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Completed",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CompletedItemBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "completed_bg")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Canvas(
        modifier = modifier.alpha(0.1f)
    ) {
        val canvasWidth = size.width

        val shimmerOffset = canvasWidth * shimmer

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Green.copy(alpha = 0.3f),
                ),
                startX = shimmerOffset - 200f,
                endX = shimmerOffset + 200f
            ),
            size = size
        )
    }
}
