package com.app.k2t.ui.presentation.screen.admin.food

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.k2t.firebase.model.Details
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import org.koin.androidx.compose.koinViewModel
import com.app.k2t.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFoodScreen(
    foodId: String?,
    onDismiss: () -> Unit,
) {
    val viewModel: FoodViewModel = koinViewModel()
    val food by viewModel.getFood(foodId ?: "").collectAsState(initial = null)
    val error by viewModel.error.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var videoUrl by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val scope = rememberCoroutineScope()

    // Update form fields when food data changes
    LaunchedEffect(food) {
        if (foodId != null && food == null) return@LaunchedEffect // Don't populate until loaded
        food?.let {
            name = it.name ?: ""
            price = it.price?.toString() ?: ""
            prepTime = it.details?.prepTime ?: ""
            ingredients = it.details?.ingredients?.joinToString(", ") ?: ""
            existingImageUrl = it.imageUrl
            videoUrl = it.videoUrl ?: ""
            availability = it.availability ?: true
        }
    }

    val isEditing = foodId != null
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isEditing) "Edit Food Item" else "Add New Food Item") 
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // No actions in the top bar
                actions = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Basic Information Section
            SectionHeader("Basic Information")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Food Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("₹") },
                supportingText = {
                    if (price.isNotBlank() && price.toDoubleOrNull() == null) {
                        Text("Please enter a valid price", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Details Section
            SectionHeader("Details")


            OutlinedTextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("Preparation Time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("e.g., 15 minutes") }
            )

            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Separate with commas") }
            )

            // Media Section
            SectionHeader("Media")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val imageModel = selectedImageUri ?: existingImageUrl
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Food Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.addphoto),
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to select an image",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Video URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Availability Section
            SectionHeader("Availability")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available for Ordering",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Toggle to make this item available to customers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = availability,
                        onCheckedChange = { availability = it },
                        thumbContent = if (availability) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
            }

            // Error message display
            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Save button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val buttonEnabled = name.isNotBlank() && price.toDoubleOrNull() != null && !isSaving
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.clearError() // Clear any previous errors
                            val foodToSave = Food(
                                foodId = foodId,
                                name = name.takeIf { it.isNotBlank() },
                                price = price.toDoubleOrNull(),
                                details = Details(
                                    prepTime = prepTime.takeIf { it.isNotBlank() },
                                    ingredients = ingredients.takeIf { it.isNotBlank() }
                                        ?.split(",")?.map { it.trim() }
                                ),
                                imageUrl = existingImageUrl,
                                videoUrl = videoUrl.takeIf { it.isNotBlank() },
                                availability = availability,
                                createdAt = food?.createdAt // Preserve original creation date
                            )
                            try {
                                viewModel.upsertFoodWithImage(foodToSave, selectedImageUri)
                                onDismiss() // Dismiss only on success
                            } catch (e: Exception) {
                                // Error is handled in the ViewModel and displayed in the UI
                            }
                        }
                    },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isEditing) "Update Food" else "Add Food",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}
