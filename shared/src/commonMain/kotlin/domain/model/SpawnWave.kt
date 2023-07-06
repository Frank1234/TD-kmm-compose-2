package domain.model

/**
 * A wave of enemies that are spawn at the same time or shortly after one another.
 */
data class SpawnWave(
    val index: Int,
    val enemiesLeftToSpawn: Map<EnemyType, Int>,
) {
    val isFinished = enemiesLeftToSpawn.all { it.value == 0 }
}

fun createInitialWave() = SpawnWave(0, mapOf(EnemyType.Orc to 1))

fun SpawnWave.removeEnemies(enemies: List<Enemy>): SpawnWave {
    val updatedEnemies = enemiesLeftToSpawn.toMutableMap()
    enemies.forEach { enemy ->
        updatedEnemies[enemy.type] = updatedEnemies[enemy.type]?.minus(1) ?: 0
    }
    return SpawnWave(index, enemiesLeftToSpawn = updatedEnemies)
}

fun SpawnWave.toNextWave() = copy(
    index = index + 1,
    enemiesLeftToSpawn = mapOf(
        EnemyType.Orc to
            if (index <= 2) index + 2
            else if (index == 3) 2
            else if (index <= 5) index
            else if (index <= 7) index + 2
            else if (index == 8) 10
            else 8,
        EnemyType.Ogre to
            if (index < 3) 0
            else if (index == 3) 1
            else if (index == 4) 2
            else if (index == 5) 3
            else if (index == 6) 5
            else if (index == 7) 6
            else if (index == 8) 10
            else 28
    )
)