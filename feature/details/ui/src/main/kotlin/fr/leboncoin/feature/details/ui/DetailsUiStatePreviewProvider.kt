package fr.leboncoin.feature.details.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.details.presentation.DetailsUiState

internal class DetailsUiStatePreviewProvider : PreviewParameterProvider<DetailsUiState> {
    override val values: Sequence<DetailsUiState> = sequenceOf(
        DetailsUiState.Loading,
        DetailsUiState.Error,
        DetailsUiState.Success(
            Album(
                id = 3,
                albumId = 1,
                title = "officia porro iure quia iusto qui ipsa ut modi",
                url = "",
                thumbnailUrl = "",
                isFavorite = true,
            ),
        ),
    )
}
