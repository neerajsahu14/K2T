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
    // Remove isPressed and cardScale for simplicity
    var deletePressed by remember { mutableStateOf(false) }
    var increasePressed by remember { mutableStateOf(false) }
    var decreasePressed by remember { mutableStateOf(false) }

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
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            cartFood.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = cartFood.foodName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartFood.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unit: ₹${String.format(Locale.getDefault(), "%.2f", cartFood.unitPrice ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total: ₹${String.format(Locale.getDefault(), "%.2f", cartFood.totalPrice ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = {
                        deletePressed = true
                        onDelete()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .scale(deleteScale)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            decreasePressed = true
                            onDecrease()
                        },
                        enabled = (cartFood.quantity ?: 1) > 1,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(decreaseScale)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_remove_24),
                            contentDescription = "Decrease",
                            tint = if ((cartFood.quantity ?: 1) > 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    Text(
                        text = (cartFood.quantity ?: 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = {
                            increasePressed = true
                            onIncrease()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .scale(increaseScale)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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