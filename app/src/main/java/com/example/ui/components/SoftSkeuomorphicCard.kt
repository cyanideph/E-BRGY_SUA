package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared content surface — Design System V2.
 * M3-native: surfaceContainerLow fill, outlineVariant border, 1dp tonal lift.
 * Keeps the existing API so all screens keep working.
 */
@Composable
fun SoftSkeuomorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    elevation: Dp = 1.dp,
    highlightColor: Color = Color.Transparent,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
    testTag: String? = null,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var surfaceModifier = modifier
    if (testTag != null) surfaceModifier = surfaceModifier.testTag(testTag)
    if (onClick != null) surfaceModifier = surfaceModifier.clickable { onClick.invoke() }

    Surface(
        modifier = surfaceModifier,
        shape = shape,
        color = backgroundColor,
        tonalElevation = elevation,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
