package domain.model

import domain.model.GameSizeSpecs.gameHeightTiles
import domain.model.GameSizeSpecs.gameWidthTiles

/**
 * [x] and [y] are tile indexes.
 */
data class LocationTile(val x: Int, val y: Int) {
    /**
     * zIndex for rendering on top of tiles to the north this one.
     */
    val zIndex = y * 10f
}

/**
 * Something that can be placed on a LocationTile. Like a tower, enemy, etc.
 */
interface LocationTileOccupant {
    val locationTile: LocationTile

    fun getScreenCoordinates(): ScreenCoordinates =
        getScreenCoordinates(
            locationTileX = locationTile.x,
            locationTileY = locationTile.y,
            zAxisFraction = getZAxisFraction(),
        )

    fun getWidth() = GameSizeSpecs.tileWidth
    fun getHeight() = GameSizeSpecs.tileHeight + getZAxisFraction() * GameSizeSpecs.tileHeight
}

fun isOnBoard(location: LocationTile) =
    location.x >= 0 && location.y >= 0 && location.x <= gameWidthTiles && location.y <= gameHeightTiles

/**
 * Returns the fraction of tiles to overlap the y axis with, above the current tile,
 * to mimic/fake z-axis behavour.
 *
 * Used for items that span/overlap tiles on top of it, like a tall tower
 * that overlaps the grass tile on top of it.
 */
fun LocationTileOccupant.getZAxisFraction(): Float =
    when (this) {
        is Tower -> when (type) {
            TowerType.Archer -> 1.22f
            TowerType.Empty -> .138f
        }

        is Decoration -> when (type) {
            DecorationType.TreesPine -> .63f
            DecorationType.TreesRound -> .53f
            DecorationType.Barrels,
            DecorationType.BushOne,
            DecorationType.BushTwo,
            DecorationType.BushThree,
            DecorationType.StoneOne,
            DecorationType.StoneTwo -> .0f
        }

        else -> 0f
    }

/**
 * Returns the neighbor location in the given direction.
 */
fun LocationTile.getNeighborLocation(direction: Direction): LocationTile =
    when (direction) {
        Direction.NorthEast -> LocationTile(x + (y % 2), y - 1)
        Direction.NorthWest -> LocationTile(x - ((y + 1) % 2), y - 1)
        Direction.SouthEast -> LocationTile(x + (y % 2), y + 1)
        Direction.SouthWest -> LocationTile(x - ((y + 1) % 2), y + 1)
        Direction.East -> LocationTile(x + 1, y)
        Direction.North -> LocationTile(x, y - 2)
        Direction.South -> LocationTile(x, y + 2)
        Direction.West -> LocationTile(x - 1, y)
    }

fun LocationTile.getLocationsWithinShootingRange(): List<LocationTile> =
    shootingDirections.map { direction ->
        getNeighborLocation(direction)
    }

/**
 * Gets the direction of [otherLocation] from [this] Location, if it's adjacent.
 */
fun LocationTile.getMovementDirection(otherLocation: LocationTile): Direction? {
    movementDirections.forEach { direction ->
        if (getNeighborLocation(direction) == otherLocation) return direction
    }
    return null
}