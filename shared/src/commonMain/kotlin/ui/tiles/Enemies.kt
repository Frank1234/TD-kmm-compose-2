package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Enemy
import domain.model.getDrawableResId
import domain.model.getRelativeScreenCoordinates
import domain.model.getZIndex

@Composable
fun Enemies(enemies: List<Enemy>) {
    enemies.forEach { enemy ->
        Enemy(enemy)
    }
}

@Composable
fun Enemy(enemy: Enemy) {
    val relativeScreenCoordinates = enemy.getRelativeScreenCoordinates()

    AtLocationOf(
        enemy, zIndex = enemy.getZIndex(relativeScreenCoordinates)
    ) {

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .offset(x = relativeScreenCoordinates.x.dp, y = relativeScreenCoordinates.y.dp)
            ) {
                Image(
                    painter = painterResource(enemy.getDrawableResId()), contentDescription = "",
                    modifier = Modifier
                        .scale(scaleX = enemy.scaleX, enemy.scaleY)
                        .size(width = enemy.type.imageBasicWidth.dp, height = enemy.type.imageBasicHeight.dp)
                        .alpha(enemy.alpha)
                        .rotate(enemy.rotation)
                )
                if (enemy.health.showHealthBar) EnemyHealthBar(enemy)
            }
        }
    }
}