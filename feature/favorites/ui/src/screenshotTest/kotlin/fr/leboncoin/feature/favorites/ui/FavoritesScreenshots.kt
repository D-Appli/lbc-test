package fr.leboncoin.feature.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import fr.leboncoin.feature.favorites.presentation.FavoritesUiState

@PreviewTest
@Preview(widthDp = 411, heightDp = 914)
@Composable
fun FavoritesScreenshot(
    @PreviewParameter(FavoritesUiStatePreviewProvider::class) uiState: FavoritesUiState,
) {
    FavoritesPreview(uiState)
}
