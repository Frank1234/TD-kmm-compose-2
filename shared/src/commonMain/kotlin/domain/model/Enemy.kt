package domain.model

import com.example.kotlinmultiplatformmobiletest.game.gameRandom
import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileHeightHalf
import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileWidthHalf
import domain.model.getDirectionMultiplierX
import domain.model.getDirectionMultiplierY
import kotlin.math.max

data class EnemyId(val id: Long)

data class Enemy(
    val id: EnemyId = EnemyId(gameRandom.nextLong()),
    override val locationTile: LocationTile,
    val stepTargetLocation: LocationTile? = null,
    val stepTargetFractionDone: Double = 0.0,
    val type: EnemyType,
    val health: Health,
    val adjustmentFromCenterOfTile: Float, // TODO rename/document
) : LocationTileOccupant {
    val stepTargetDirection = stepTargetLocation?.let { locationTile.getMovementDirection(it) }

    val isMovingWest = stepTargetDirection?.getDirectionMultiplierX() == -1
    val isDead = health.currentValue <= 0
    val canMove = !isDead

    val scaleX = if (isMovingWest) -1f else 1f // mirror image when walking west[
    val scaleY = 1f
    val alpha = if (isDead) 0.5f else 1.0f
    val rotation = if (isDead) 90f else 0f
}

data class Health(
    val startingValue: Float,
    val currentValue: Float,
) {
    val showHealthBar = startingValue != currentValue && currentValue > 0
    val currentHealthFraction = currentValue / startingValue
}

private val enemyImageZoomFactor = .55f // TODO move

sealed class EnemyType(
    val speed: Float,
    val coins: Int,
    val maxNumberOnTile: Int,
    val health: Float,
    val imageBasicWidth: Float,
    val imageBasicHeight: Float,
) {

    object Orc : EnemyType(
        speed = 1.0f, 10, maxNumberOnTile = 4, health = 94f,
        imageBasicWidth = 97f * enemyImageZoomFactor,
        imageBasicHeight = 87f * enemyImageZoomFactor
    )

    object Ogre : EnemyType(
        speed = 0.75f, 25, maxNumberOnTile = 3, health = 240f,
        imageBasicWidth = 129f * enemyImageZoomFactor,
        imageBasicHeight = 83 * enemyImageZoomFactor,
    )
}

fun Enemy.getZIndex(screenCoordinates: ScreenCoordinates): Float {
    val zIndexTile = max(locationTile.zIndex, stepTargetLocation?.zIndex ?: 0f)
    val zAxisExtraFromX = screenCoordinates.x / 100 // when > 1 units on one tile: overlap the others when you're further to the right
    val zAxisExtraWhenAlive = if (isDead) 0 else 1 // walk over dead enemies
    return (zIndexTile + zAxisExtraFromX + zAxisExtraWhenAlive).toFloat()
}

/**
 * Returns the enemy's location screen coordinates + their current step animation coordinates.
 */
fun Enemy.getGlobalScreenCoordinates(): ScreenCoordinates {

    val tileScreenCoordinates = getScreenCoordinates()
    val relativeEnemyScreenCoordinates = getRelativeScreenCoordinates()

    val currentGlobalX = tileScreenCoordinates.x + relativeEnemyScreenCoordinates.x
    val currentGlobalY = tileScreenCoordinates.y + relativeEnemyScreenCoordinates.y

    return ScreenCoordinates(currentGlobalX, currentGlobalY)
}

/**
 * Returns the enemy's location screen coordinates + their current step animation coordinates.
 */
fun Enemy.getGlobalScreenCoordinatesAtCenter(): ScreenCoordinates {

    val coordinates = getGlobalScreenCoordinates()
    val centerX = coordinates.x + (type.imageBasicWidth / 2)
    val centerY = coordinates.y + (type.imageBasicHeight / 2)

    return ScreenCoordinates(centerX, centerY)
}

/**
 *  Returns x,y positions relative to [enemy.position] (set in AtLocationOf).
 */
fun Enemy.getRelativeScreenCoordinates(pivotPointIsTopLeft: Boolean = true): ScreenCoordinates {

    val baseX = tileWidthHalf - (type.imageBasicWidth / 2) + (type.imageBasicWidth * adjustmentFromCenterOfTile)
    val baseY =
        tileHeightHalf - (type.imageBasicHeight) + (type.imageBasicWidth * adjustmentFromCenterOfTile) // enemyHeight because feet should be in the center of the tile
    val directionMultiplierX = stepTargetDirection?.getDirectionMultiplierX() ?: 1
    val directionMultiplierY = stepTargetDirection?.getDirectionMultiplierY() ?: 1
    val currentX = baseX + (stepTargetFractionDone * tileWidthHalf * directionMultiplierX)
    val currentY = baseY + (stepTargetFractionDone * tileHeightHalf * directionMultiplierY)
    val platFormX =
        if (pivotPointIsTopLeft) currentX
        else currentX + (type.imageBasicWidth / 2)
    val platFormY =
        if (pivotPointIsTopLeft) currentY
        else currentY + (type.imageBasicHeight / 2)

    return ScreenCoordinates(platFormX, platFormY)
}

fun Enemy.didReachFinishTile(roads: Roads): Boolean =
    locationTile == roads.value.last().locationTile

fun Enemy.getDrawableResId(): String =
    when (this.type) {
        EnemyType.Ogre -> "enemy_ogre"
        EnemyType.Orc -> "enemy_orc"
    }