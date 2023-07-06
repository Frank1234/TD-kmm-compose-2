package domain.model

import domain.game.gameRandom

data class TowerId(val id: Long)

data class Tower(
    val id: TowerId = TowerId(gameRandom.nextLong()),
    override val locationTile: LocationTile,
    val type: TowerType,
    val shootingTarget: Enemy? = null,
    val shootingTargetId: EnemyId? = null,
    val shootingFractionDone: Double = 0.0,
    val isSelected: Boolean = false,
    val level: TowerLevel = TowerLevel.One,
    val constructionButtonAlpha: Float = 1f,
) : LocationTileOccupant {
    val canShoot = type != TowerType.Empty

    val canConstructOrUpgrade = type == TowerType.Empty || level.hasNextLevel()
    fun getLevelSpecs() = type.levelSpecs[level]
    fun getAttackDamage() = type.levelSpecs[level]?.attackDamage ?: 0
    fun getSelectedEvent() = Event.Tower.Selected(id)
    fun getConstructButtonPressedEvent() = Event.Tower.ConstructButtonPressed(id, TowerType.Archer)
    fun getNextLevelSpecs() = TowerType.Archer.levelSpecs[getNextLevel()]

    // TODO static viewstate with this?
    val controlsOffSetX: Float = 3f
    val controlsOffSetY: Float = -24f
    val controlsPaddingEnd: Float = 12f
    val controlsBackgroundColor: Long = 0xFF000000
    val controlsBackgroundOpacity: Float = .2f
    val controlsLabelAttackColorFilter: Long = 0xFFFFC107
    val controlsRoundedCorners: Float = 20f
    val controlButtonSize: Float = 48f
    val controlButtonPadding: Float = 6f
    val controlButtonIconAlpha: Float = 0.85f
    val controlButtonColorFilter: Long = 0xFFFFFFFF
}

fun Tower.getConstructionButtonAlpha(constructionTowerType: TowerType, availableCoins: Int) =
    if (canFundConstruction(constructionTowerType, availableCoins)) 1f else 0.4f

fun Tower.canFundConstruction(constructionTowerType: TowerType, availableCoins: Int): Boolean =
    getConstructionPrice(constructionTowerType) <= availableCoins

fun Tower.getConstructIconPainterRes() =
    if (type == TowerType.Empty) {
        "ic_arrow"
    } else {
        when (level) {
            TowerLevel.One -> "ic_arrow_2"
            else -> "ic_arrow_3"
        }
    }

fun Tower.getBulletPainterRes(): String =
    when (this.level) {
        is TowerLevel.One -> "shoot_arrow_level_1"
        is TowerLevel.Two -> "shoot_arrow_level_2"
    }

sealed class TowerLevel {
    object One : TowerLevel()
    object Two : TowerLevel()

    fun getNextLevel() =
        when (this) {
            is One -> Two
            else -> Two // we support only levels one and two for now
        }

    fun hasNextLevel() = this != Two
}

fun Tower.getNextLevel() =
    when (type) {
        is TowerType.Empty -> TowerLevel.One
        else -> when (level) {
            TowerLevel.One -> TowerLevel.Two
            else -> TowerLevel.Two // we support only levels one and two for now
        }
    }

sealed class TowerType(
    val levelSpecs: Map<TowerLevel, TowerTypeLevelSpecs>,
) {
    object Empty : TowerType(levelSpecs = mapOf())
    object Archer : TowerType(
        levelSpecs = mapOf(
            TowerLevel.One to TowerTypeLevelSpecs(50, 10),
            TowerLevel.Two to TowerTypeLevelSpecs(80, 26),
        )
    )
}

// TODO better
fun Tower.getConstructionPrice(constructionTowerType: TowerType) =
    if (type == TowerType.Empty) constructionTowerType.getLevelOneConstructionPrice() ?: 0
    else constructionTowerType.levelSpecs[level.getNextLevel()]?.constructionPrice ?: 0

fun TowerType.getLevelOneConstructionPrice() = levelSpecs[TowerLevel.One]?.constructionPrice

data class TowerTypeLevelSpecs(
    val constructionPrice: Int, // TODO value class
    val attackDamage: Int, // TODO value class
)

fun Tower.isInRange(enemy: Enemy): Boolean {
    val locationsInRange = locationTile.getLocationsWithinShootingRange()
    return locationsInRange.contains(enemy.stepTargetLocation)
}

fun Tower.getDrawableResId(): String =
    when (this.type) {
        is TowerType.Empty -> "tower_empty"
        is TowerType.Archer -> {
            when (this.level) {
                TowerLevel.One -> "tower_archer_level_one"
                TowerLevel.Two -> "tower_archer_level_two"
            }
        }
    }