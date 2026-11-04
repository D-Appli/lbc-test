package fr.leboncoin.feature.details.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import fr.leboncoin.feature.details.presentation.DetailsUiState

@PreviewTest
@Preview(name = "Portrait", widthDp = 411, heightDp = 914)
@Preview(name = "Landscape", widthDp = 914, heightDp = 411)
@Composable
fun DetailsScreenshot(
    @PreviewParameter(DetailsUiStatePreviewProvider::class) uiState: DetailsUiState,
) {
    DetailsPreview(uiState)
}
