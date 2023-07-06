package domain.model

import domain.game.getCurrentTimeMs

private const val DISPLAY_DURATION_SHORT = 1200L
private const val DISPLAY_DURATION_LONG = 3000L

data class ToastMessage(
    val specs: ToastMessageSpecs, // TODO merge specs with ToastMessage?
    val startTimeMs: Long = getCurrentTimeMs(), // time at we requested displaying this toast
) {
    fun isExpired(): Boolean = getCurrentTimeMs() - startTimeMs > specs.displayDurationMs
}

sealed class ToastMessageSpecs(val displayDurationMs: Long, val displayType: ToastMessageDisplayType) {
    object NotEnoughFunds : ToastMessageSpecs(DISPLAY_DURATION_SHORT, ToastMessageDisplayType.Small)
    data class SpawnWaveStarts(val waveNumber: Int, val isLastWave: Boolean) : ToastMessageSpecs(DISPLAY_DURATION_LONG, ToastMessageDisplayType.Large)
}

sealed class ToastMessageDisplayType {
    object Small : ToastMessageDisplayType()
    object Large : ToastMessageDisplayType()
}