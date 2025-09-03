package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Re-defining colors here for standalone use, or import from a central Theme.kt
val TextColorPrimaryChip = Color(0xFFF5EFE6)
val AccentColorOrangeChip = Color(0xFFE57905)
val ChipBackgroundUnselected = Color(0xFF503F3B) // Darker, less saturated than card bg
val DarkTextOnOrangeChip = Color(0xFF251C1A)


data class CategoryItem(val id: String, val name: String) // Simple data class for category

@Composable
fun CategoryChip(
    categoryItem: CategoryItem,
    isSelected: Boolean,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) AccentColorOrangeChip else ChipBackgroundUnselected)
            .clickable { onCategorySelected(categoryItem.id) }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = categoryItem.name,
            color = if (isSelected) DarkTextOnOrangeChip else TextColorPrimaryChip,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
