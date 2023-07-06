package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.myapplication.common.MR
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Event
import domain.model.Tower
import domain.model.getDrawableResId

@Composable
fun Towers(towers: List<Tower>, onEvent: (Event) -> Unit) {
    towers.forEach { tower ->
        TowerComposable(
            tower = tower,
            onEvent = onEvent
        )
    }
}

@Composable
private fun TowerComposable(tower: Tower, onEvent: (Event) -> Unit) {
    AtLocationOf(tower) {
        Image(
            painter = painterResource(tower.getDrawableResId()), contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onEvent(tower.getSelectedEvent()) }
        )
    }
    if (tower.isSelected) TowerControls(tower, onEvent)
    tower.shootingTarget?.let { Bullet(tower, it) }
}