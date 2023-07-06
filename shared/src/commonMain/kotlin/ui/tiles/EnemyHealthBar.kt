package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.Enemy

@Composable
fun EnemyHealthBar(enemy: Enemy) {
    Box(
        modifier = Modifier
            .width((enemy.type.imageBasicWidth - 8).dp)
            .height(5.dp)
            .background(color = Color.DarkGray.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth(enemy.health.currentHealthFraction)
                .fillMaxHeight()
        )
    }
}