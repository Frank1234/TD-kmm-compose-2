package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import domain.model.LocationTileOccupant

/**
 * Paints [content] at the location of [LocationTileOccupant].
 */
@Composable
fun AtLocationOf(
    locationTileOccupant: LocationTileOccupant,
    zIndex: Float = locationTileOccupant.locationTile.zIndex,
    setFixedSize: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {

    val screenCoordinates = locationTileOccupant.getScreenCoordinates()
    Box(
        modifier = Modifier
            .offset(
                x = screenCoordinates.x.dp,
                y = screenCoordinates.y.dp
            )
            .let {
                if (setFixedSize) it.size(
                    width = locationTileOccupant.getWidth().dp,
                    height = locationTileOccupant.getHeight().dp
                )
                else it
            }
            .zIndex(zIndex) // for correctly overlapping other tiles
    ) {
        content()
    }
}