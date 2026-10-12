package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// SUA SECTIONS V2 — non-card primitives so screens stop wrapping everything
// in SoftSkeuomorphicCard.
//
// Rule of thumb:
// - Lists & details  -> SuaPlainRow / SuaGroupedSection + SuaKeyValueRow
// - Section titles   -> SuaSectionHeader (already in SuaDesignSystem)
// - Keep cards ONLY for: QR, resident ID, SOS sheet, media thumbnails.
// ============================================================================

/**
 * Plain section container — NO surface, NO border. Just title + content
 * with consistent spacing. Use for Home blocks, detail headers, timelines.
 */
@Composable
fun SuaPlainSection(
    title: String? = null,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            SuaSectionHeader(
                title = title,
                subtitle = subtitle,
                actionLabel = actionLabel,
                onActionClick = onActionClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        content()
    }
}

/**
 * Grouped detail container — ONE surface for a set of rows with dividers
 * between them (iOS-settings style). Replaces stacks of 3-4 separate cards
 * on details screens (RequestDetails, ServiceDetails, AnnouncementDetails).
 */
@Composable
fun SuaGroupedSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                content = content
            )
        }
    }
}

/**
 * Plain tappable row — NO card, NO border. Leading slot + title/subtitle/
 * meta + trailing slot. Dividers are drawn by the parent list, not the row.
 */
@Composable
fun SuaPlainRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    meta: String? = null,
    testTag: String? = null,
    onClick: (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    badge: @Composable (() -> Unit)? = null
) {
    var rowModifier = modifier.fillMaxWidth()
    if (testTag != null) rowModifier = rowModifier.testTag(testTag)
    if (onClick != null) {
        rowModifier = rowModifier.clickable(role = Role.Button) { onClick() }
    }
    Row(
        modifier = rowModifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    badge()
                }
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (meta != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meta,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        } else if (onClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Divider used between plain rows — single canonical style. */
@Composable
fun SuaRowDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = 66.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

/**
 * Key-value detail row for grouped sections. No card — just stacked
 * label/value pairs separated by the parent.
 */
@Composable
fun SuaKeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifEmpty { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }
}

/**
 * Borderless quick-service tile for Home — image thumb + two-line text,
 * NO Surface wrapper. Keeps the 2x2 grid scannable without 4 mini-cards.
 */
@Composable
fun SuaQuickServiceTile(
    name: String,
    meta: String,
    thumbnailRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button) { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = thumbnailRes),
            contentDescription = name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = meta,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
