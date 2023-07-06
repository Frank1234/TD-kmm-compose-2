package com.example.kotlinmultiplatformmobiletest.android

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.myapplication.common.MR
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import ui.common.TextWithShadow

@Composable
fun WaveCounter(waveNumber: Int, lastWaveNumber: Int) {
    TextWithShadow(
        text = stringResource(MR.strings.wave_counter_message, waveNumber, lastWaveNumber),
        fontSize = 22.sp,
        modifier = Modifier
            .padding(top = 10.dp, bottom = 25.dp, end = 10.dp),
    )
}