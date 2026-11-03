package fr.leboncoin.feature.favorites.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.adevinta.spark.components.image.Illustration
import fr.leboncoin.feature.favorites.presentation.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
) {
    Illustration(
        modifier = modifier.fillMaxSize(),
        painter = painterResource(id = R.drawable.work_in_progress),
        contentDescription = null,
        contentScale = ContentScale.Inside,
    )
}
