package com.example.kotlinmultiplatformmobiletest.android

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlinmultiplatformmobiletest.android.tiles.Decorations
import com.example.kotlinmultiplatformmobiletest.android.tiles.Enemies
import com.example.kotlinmultiplatformmobiletest.android.tiles.Roads
import com.example.kotlinmultiplatformmobiletest.android.tiles.Towers
import domain.model.Event
import domain.model.Game
import domain.model.GameSizeSpecs.mapHeight
import domain.model.GameSizeSpecs.mapWidth

@Composable
fun GameBoard(
    bgColor: Color,
    game: Game,
    onEvent: (Event) -> Unit
) {
    Box(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
            .width(mapWidth.dp)
            .height(mapHeight.dp)
            .background(bgColor)
    ) {
        // if (SHOW_DEBUG_INFO) DebugTileInfo()
        Decorations(game.decorations)
        Roads(game.roads)
        Towers(game.towers, onEvent)
        Enemies(game.enemies)
    }
}