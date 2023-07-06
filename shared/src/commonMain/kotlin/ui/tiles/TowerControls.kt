package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.common.MR
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Event
import domain.model.LocationTile
import domain.model.Tower
import domain.model.TowerId
import domain.model.TowerLevel
import domain.model.TowerType
import domain.model.ZIndex
import domain.model.getNextLevel
import ui.common.TextWithShadow

@Composable
fun TowerControls(tower: Tower, onEvent: (Event) -> Unit) {
    AtLocationOf(tower, zIndex = ZIndex.TowerControls.value, setFixedSize = false) {
        Box(
            modifier = Modifier
                .offset(x = tower.controlsOffSetX.dp, y = tower.controlsOffSetY.dp),
        ) {
            Row(
                Modifier
                    .background(
                        color = Color(tower.controlsBackgroundColor).copy(alpha = tower.controlsBackgroundOpacity),
                        shape = RoundedCornerShape(tower.controlsRoundedCorners.dp)
                    )
                    .padding(end = tower.controlsPaddingEnd.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tower.canConstructOrUpgrade) {
                    ConstructOrUpdateButton(tower, onEvent)

                    val specs = TowerType.Archer.levelSpecs[tower.getNextLevel()]
                    Column {
                        Label(
                            specs?.constructionPrice.toString(), painterResource(MR.images.ic_coin),
                        )
                        Label(
                            specs?.attackDamage.toString(),
                            painterResource(MR.images.ic_arrow),
                            colorFilter = ColorFilter.tint(color = Color(tower.controlsLabelAttackColorFilter))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(message: String, painter: Painter, colorFilter: ColorFilter? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painter, contentDescription = "",
            modifier = Modifier.size(18.dp),
            colorFilter = colorFilter,
        )
        TextWithShadow(
            modifier = Modifier
                .offset(y = (-4).dp)
                .padding(start = 8.dp),
            text = message,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun ConstructOrUpdateButton(tower: Tower, onEvent: (Event) -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(MR.images.button_small_green), contentDescription = "",
            modifier = Modifier
                .clickable {
                    // we only support constructing archer towers for now:
                    onEvent(tower.getConstructButtonPressedEvent())
                }
                .alpha(tower.constructionButtonAlpha)
        )
        Image(
            painter = painterResource(MR.images.ic_arrow), contentDescription = "",
            modifier = Modifier
                .size(tower.controlButtonSize.dp)
                .padding(tower.controlButtonPadding.dp)
                .alpha(tower.controlButtonIconAlpha)
                .align(Alignment.Center),
            colorFilter = ColorFilter.tint(color = Color(tower.controlButtonColorFilter)),
        )
    }
}

@Composable
fun TowerControlsPreview() {
    TowerControls(Tower(
        id = TowerId(id = 0),
        locationTile = LocationTile(x = 0, y = 0),
        type = TowerType.Empty,
        shootingTarget = null,
        shootingTargetId = null,
        shootingFractionDone = 0.0,
        isSelected = false,
        level = TowerLevel.One,
        constructionButtonAlpha = 0.0f
    ), { })
}