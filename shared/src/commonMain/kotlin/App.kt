import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.kotlinmultiplatformmobiletest.android.GameView
import domain.game.GameLoop
import domain.model.Event
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

private val gameLoop: GameLoop = GameLoop()

@Composable
fun App() {

    LaunchedEffect(Unit) {
        gameLoop.start()
        Napier.base(DebugAntilog())
    }

    val onEvent = { event: Event ->
        when (event) {
            Event.Menu.QuitGame -> {} //quitGame()
            Event.Menu.RestartGame -> restartGame(gameLoop)
            else -> gameLoop.onEvent(event)
        }
    }

    val game = gameLoop.gameFlow.collectAsState()

    game.value?.let { GameView(it, onEvent) }
}

private fun restartGame(gameLoop: GameLoop) {
    gameLoop.stop()
    gameLoop.start()
}

expect fun getPlatformName(): String
