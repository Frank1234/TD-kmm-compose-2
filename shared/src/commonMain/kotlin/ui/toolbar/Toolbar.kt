package com.example.kotlinmultiplatformmobiletest.android.tiles.toolbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinmultiplatformmobiletest.android.MenuButton
import com.example.kotlinmultiplatformmobiletest.android.WaveCounter
import com.example.kotlinmultiplatformmobiletest.android.tiles.Counter
import com.myapplication.common.MR
import domain.model.Event
import domain.model.Game

@Composable
fun Toolbar(game: Game, onEvent: (Event) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
    ) {

        Counter(game.coins, MR.images.counter_coins)
        Counter(game.lifeCount, MR.images.counter_heart)

        Spacer(modifier = Modifier.weight(1f))

        WaveCounter(game.waveNumber, game.maxWaveNumber)
        MenuButton(onEvent)

    }
}