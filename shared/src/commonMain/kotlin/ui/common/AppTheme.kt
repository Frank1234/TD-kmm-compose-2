package ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val bgColor = Color(0xff90ae28)

@Composable
fun AppTheme(typography: Typography, content: @Composable () -> Unit) {
    MaterialTheme(
        typography = typography,
        content = content,
    )
}