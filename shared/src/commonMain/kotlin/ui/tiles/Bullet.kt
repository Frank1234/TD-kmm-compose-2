package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Enemy
import domain.model.Tower
import domain.model.ZIndex
import domain.model.getBulletDrawSpecs
import domain.model.getBulletPainterRes

@Composable
fun Bullet(tower: Tower, shootingTarget: Enemy) {
    AtLocationOf(tower, zIndex = ZIndex.Bullet.value) {

        val (x, y, rotationDegrees) = tower.getBulletDrawSpecs(shootingTarget)

        Image(
            painter = painterResource(tower.getBulletPainterRes()), contentDescription = "", modifier = Modifier
                .offset(x = x.dp, y = y.dp)
                .rotate(rotationDegrees)
        )
    }
}