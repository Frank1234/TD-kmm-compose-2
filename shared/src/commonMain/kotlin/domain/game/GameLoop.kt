package domain.game

import domain.common.Cancellable
import domain.common.collect2
import domain.game.EventHandler.TowerHandler.constructOrUpdateTower
import domain.game.EventHandler.TowerHandler.selectTower
import domain.game.EventHandler.clearSelection
import domain.model.Enemy
import domain.model.Event
import domain.model.Game
import domain.model.GameState
import domain.model.Roads
import domain.model.ToastMessage
import domain.model.ToastMessageSpecs
import domain.model.Tower
import domain.model.TowerType
import domain.model.didReachFinishTile
import domain.model.getConstructionButtonAlpha
import domain.model.isInRange
import domain.model.removeEnemies
import domain.model.toNextWave
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

const val zoomLevel = 0.8
const val gameSpeed = 0.7 // for easy tweaking. higher is faster

const val updatesPerSecond = 60
const val loopCycleLengthMs = 1000 / updatesPerSecond
const val enemyMovementPerCycle = (1.0 / updatesPerSecond) * gameSpeed
const val bulletMovementPerCycle = (2.3 / updatesPerSecond) * gameSpeed
const val spawnEnemiesDelayMs = 1000 * (1.0 / gameSpeed) // in between spawns inside a wave
const val initialGameStartupDelay = 2100 // go give the user a bit of time to look at the map

val gameRandom = Random(getCurrentTimeMs())

class GameLoop() {

    private val scope: CoroutineScope = GlobalScope

    val gameFlow: MutableStateFlow<Game?> = MutableStateFlow(null)
    private var lastSpawnTimeMs: Long = getInitialLastSpawnTime()

    // Wrapper function for Swift
    // https://betterprogramming.pub/using-kotlin-flow-in-swift-3e7b53f559b6
    fun gameFlowiOS(onEach: (Game?) -> Unit, onCompletion: (Throwable?) -> Unit): Cancellable =
        gameFlow.collect2(onEach, onCompletion)

    private fun getInitialLastSpawnTime(): Long = getCurrentTimeMs() + initialGameStartupDelay

    // TODO is this threadsafe? no
    private var lastEvent: Event? = null // TODO do we need a list?
    private var loopJob: Job? = null

    fun start() {
        gameFlow.update { _ -> generateGame() }
        lastSpawnTimeMs = getInitialLastSpawnTime()
        loopJob = scope.launch(Dispatchers.Default) {
            run()
        }
    }

    fun stop() {
        loopJob?.cancel()
        loopJob = null
        lastEvent = null
    }

    fun onEvent(event: Event) {
        lastEvent = event
    }

    @OptIn(ExperimentalTime::class)
    private tailrec suspend fun run() {

        if (gameFlow.value?.state?.isEndState == true || loopJob?.isActive == false) {
            stop()
            return // end the loop
        }

        val thisLoopsTime = measureTime {
            gameFlow.update { lastGame -> if (lastGame != null) updateGame(lastGame) else null }
        }
        val timeToDelayMs = loopCycleLengthMs - thisLoopsTime.inWholeMilliseconds
        if (timeToDelayMs > 0) {
            Napier.d { "Game loop cycle finished in ${thisLoopsTime.inWholeMilliseconds} ms. Delaying $timeToDelayMs ms (loopLengthMs $loopCycleLengthMs)" }
            delay(timeToDelayMs.toLong())
        } else {
            // TODO handle better? read up
            Napier.e { "Game loop cycle too long (is ${thisLoopsTime.inWholeMilliseconds} ms)!! timeToDelay: $timeToDelayMs ms (loopLengthMs $loopCycleLengthMs)" }
        }
        return run()
    }

    private fun handleEvents(incomingGame: Game): Game {
        val game = when (val event = lastEvent) {
            is Event.Tower.Selected -> selectTower(incomingGame, event)
            is Event.Tower.ConstructButtonPressed -> constructOrUpdateTower(incomingGame, event)
            Event.ClearSelection -> clearSelection(incomingGame)
            null -> incomingGame
            Event.Menu.QuitGame -> incomingGame // handled elsewhere
            Event.Menu.RestartGame -> incomingGame// handled elsewhere
            Event.Menu.CloseMenu -> // TODO move to eventhandler
                if (!incomingGame.state.isEndState) incomingGame.copy(showMenu = false)
                else incomingGame // TODO move to eventhandler
            Event.Menu.OpenMenu -> incomingGame.copy(showMenu = true)
        }
        lastEvent = null
        return game
    }

    private fun updateGame(incomingGame: Game): Game {

        val currentTimeMs = getCurrentTimeMs()

        // handle events
        val game = handleEvents(incomingGame)

        // update game state based on time:

        // TODO not var?
        var updatedEnemies = game.enemies.toMutableList()
        var updatedCoins = game.coins

        // let's move the towers and their bullets
        var updatedTowers = game.towers.map { tower ->
            if (tower.canShoot) updateTower(tower, updatedEnemies) { attackDamage ->
                // TODO method
                val target = updatedEnemies.firstOrNull { it.id == tower.shootingTargetId }
                val updatedTarget =
                    target?.health?.let { health -> target.copy(health = health.copy(currentValue = target.health.currentValue - attackDamage)) }
                if (updatedTarget != null) {
                    updatedEnemies.remove(target)
                    updatedEnemies.add(updatedTarget)

                    if (!target.isDead && updatedTarget.isDead) {
                        // TODO method
                        updatedCoins += target.type.coins
                    }
                }
            }
            else tower
        }

        // TODO add: when "spawning index > x && spawning tile empty":
        val spawnDelayPassed = currentTimeMs - lastSpawnTimeMs > spawnEnemiesDelayMs
        if (spawnDelayPassed) lastSpawnTimeMs = currentTimeMs
        val noEnemiesLeftOnBoard = updatedEnemies.count { it.canMove } == 0

        var updatedSpawnWave = if (noEnemiesLeftOnBoard && game.spawnWave.isFinished) {
            game.spawnWave.toNextWave()
        } else game.spawnWave

        val spawnNewEnemies = (spawnDelayPassed && !game.spawnWave.isFinished)

        // TODO function
        if (spawnNewEnemies) {
            // let's spawn new enemies
            val spawnEnemies = spawnNewEnemies(updatedSpawnWave, game.roads)
            updatedSpawnWave = updatedSpawnWave.removeEnemies(spawnEnemies)
            updatedEnemies.addAll(spawnEnemies)
        }

        // let's move the enemies
        updatedEnemies = updatedEnemies.map { enemy ->
            if (enemy.canMove) moveEnemy(enemy, game.roads)
            else enemy
        }.toMutableList()

        // let's remove finished enemies
        var updatedLifeCount = game.lifeCount
        updatedEnemies = updatedEnemies.mapNotNull { enemy ->
            if (enemy.didReachFinishTile(game.roads)) {
                if (updatedLifeCount > 0) {
                    updatedLifeCount -= 1
                    // TODO method:
                    updatedCoins += enemy.type.coins
                }
                null
            } else enemy
        }.toMutableList()

        val updatedGameState =
            if (updatedLifeCount <= 0) GameState.Lost
            else if (updatedSpawnWave.index > game.maxWaveIndex) GameState.Won
            else GameState.Running

        val updatedShowMenu = if (updatedGameState.isEndState) true
        else game.showMenu

        val updatedToastMessage: ToastMessage? =
            if (game.spawnWave.index != updatedSpawnWave.index && game.spawnWave.index <= game.maxWaveIndex)
                ToastMessage(specs = ToastMessageSpecs.SpawnWaveStarts(updatedSpawnWave.index + 1, updatedSpawnWave.index == game.maxWaveIndex))
            else if (game.toastMessage?.isExpired() == false) game.toastMessage
            else null

        updatedTowers = updatedTowers.map { tower ->
            tower.copy(
                constructionButtonAlpha = tower.getConstructionButtonAlpha(
                    TowerType.Archer, updatedCoins
                )
            )
        }
        updatedTowers = updatedTowers.map { tower ->
            tower.copy(
                shootingTarget = updatedEnemies.firstOrNull { it.id == tower.shootingTargetId }
            )
        }

        return game.copy(
            enemies = updatedEnemies,
            towers = updatedTowers,
            spawnWave = updatedSpawnWave,
            coins = updatedCoins,
            lifeCount = updatedLifeCount,
            state = updatedGameState,
            showMenu = updatedShowMenu,
            toastMessage = updatedToastMessage,
        )
    }
}

private fun moveEnemy(enemy: Enemy, roads: Roads): Enemy {
    return if (enemy.stepTargetLocation == null || enemy.stepTargetFractionDone >= 1f) {
        // step target reached, find new target
        val roadLocations = roads.value.map { it.locationTile }
        val stepTargetIndex = enemy.stepTargetLocation?.let { roadLocations.indexOf(it) } ?: 0
        val newStepTarget = roadLocations.getOrNull(stepTargetIndex + 1)
        enemy.copy(
            locationTile = enemy.stepTargetLocation ?: enemy.locationTile,
            stepTargetLocation = newStepTarget,
            stepTargetFractionDone = 0.0 + (enemyMovementPerCycle * enemy.type.speed)
        )
    } else {
        enemy.copy(stepTargetFractionDone = enemy.stepTargetFractionDone + (enemyMovementPerCycle * enemy.type.speed))
    }
}

private fun updateTower(tower: Tower, enemies: List<Enemy>, onEnemyHit: (attackDamage: Int) -> Unit): Tower =
    // TODO methods:
    if (tower.shootingTargetId == null) {
        // check if there is a target in range
        val shootingTarget = enemies.firstOrNull { enemy -> !enemy.isDead && tower.isInRange(enemy) }
        tower.copy(shootingTargetId = shootingTarget?.id, shootingFractionDone = 0.0)
    } else {
        val shootingTarget = enemies.firstOrNull { it.id == tower.shootingTargetId }
        val bulletStillFlying = (0.001..0.999).contains(tower.shootingFractionDone)
        if ((shootingTarget != null && !shootingTarget.isDead && tower.isInRange(shootingTarget)) || bulletStillFlying) {
            // target is still in range or bullet already flies
            val newFractionDone = tower.shootingFractionDone + bulletMovementPerCycle
            if (newFractionDone < 1.0) {
                // move bullet
                tower.copy(shootingFractionDone = newFractionDone)
            } else {
                // bullet arrived
                onEnemyHit(tower.getAttackDamage())
                // shoot new bullet
                tower.copy(shootingFractionDone = 0.0)
            }
        } else {
            // target walked out of range or died
            tower.copy(shootingTargetId = null, shootingFractionDone = 0.0)
        }
    }