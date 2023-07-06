package com.example.kotlinmultiplatformmobiletest.android

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.myapplication.common.MR
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import domain.model.Event
import domain.model.GameState
import ui.common.TextWithShadow

@Composable
fun Menu(title: String, onEvent: (Event) -> Unit) {
    MenuContainer(onEvent) {

        MenuTitle(title)

        Row(horizontalArrangement = Arrangement.Center) {
            MenuIcon(MR.images.button_restart) { onEvent(Event.Menu.RestartGame) }
            MenuIcon(MR.images.button_quit) { onEvent(Event.Menu.QuitGame) }
        }
    }
}

@Composable
private fun MenuContainer(onEvent: (Event) -> Unit, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onEvent(Event.Menu.CloseMenu) },
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
private fun MenuTitle(title: String) {
    TextWithShadow(
        text = title,
        textAlign = TextAlign.Center,
        fontSize = 55.sp,
    )
}

@Composable
private fun MenuIcon(imageResource: ImageResource, onClick: () -> Unit) {
    Image(
        painter = painterResource(imageResource), contentDescription = "",
        modifier = Modifier
            .clickable {
                onClick()
            }
    )
}

@Composable
fun GameState.getMessage(): String = when (this) {
    GameState.Lost -> stringResource(MR.strings.message_you_lost)
    GameState.Running -> stringResource(MR.strings.menu_title)
    GameState.Won -> stringResource(MR.strings.message_you_won)
}