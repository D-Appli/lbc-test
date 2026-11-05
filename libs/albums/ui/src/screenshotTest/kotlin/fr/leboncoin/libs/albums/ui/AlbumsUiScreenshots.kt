package fr.leboncoin.libs.albums.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import fr.leboncoin.core.domain.model.Album

@PreviewTest
@Preview(name = "Default", showBackground = true)
@Preview(name = "LargeFont", showBackground = true, fontScale = 1.5f)
@Composable
fun AlbumItemScreenshot(
    @PreviewParameter(AlbumPreviewProvider::class) album: Album,
) {
    AlbumItemPreview(album)
}

@PreviewTest
@Preview(name = "Default", showBackground = true)
@Preview(name = "LargeFont", showBackground = true, fontScale = 1.5f)
@Composable
fun AlbumLabelScreenshot() {
    AlbumLabelPreview()
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AlbumImagePlaceholderScreenshot() {
    AlbumImagePlaceholderPreview()
}
