package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.k2t.R
import com.app.k2t.firebase.model.Details
import com.app.k2t.firebase.model.Food
import com.app.k2t.local.model.FoodInCart
import com.app.k2t.ui.presentation.screen.shared.FoodDetailsContent
import com.app.k2t.ui.theme.K2TTheme
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import java.util.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableFoodDetailsScreen( // Renamed for clarity
    food: Food,
    cartViewModel: CartViewModel = koinViewModel(),
    foodViewModel: FoodViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onPlayVideo: () -> Unit = {}
) {
    var quantity by remember { mutableIntStateOf(1) }
    val scrollState = rememberScrollState()
    val isFoodInCart by cartViewModel.isFoodInCart(food.foodId ?: "").collectAsState(initial = false)
    val allFoodInCart by cartViewModel.allFoodInCart.collectAsState()
    val foodInCart = allFoodInCart.find { it.foodId == food.foodId }

    // Use a Box to allow overlaying the TopAppBar and controlling content padding
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Set background color for the whole screen
    ) {

        // Main content area, occupies the full space and handles scrolling
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }

            // Use the shared FoodDetailsContent
            // The contentPadding will be handled by the Box and the TopAppBar's height
            FoodDetailsContent(
                food = food,
                scrollState = scrollState,
                isAdminView = false,
                onPlayVideo = onPlayVideo,
                // We'll manually adjust padding for the top and bottom bars
                // Top padding will be the height of the TopAppBar
                // Bottom padding will be the height of the BottomBar
                // This is a simplified example; you might need more sophisticated padding logic
                // if your bars have dynamic heights or you need to account for insets.
                contentPadding = PaddingValues(top = 56.dp, bottom = 80.dp) // Approximate heights
            )
            // Quantity Selector is part of FoodDetailsContent or could be added here
        }


        // Bottom Bar - Aligned to the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align to the bottom of the Box
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background), // Add a background to the bottom bar if needed
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
}

@Preview(name = "TableFoodDetailsScreen", device = Devices.PIXEL_TABLET)
@Composable
private fun PreviewCustomerFoodDetailsScreen() {
    K2TTheme {
        TableFoodDetailsScreen( // Use the new composable for preview
            food = Food(
                foodId = "1",
                name = "Butter Chicken",
                details = Details(
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