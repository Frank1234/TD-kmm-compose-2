package domain.model

import com.example.kotlinmultiplatformmobiletest.game.zoomLevel
import domain.model.Decoration
import kotlin.math.min

object GameSizeSpecs {
    const val gameWidthTiles = 5
    const val gameHeightTiles = 10
    const val tileHeight = 100f * zoomLevel // note that image might be taller, like a tower that overlaps the tile on top of it as well.
    const val tileHeightHalf = tileHeight / 2
    const val tileWidth = 200f * zoomLevel
    const val tileWidthHalf = tileWidth / 2

    const val startXOfMap = 0
    const val startYOfMap = tileHeight // tileHeightDp is top margin for upper square, to be able to draw out of the box (with a tower)

    const val mapWidth =
        startXOfMap + (tileWidth * (gameWidthTiles + 1)) + tileWidthHalf // + tileWidthHalfDp for the uneven rows that go to the right
    const val mapHeight = startYOfMap + (tileHeightHalf * (gameHeightTiles + 1))
}

data class Game(
    val decorations: List<Decoration>,
    val roads: Roads,
    val towers: List<Tower>,
    val enemies: List<Enemy>,
    val spawnWave: SpawnWave,
    val maxWaveIndex: Int,
    val coins: Int,
    val state: GameState,
    val showMenu: Boolean,
    val lifeCount: Int, // TODO value class
    val toastMessage: ToastMessage?,
) {
    val waveNumber = min(maxWaveIndex, spawnWave.index) + 1
    val maxWaveNumber = maxWaveIndex + 1

    // needed for iOS:
    val gameWidthTiles = GameSizeSpecs.gameWidthTiles
    val gameHeightTiles = GameSizeSpecs.gameHeightTiles
    val tileHeight = GameSizeSpecs.tileHeight
    val tileHeightHalf = GameSizeSpecs.tileHeightHalf
    val tileWidth = GameSizeSpecs.tileWidth
    val tileWidthHalf = GameSizeSpecs.tileWidthHalf
    val startXOfMap = GameSizeSpecs.startXOfMap
    val startYOfMap = GameSizeSpecs.startYOfMap
    val mapWidth = GameSizeSpecs.mapWidth
    val mapHeight = GameSizeSpecs.mapHeight
}

sealed class GameState(val isEndState: Boolean) {
    object Running : GameState(isEndState = false)
    object Won : GameState(true)
    object Lost : GameState(true)
}