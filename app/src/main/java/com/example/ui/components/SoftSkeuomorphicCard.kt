package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun SoftSkeuomorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 3.dp,
    highlightColor: Color = Color.White.copy(alpha = 0.85f),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier
            .shadow(elevation = elevation, shape = shape, spotColor = DeepNavy.copy(alpha = 0.12f))
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier
            .shadow(elevation = elevation, shape = shape, spotColor = DeepNavy.copy(alpha = 0.12f))
            .clip(shape)
    }

    Surface(
        modifier = clickableModifier,
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box {
            // Subtle top highlight sheen for tactile depth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                highlightColor.copy(alpha = 0.1f),
                                highlightColor,
                                highlightColor.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}
