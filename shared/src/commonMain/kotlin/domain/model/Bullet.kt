package domain.model

import domain.model.GameSizeSpecs.tileWidthHalf
import kotlin.math.PI
import kotlin.math.atan2

data class BulletDrawSpecs(
    val x: Double,
    val y: Double,
    val rotationDegrees: Float,
)

fun Tower.getBulletDrawSpecs(shootingTarget: Enemy, pivotPointIsTopLeft: Boolean = true): BulletDrawSpecs {
    val towerGlobalCoordinates = getScreenCoordinates()
    val imageBasicWidth = 51
    val imageBasicHeight = 19

    val baseX = tileWidthHalf
    val baseY = 44 // the exact the location of the tower's gun
    val platformBaseX = if (pivotPointIsTopLeft) tileWidthHalf else tileWidthHalf - (getWidth() / 2)
    val platformBaseY = if (pivotPointIsTopLeft) 44.0 else 44.0 - (getHeight() / 2)

    val bulletStartingPointX = towerGlobalCoordinates.x + baseX.toDouble()
    val bulletStartingPointY =
        towerGlobalCoordinates.y + baseY

    val enemyGlobalCoordinates =
        shootingTarget.getGlobalScreenCoordinatesAtCenter().let {
            // location where it 'hits' the target:
            ScreenCoordinates(
                it.x - (shootingTarget.type.imageBasicWidth / if (pivotPointIsTopLeft) 4 else 2),
                it.y - (shootingTarget.type.imageBasicHeight / if (pivotPointIsTopLeft) 4 else 2)
            )
        }

    val deltaX = enemyGlobalCoordinates.x - bulletStartingPointX.toDouble()
    val deltaY = enemyGlobalCoordinates.y - bulletStartingPointY
    val currentBulletX = deltaX * shootingFractionDone
    val currentBulletY = deltaY * shootingFractionDone

    val rotationDegrees = (atan2(deltaY, deltaX) * 180 / PI).toFloat()

    val platFormX =
        if (pivotPointIsTopLeft) platformBaseX + currentBulletX
        else platformBaseX + currentBulletX + (imageBasicWidth / 2)
    val platFormY =
        if (pivotPointIsTopLeft) platformBaseY + currentBulletY
        else platformBaseY + currentBulletY + (imageBasicHeight / 2)

    return BulletDrawSpecs(platFormX, platFormY, rotationDegrees)
}
