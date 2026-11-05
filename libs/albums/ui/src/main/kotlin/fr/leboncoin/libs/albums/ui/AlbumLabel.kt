package fr.leboncoin.libs.albums.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.text.Text

/**
 * Looks like a Spark `ChipTinted`, which is always clickable or faded when disabled. This label
 * is not clickable and has no ripple, but it blocks the clicks: tapping it does not click the
 * parent, e.g. the card of an album.
 */
@Composable
fun AlbumLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .blockClicks()
            .heightIn(min = 32.dp)
            .background(SparkTheme.colors.basicContainer, SparkTheme.shapes.small)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SparkTheme.colors.onBasicContainer,
            style = SparkTheme.typography.body2,
        )
    }
}

/** Consumes only the press, so that a list can still be scrolled from this element. */
private fun Modifier.blockClicks(): Modifier = pointerInput(Unit) {
    awaitEachGesture { awaitFirstDown().consume() }
}

@Preview
@Composable
internal fun AlbumLabelPreview() {
    SparkTheme {
        AlbumLabel(text = "Album #1")
    }
}
