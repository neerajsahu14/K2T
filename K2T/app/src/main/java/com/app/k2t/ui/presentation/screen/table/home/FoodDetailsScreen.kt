package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.k2t.R
import com.app.k2t.firebase.model.Details
import com.app.k2t.firebase.model.Food
import com.app.k2t.local.model.FoodInCart
import com.app.k2t.ui.theme.K2TTheme
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import java.text.SimpleDateFormat
import java.util.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableFoodDetailsScreen(
    food: Food,
    cartViewModel: CartViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAddToCart: (Int) -> Unit = {},
    onPlayVideo: () -> Unit = {}
) {
    var quantity by remember { mutableIntStateOf(1) }
    val scrollState = rememberScrollState()
    val isFoodInCart by cartViewModel.isFoodInCart(food.foodId ?: "").collectAsState(initial = false)
    val allFoodInCart by cartViewModel.allFoodInCart.collectAsState()
    val foodInCart = allFoodInCart.find { it.foodId == food.foodId }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Make TopAppBar transparent
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // Add to Cart Bottom Bar
            Box(
                modifier = Modifier.widthIn(max = 840.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total: ₹${String.format(Locale.getDefault(),"%.0f", (food.price ?: 0.0) * quantity)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$quantity × ₹${String.format(Locale.getDefault(),"%.0f", food.price ?: 0.0)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            if (isFoodInCart && foodInCart != null) {
                                val newQuantity = (foodInCart.quantity ?: 0) + quantity
                                val updatedFoodInCart = foodInCart.copy(quantity = newQuantity)
                                cartViewModel.updateFood(updatedFoodInCart)
                            } else {
                                val newFoodInCart = FoodInCart(
                                    foodId = food.foodId ?: "",
                                    quantity = quantity,
                                    foodName = food.name.toString(),
                                    unitPrice = food.price,
                                    totalPrice = food.price?.times(quantity),
                                    imageUrl = food.imageUrl
                                )
                                cartViewModel.insertFood(newFoodInCart)
                            }
                        },
                        enabled = food.availability == true,
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isFoodInCart) "Add More" else "Add to Cart")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
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


                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format(Locale.getDefault(),"%.0f", food.price ?: 0.0)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        // Mock rating
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

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Info Cards
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    food.details?.prepTime?.let { prepTime ->
                        item {
                            InfoCard(
                                    icon=  R.drawable.baseline_access_time_filled_24,
                                    title = "Prep Time",
                                    value = prepTime
                            )
                        }
                    }


                    item {
                        InfoCard(
                            icon = R.drawable.baseline_local_fire_department_24,
                            title = "Spice Level",
                            value = "Medium" // You can add this to your data model
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ingredients Section
                food.details?.ingredients?.let { ingredients ->
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quantity Selector
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1
                    ) {
                        Icon(painter = painterResource(R.drawable.baseline_remove_24), contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { quantity++ }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Created Date (Optional)
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
}

@Composable
private fun InfoCard(
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

@Preview(name = "TableFoodDetailsScreen", device = Devices.PIXEL_TABLET)
@Composable
private fun PreviewCustomerFoodDetailsScreen() {
    K2TTheme {
        TableFoodDetailsScreen(
            food = Food(
                foodId = "1",
                name = "Butter Chicken",
                details = Details(
                    prepTime = "25 mins",
                    ingredients = listOf(
                        "Chicken",
                        "Butter",
                        "Tomato",
                        "Cream",
                        "Spices",
                        "Onions"
                    )
                ),
                price = 299.0,
                availability = true,
                createdAt = Date()
            )
        )
    }
}