package fr.leboncoin.libs.albums.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.icons.Icon
import com.adevinta.spark.icons.LikeFill
import com.adevinta.spark.icons.SparkIcons
import com.adevinta.spark.tokens.dim3
import kotlinx.coroutines.launch

/**
 * Pops and wiggles when the album becomes a favorite, and deflates when it no longer is one.
 * Only a change plays the animation, not the first display, e.g. when scrolling to the album.
 */
@Composable
internal fun AlbumFavoriteIcon(
    isFavorite: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (isFavorite) SparkTheme.colors.error else SparkTheme.colors.onSurface.dim3,
        animationSpec = tween(durationMillis = if (isFavorite) 150 else 300),
        label = "favorite tint",
    )
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    var animatedState by remember { mutableStateOf(isFavorite) }

    LaunchedEffect(isFavorite) {
        if (isFavorite == animatedState) return@LaunchedEffect
        animatedState = isFavorite
        launch { scale.animateTo(1f, if (isFavorite) PopScale else DeflateScale) }
        // A heart removed in the middle of the wiggle simply straightens up.
        rotation.animateTo(0f, if (isFavorite) PopRotation else tween(durationMillis = 150))
    }

    Icon(
        sparkIcon = SparkIcons.LikeFill,
        contentDescription = contentDescription,
        modifier = modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            rotationZ = rotation.value
        },
        tint = tint,
    )
}

private val PopScale: KeyframesSpec<Float> = keyframes {
    durationMillis = 500
    0.7f at 80 using FastOutSlowInEasing
    1.4f at 220 using FastOutSlowInEasing
    0.9f at 340 using FastOutSlowInEasing
    1.08f at 420 using FastOutSlowInEasing
}

private val PopRotation: KeyframesSpec<Float> = keyframes {
    durationMillis = 500
    -16f at 130 using FastOutSlowInEasing
    14f at 260 using FastOutSlowInEasing
    -7f at 370 using FastOutSlowInEasing
    3f at 440 using FastOutSlowInEasing
}

private val DeflateScale: KeyframesSpec<Float> = keyframes {
    durationMillis = 400
    1.12f at 70 using FastOutSlowInEasing
    0.6f at 230 using FastOutSlowInEasing
}
