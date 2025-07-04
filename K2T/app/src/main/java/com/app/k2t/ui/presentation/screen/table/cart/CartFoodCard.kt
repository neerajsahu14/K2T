package com.app.k2t.ui.presentation.screen.table.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.k2t.R
import com.app.k2t.local.model.FoodInCart
import com.app.k2t.ui.presentation.screen.table.CartItem
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun CartItemCard(
    cartFood: FoodInCart,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartFood.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unit: ₹${String.format(Locale.getDefault(), "%.2f", cartFood.unitPrice ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Total: ₹${String.format(Locale.getDefault(), "%.2f", cartFood.totalPrice ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, enabled = (cartFood.quantity ?: 1) > 1) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_remove_24),
                        contentDescription = "Decrease"
                    )
                }
                Text(
                    text = (cartFood.quantity ?: 1).toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = onIncrease) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Preview(name = "CartFoodCard")
@Composable
private fun PreviewCartFoodCard() {
    CartItemCard(
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