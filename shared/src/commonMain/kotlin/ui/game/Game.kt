package com.example.kotlinmultiplatformmobiletest.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kotlinmultiplatformmobiletest.android.tiles.toolbar.Toolbar
import com.myapplication.common.MR
import dev.icerock.moko.resources.compose.fontFamilyResource
import domain.model.Event
import domain.model.Game
import ui.common.AppTheme
import ui.common.ToastMessage

val bgColor = Color(0xff90ae28)

@Composable
fun GameView(game: Game, onEvent: (Event) -> Unit) {

    val grandStanderCleanFont = fontFamilyResource(
        MR.fonts.grandstanderclean.grandstanderclean,
    )

    val typography = Typography(
        defaultFontFamily = grandStanderCleanFont,
    )

    AppTheme(typography) {
        Box(modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onEvent(Event.ClearSelection) }) {

            GameBoard(bgColor, game, onEvent)

            game.toastMessage.let { toastMessage ->
                AnimatedVisibility(visible = toastMessage != null, enter = fadeIn() + slideInVertically { it / 4 }, exit = fadeOut()) {
                    if (toastMessage != null) ToastMessage(toastMessage)
                }
            }

            Toolbar(game, onEvent)

            if (game.showMenu)
                Menu(title = game.state.getMessage(), onEvent)
        }
    }
}