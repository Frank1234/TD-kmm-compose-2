package domain.model

import com.myapplication.common.MR
import dev.icerock.moko.resources.ImageResource
import domain.game.gameRandom

val movementDirections = listOf(Direction.NorthEast, Direction.NorthWest, Direction.SouthEast, Direction.SouthWest)
val shootingDirections = movementDirections.toMutableList().apply {
    addAll(
        listOf(
            Direction.North, Direction.East, Direction.South, Direction.West
        )
    )
}.toList()

sealed class Direction {
    object North : Direction()
    object East : Direction()
    object South : Direction()
    object West : Direction()
    object NorthEast : Direction()
    object SouthEast : Direction()
    object SouthWest : Direction()
    object NorthWest : Direction()
}

// TODO move:
data class Decoration(
    override val locationTile: LocationTile,
    val type: DecorationType,
    val id: Long = gameRandom.nextLong(),
) : LocationTileOccupant

// strings are used to be able to use this method on both platforms
fun Decoration.getDrawableResId(): ImageResource =
    when (this.type) {
        DecorationType.Barrels -> MR.images.deco_barrels
        DecorationType.BushOne -> MR.images.deco_bush_1
        DecorationType.BushThree -> MR.images.deco_bush_2
        DecorationType.BushTwo -> MR.images.deco_bush_3
        DecorationType.StoneOne -> MR.images.deco_stone_1
        DecorationType.StoneTwo -> MR.images.deco_stone_2
        DecorationType.TreesPine -> MR.images.deco_trees_pine
        DecorationType.TreesRound -> MR.images.deco_trees_round
    }

sealed class DecorationType {
    object StoneOne : DecorationType()
    object StoneTwo : DecorationType()
    object BushOne : DecorationType()
    object BushTwo : DecorationType()
    object BushThree : DecorationType()
    object TreesPine : DecorationType()
    object TreesRound : DecorationType()
    object Barrels : DecorationType()
}

fun Direction.getDirectionMultiplierY() = when (this) {
    Direction.NorthEast,
    Direction.NorthWest -> -1

    Direction.SouthEast,
    Direction.SouthWest -> 1

    Direction.East,
    Direction.North,
    Direction.South,
    Direction.West -> 0
}

fun Direction.getDirectionMultiplierX() = when (this) {
    Direction.NorthEast,
    Direction.SouthEast -> 1

    Direction.NorthWest,
    Direction.SouthWest -> -1

    Direction.East,
    Direction.North,
    Direction.South,
    Direction.West -> 0
}