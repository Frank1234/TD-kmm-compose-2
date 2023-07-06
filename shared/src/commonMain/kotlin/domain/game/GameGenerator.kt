package domain.game

import domain.model.Decoration
import domain.model.DecorationType
import domain.model.Enemy
import domain.model.EnemyType
import domain.model.Game
import domain.model.GameSizeSpecs.gameHeightTiles
import domain.model.GameSizeSpecs.gameWidthTiles
import domain.model.GameState
import domain.model.Health
import domain.model.LocationTile
import domain.model.Road
import domain.model.Roads
import domain.model.SpawnWave
import domain.model.ToastMessage
import domain.model.ToastMessageSpecs
import domain.model.Tower
import domain.model.TowerType
import domain.model.createInitialWave
import kotlin.math.max
import kotlin.math.min

fun generateGame(): Game {
    val roads = generateRoads()
    val towers = generateTowers()
    val blockedTiles = roads.value.map { it.locationTile }.toMutableList().apply {
        addAll(towers.map { it.locationTile })
    }
    val initialWave = createInitialWave()
    return Game(
        decorations = generateDecorations(blockedTiles),
        roads = roads,
        towers = towers,
        spawnWave = initialWave,
        maxWaveIndex = 9,
        enemies = listOf(),
        coins = 80,
        lifeCount = 3,
        state = GameState.Running,
        showMenu = false,
        toastMessage = ToastMessage(ToastMessageSpecs.SpawnWaveStarts(1, false))
    )
}

private fun generateRoads(): Roads {
    val roads = mutableListOf(
        Road(LocationTile(-1, 3)), // starting point of units, outside of board
        Road(LocationTile(0, 4)),
        Road(LocationTile(0, 5)),
        Road(LocationTile(1, 6)),
        Road(LocationTile(1, 5)),
        Road(LocationTile(2, 4)),
        Road(LocationTile(2, 5)),
        Road(LocationTile(3, 4)),
        Road(LocationTile(3, 3)),
        Road(LocationTile(4, 2)),
        Road(LocationTile(4, 3)),
        Road(LocationTile(5, 4)),
        Road(LocationTile(4, 5)),
        Road(LocationTile(4, 6)),
        Road(LocationTile(4, 7)),
        Road(LocationTile(5, 8)),
        Road(LocationTile(5, 9)),
        Road(LocationTile(6, 10)), // out of board, end tile
    )
    return Roads(roads)
}

private fun generateTowers(): List<Tower> =
    listOf(
        Tower(
            locationTile = LocationTile(x = 0, 7),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 1, 4),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 2, 3),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 1, 7),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 3, 1),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 3, 6),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 4, 1),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 4, 4),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 4, 8),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 5, 3),
            type = TowerType.Empty,
        ),
        Tower(
            locationTile = LocationTile(x = 5, 7),
            type = TowerType.Empty,
        ),
    )

fun generateDecorations(unavailableTiles: List<LocationTile>): List<Decoration> {
    val decorations = mutableListOf<Decoration>()
    for (x in 0..gameWidthTiles) {
        for (y in 0..gameHeightTiles) {
            val location = LocationTile(x, y)
            if (!unavailableTiles.contains(location)) {
                val randomNr = x + y % 10 // gameRandom.nextInt(11) // TODO
                val type = when (randomNr) {
                    0 -> DecorationType.BushOne
                    1 -> DecorationType.BushTwo
                    2 -> DecorationType.BushThree
                    3 -> DecorationType.StoneOne
                    4 -> DecorationType.StoneTwo
                    in 5..6 -> DecorationType.TreesPine
                    in 7..8 -> DecorationType.TreesRound
                    9 -> DecorationType.Barrels
                    else -> null
                }
                if (type != null)
                    decorations.add(
                        Decoration(
                            location, type
                        )
                    )
            }
        }
    }
    return decorations
}

// TODO move
fun spawnNewEnemies(spawnWave: SpawnWave, roads: Roads): List<Enemy> {
    val enemies = mutableListOf<Enemy>()
    val enemyTypesLeftToSpawn = spawnWave.enemiesLeftToSpawn.filterNot { it.value == 0 }.keys
    val spawnEnemyType = enemyTypesLeftToSpawn.randomOrNull()
    if (spawnEnemyType != null) {
        val enemiesLeftToSpawn = spawnWave.enemiesLeftToSpawn.get(spawnEnemyType)
        if (enemiesLeftToSpawn != null) enemies.addAll(spawnNewEnemiesOfType(roads, spawnEnemyType, enemiesLeftToSpawn))
    }
    return enemies
}

// TODO move
fun spawnNewEnemiesOfType(roads: Roads, enemyType: EnemyType, enemiesLeftToSpawn: Int): List<Enemy> {
    val spawnLocation = roads.value[0].locationTile
    val enemies = mutableListOf<Enemy>()
    val maxEnemiesToSpawnOnOneTile = min(enemiesLeftToSpawn, enemyType.maxNumberOnTile)
    if (maxEnemiesToSpawnOnOneTile > 0) {
        val enemiesToAdd = gameRandom.nextInt(max(1, maxEnemiesToSpawnOnOneTile - 2), maxEnemiesToSpawnOnOneTile + 1)
        for (index in 0 until enemiesToAdd) {
            enemies.add(
                Enemy(
                    type = enemyType,
                    locationTile = spawnLocation,
                    health = Health(enemyType.health, enemyType.health),
                    adjustmentFromCenterOfTile = when (index) {
                        0 -> 0f
                        1 -> .4f
                        2 -> -.4f
                        3 -> .2f
                        4 -> -.2f
                        else -> 1f / (index + 1f)
                    }
                )
            )
        }
    }
    return enemies
}