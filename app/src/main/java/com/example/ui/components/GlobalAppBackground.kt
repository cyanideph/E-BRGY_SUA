package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

/**
 * Shared full-screen application background.
 *
 * The app module packages the repository-level assets/background directory as
 * Android assets. The background is loaded from the exact approved WebP asset
 * so runtime discovery cannot select a different image.
 */
@Composable
fun GlobalAppBackground(
    modifier: Modifier = Modifier,
    scrimAlpha: Float = 0.22f,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val bitmap = produceState<Bitmap?>(initialValue = null, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val assetPath = "background/1791199555816.webp"

                val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.assets.open(assetPath).use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }

                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                    return@withContext null
                }

                val sample = max(
                    1,
                    max(bounds.outWidth, bounds.outHeight) / 2160
                )

                val options = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.RGB_565
                }

                context.assets.open(assetPath).use {
                    BitmapFactory.decodeStream(it, null, options)
                }
            }.getOrNull()
        }
    }.value

    val overlay = MaterialTheme.colorScheme.scrim.copy(alpha = scrimAlpha)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlay)
        )

        content()
    }
}
