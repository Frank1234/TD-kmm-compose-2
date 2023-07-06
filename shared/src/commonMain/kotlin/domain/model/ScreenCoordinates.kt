package domain.model

import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileHeight
import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileHeightHalf
import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileWidth
import com.example.kotlinmultiplatformmobiletest.game.model.GameSizeSpecs.tileWidthHalf

data class ScreenCoordinates(
    val x: Double,
    val y: Double,
)

fun getScreenCoordinates(
    locationTileX: Int,
    locationTileY: Int,
    /**
     * The fraction of tiles to overlap the y axis with, above the current tile,
     * to mimic/fake z-axis behaviour.
     */
    zAxisFraction: Float = 0f,
): ScreenCoordinates {
    val screenX =
        if (locationTileY % 2 == 0) tileWidth * locationTileX
        else (tileWidthHalf + (tileWidth * locationTileX))
    val screenY = (tileHeightHalf * locationTileY)
    val x = screenX
    val startY = tileHeightHalf // extra space on top of the map, so that high towers/trees do not fall out of the screen
    val y = startY + screenY
    val z = (zAxisFraction * tileHeight) // used for items that span/overlap tiles above the current tile, like a tall tower.
    return ScreenCoordinates(x, y - z)
}