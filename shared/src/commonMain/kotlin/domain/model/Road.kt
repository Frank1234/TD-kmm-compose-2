package domain.model

import com.myapplication.common.MR
import dev.icerock.moko.resources.ImageResource
import domain.game.gameRandom

data class Roads(val value: List<Road>)

data class Road(
    override val locationTile: LocationTile,
    val id: Long = gameRandom.nextLong(),
) : LocationTileOccupant

// strings are used to be able to use this method on both platforms
fun RoadDirectionType.getDrawableResId(): ImageResource =
    when (this) {
        RoadDirectionType.NorthEastToNorthWest -> MR.images.road_nenw
        RoadDirectionType.NorthWestToSouthEast -> MR.images.road_nwse
        RoadDirectionType.SouthWestToNorthEast -> MR.images.road_swne
        RoadDirectionType.SouthWestToSouthEast -> MR.images.road_swse
        RoadDirectionType.NorthWestToSouthWest -> MR.images.road_nwsw
        RoadDirectionType.NorthEastToSouthEast -> MR.images.road_nese
    }

sealed class RoadDirectionType {
    object NorthEastToNorthWest : RoadDirectionType()
    object SouthWestToSouthEast : RoadDirectionType()
    object SouthWestToNorthEast : RoadDirectionType()
    object NorthWestToSouthEast : RoadDirectionType()
    object NorthWestToSouthWest : RoadDirectionType()
    object NorthEastToSouthEast : RoadDirectionType()
    // object ForkSouthEastBlocked: RoadDirectionType()
    // object ForkSouthWestBlocked: RoadDirectionType()
}

fun Roads.getDirectionType(road: Road): RoadDirectionType {
    val northEastIsRoad = isRoad(road.locationTile.getNeighborLocation(Direction.NorthEast))
    val northWestIsRoad = isRoad(road.locationTile.getNeighborLocation(Direction.NorthWest))
    val southEastIsRoad = isRoad(road.locationTile.getNeighborLocation(Direction.SouthEast))
    val southWestIsRoad = isRoad(road.locationTile.getNeighborLocation(Direction.SouthWest))
    return when {
        southWestIsRoad && northEastIsRoad -> RoadDirectionType.SouthWestToNorthEast
        northEastIsRoad && northWestIsRoad -> RoadDirectionType.NorthEastToNorthWest
        northEastIsRoad && southEastIsRoad -> RoadDirectionType.NorthEastToSouthEast
        northWestIsRoad && southWestIsRoad -> RoadDirectionType.NorthWestToSouthWest
        southWestIsRoad && southEastIsRoad -> RoadDirectionType.SouthWestToSouthEast
        northWestIsRoad && southEastIsRoad -> RoadDirectionType.NorthWestToSouthEast
        northWestIsRoad -> RoadDirectionType.NorthWestToSouthEast // for out of game board roads (starting and ending point of enemies)
        southEastIsRoad -> RoadDirectionType.NorthWestToSouthEast // for out of game board roads (starting and ending point of enemies)
        else -> RoadDirectionType.SouthWestToNorthEast
    }
}

fun Roads.isRoad(location: LocationTile) = value.any { it.locationTile == location }
