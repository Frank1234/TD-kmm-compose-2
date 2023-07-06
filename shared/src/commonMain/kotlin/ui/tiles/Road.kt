package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Road
import domain.model.RoadDirectionType
import domain.model.Roads
import domain.model.getDirectionType
import domain.model.getDrawableResId

@Composable
fun Roads(roads: Roads) {
    roads.value.forEach { road ->
        Road(road, roads.getDirectionType(road))
    }
}

@Composable
fun Road(road: Road, directionType: RoadDirectionType) {
    AtLocationOf(road) {
        Image(
            painter = painterResource(directionType.getDrawableResId()), contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}