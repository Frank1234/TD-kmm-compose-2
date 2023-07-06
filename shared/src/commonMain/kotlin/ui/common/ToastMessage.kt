package ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.common.MR
import dev.icerock.moko.resources.compose.stringResource
import domain.model.ToastMessage
import domain.model.ToastMessageDisplayType
import domain.model.ToastMessageSpecs

@Composable
fun ToastMessage(toastMessage: ToastMessage) {
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = toastMessage.specs.displayType.getVerticalArrangement(), horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextWithShadow(
            text = toastMessage.getMessage(),
            textAlign = TextAlign.Center,
            fontSize = toastMessage.specs.displayType.fontSize(),
            modifier = Modifier.padding(20.dp)
        )
    }
}

fun ToastMessageDisplayType.fontSize() = when (this) {
    ToastMessageDisplayType.Large -> 40.sp
    ToastMessageDisplayType.Small -> 22.sp
}

fun ToastMessageDisplayType.getVerticalArrangement() = when (this) {
    ToastMessageDisplayType.Large -> Arrangement.Center
    ToastMessageDisplayType.Small -> Arrangement.Bottom
}

@Composable
fun ToastMessage.getMessage(): String = when (val specs = specs) {
    ToastMessageSpecs.NotEnoughFunds -> stringResource(MR.strings.message_not_enough_funds)
    is ToastMessageSpecs.SpawnWaveStarts ->
        when {
            specs.waveNumber > 10 -> ""
            specs.waveNumber == 1 -> stringResource(MR.strings.message_wave_first_starts)
            specs.isLastWave -> stringResource(MR.strings.message_wave_last_starts)
            else -> stringResource(MR.strings.message_wave_x_starts, specs.waveNumber)
        }
}