package fr.leboncoin.feature.albums.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import fr.leboncoin.feature.albums.presentation.AlbumsUiState

@PreviewTest
@Preview(widthDp = 411, heightDp = 914)
@Composable
fun AlbumsScreenshot() {
    AlbumsPreview()
}

@PreviewTest
@Preview(widthDp = 411, heightDp = 914)
@Composable
fun AlbumsMessageScreenshot(
    @PreviewParameter(AlbumsUiStatePreviewProvider::class) uiState: AlbumsUiState,
) {
    AlbumsMessagePreview(uiState)
}
