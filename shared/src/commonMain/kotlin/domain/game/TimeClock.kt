package domain.game

import kotlinx.datetime.Clock

fun getCurrentTimeMs() = Clock.System.now().toEpochMilliseconds()