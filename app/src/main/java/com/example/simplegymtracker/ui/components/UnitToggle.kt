package com.example.simplegymtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simplegymtracker.ui.theme.ElectricBlue

@Composable
fun UnitToggle(
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    unitType: String = "weight"
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        val units = when (unitType) {
            "distance" -> listOf("km", "mi")
            else -> listOf("kg", "lb")
        }
        units.forEach { unit ->
            val isSelected = selectedUnit == unit
            Text(
                text = unit,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onUnitChange(unit) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
