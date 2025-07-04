package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.k2t.firebase.model.Details
import com.app.k2t.firebase.model.Food
import com.app.k2t.R
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.theme.K2TTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableFoodCard(
    food: Food,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAddToCart: (Food) -> Unit = {},
    cartViewModel: CartViewModel = viewModel()
) {
    val checkFoodInCart = cartViewModel.isFoodInCart(food.foodId ?: "").collectAsState(initial = false).value

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Food Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                if (food.imageUrl != null) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = food.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Category badge

            }

            // Content
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Food name and rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = food.name ?: "Unknown Dish",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "4.5",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    // Ingredients preview
                    food.details?.ingredients?.let { ingredients ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ingredients.take(2).joinToString(", ") +
                                    if (ingredients.size > 2) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Prep time and availability
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    food.details?.prepTime?.let { prepTime ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_access_time_filled_24),
                                contentDescription = "Prep Time",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = prepTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = if (food.availability == true) "Available" else "Sold Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (food.availability == true)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price and Add to Cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        food.price?.let { price ->
                            Text(
                                text = "₹${String.format(Locale.getDefault(), "%.0f", price)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "per serving",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onAddToCart(food) },
                        enabled = (food.availability == true && !checkFoodInCart),
                        modifier = Modifier
                            .height(36.dp)
                            .defaultMinSize(minWidth = 0.dp)
                    ) {
                        if (checkFoodInCart) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Added", style = MaterialTheme.typography.labelSmall)
                        } else {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Add", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "TableFoodCard" , showBackground = true)
@Composable
private fun PreviewCustomerFoodCard() {
    K2TTheme {
        LazyRow {
            item{
                TableFoodCard(
                    food = Food(
                        foodId = "1",
                        name = "Butter Chicken",
                        details = Details(
                            prepTime = "25 mins",
                            ingredients = listOf("Chicken", "Butter", "Tomato", "Cream", "Spices")
                        ),
                        price = 299.0,
                        availability = true,
                        imageUrl = null,
                        createdAt = Date()
                    )
                )
            }
            item{
                TableFoodCard(
                    food = Food(
                        foodId = "1",
                        name = "Butter Chicken",
                        details = Details(
                            prepTime = "25 mins",
                            ingredients = listOf("Chicken", "Butter", "Tomato", "Cream", "Spices")

                        ),
                        price = 299.0,
                        availability = true,
                        imageUrl = null,
                        createdAt = Date()
                    )
                )
            }
            item{
                TableFoodCard(
                    food = Food(
                        foodId = "1",
                        name = "Butter Chicken",
                        details = Details(
                            prepTime = "25 mins",
                            ingredients = listOf("Chicken", "Butter", "Tomato", "Cream", "Spices")
                        ),
                        price = 299.0,
                        availability = true,
                        imageUrl = null,
                        createdAt = Date()
                    )
                )
            }
        }
    }
}









class Chrome {

    fun open() {
        println("Chrome opened")
        showAd()
    }

    private fun showAd() {
        if (userIsUsingChrome()) {
            println("User already on Chrome? Show Chrome ad anyway 🔁")
            val updatedChrome = Chrome()
            updatedChrome.downloadAndUpdateChrome()
        }
    }
    fun userIsUsingChrome() : Boolean = true
    fun downloadAndUpdateChrome() {
        println("Downloading Chrome… using Chrome… to update Chrome 💽")
        val newChrome = Chrome()
        newChrome.open() // recursion begins
    }
}

fun main() {
    val chrome = Chrome()
    chrome.open()
}














