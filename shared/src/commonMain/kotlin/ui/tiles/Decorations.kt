package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Decoration
import domain.model.getDrawableResId

@Composable
fun Decorations(decorations: List<Decoration>) {
    decorations.forEach { decoration ->
        Decoration(decoration)
    }
}

@Composable
fun Decoration(decoration: Decoration) {
    AtLocationOf(decoration) {
        Image(
            painter = painterResource(decoration.getDrawableResId()), contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}