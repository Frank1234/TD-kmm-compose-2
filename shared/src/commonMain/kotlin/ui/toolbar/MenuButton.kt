package com.example.kotlinmultiplatformmobiletest.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.myapplication.common.MR
import dev.icerock.moko.resources.compose.painterResource
import domain.model.Event

@Composable
fun MenuButton(onEvent: (Event) -> Unit) {
    Image(
        painter = painterResource(MR.images.ic_settings), contentDescription = "Menu",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clickable { onEvent(Event.Menu.OpenMenu) }
            .padding(top = 10.dp, end = 20.dp)
            .size(54.dp)
    )
}