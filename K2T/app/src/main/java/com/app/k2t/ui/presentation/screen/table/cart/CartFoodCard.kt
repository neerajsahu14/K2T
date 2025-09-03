package com.app.k2t.ui.presentation.screen.table.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.k2t.R
import com.app.k2t.local.model.FoodInCart // Assuming this is your cart item model
import com.app.k2t.ui.theme.K2TTheme


@SuppressLint("DefaultLocale")
@Composable
fun CartFoodCard(
    cartFood: FoodInCart,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = cartFood.imageUrl,
                    contentDescription = cartFood.foodName,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartFood.foodName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        // Assuming unitPrice is the price for one item
                        text = "₹${String.format("%.2f", cartFood.unitPrice)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuantitySelector(
                        quantity = cartFood.quantity ?: 1,
                        onIncrease = { onIncrease(cartFood.foodId) },
                        onDecrease = { onDecrease(cartFood.foodId) }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "₹${String.format("%.2f", cartFood.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        // Delete Button - aligned to TopEnd of the Box
        IconButton(
            onClick = { onDelete(cartFood.foodId) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp) // Fine-tune padding
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f), shape = CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove item",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        QuantityButton(
            icon = R.drawable.baseline_remove_24,
            onClick = onDecrease,
            enabled = quantity > 1 // Decrease is disabled if quantity is 1 or less
        )
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(horizontal = 12.dp) // Increased padding
        )
        QuantityButton(
            icon = R.drawable.add,
            onClick = onIncrease
        )
    }
}

@Composable
private fun QuantityButton(
    icon: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp) // Slightly larger buttons
            .clip(CircleShape)
            .background(if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCartFoodCard() {
    K2TTheme(darkTheme = true) {
        CartFoodCard(
            cartFood = FoodInCart(
                foodId = "f1",
                foodName = "Cappuccino",
                quantity = 2,
                unitPrice = 26.0,
                totalPrice = 52.0,
                imageUrl = "https://via.placeholder.com/150" // Replace with a real placeholder if possible
            ),
            onIncrease = {},
            onDecrease = {},
            onDelete = {}
        )
    }
}