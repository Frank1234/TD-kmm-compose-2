package ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextWithShadow(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 22.sp, textAlign: TextAlign = TextAlign.End) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.body1.copy(
            shadow = Shadow(
                color = Color.DarkGray,
                offset = Offset(4f, 4f),
                blurRadius = 8f
            )
        ),
        textAlign = textAlign,
        fontSize = fontSize,
        color = Color.White,
    )
}