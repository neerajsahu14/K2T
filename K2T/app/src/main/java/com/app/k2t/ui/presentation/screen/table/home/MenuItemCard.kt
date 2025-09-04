package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.k2t.firebase.model.Food
import java.util.Locale

@Composable
fun MenuItemCard(
    food: Food,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit,
    isItemInCart: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Food Image
            AsyncImage(
                model = food.imageUrl ?: "https://via.placeholder.com/100x100/E76F51/FFFFFF?Text=Food",
                contentDescription = food.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Food Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = food.name ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        // Veg/Non-Veg Indicator
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = if (food.isVeg == true) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        color = if (food.isVeg == true) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        shape = RoundedCornerShape(if (food.isVeg == true) 4.dp else 0.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    food.details?.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    food.details?.ingredients?.let { ingredients ->
                        if (ingredients.isNotEmpty()) {
                            Text(
                                text = "Contains: ${ingredients.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price and Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${String.format(Locale.getDefault(), "%.0f", food.price ?: 0.0)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )

                    FilledTonalButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(width = 80.dp, height = 32.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isItemInCart)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isItemInCart)
                                MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isItemInCart) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (isItemInCart) "Added" else "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isItemInCart) "Added" else "Add",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

