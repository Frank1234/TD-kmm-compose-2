package com.example.kotlinmultiplatformmobiletest.android.tiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun Counter(displayNumber: Int, painterRes: ImageResource) {

    Box(modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)) {
        Image(
            painter = painterResource(painterRes), contentDescription = "",
            contentScale = ContentScale.Fit,
        )
        Text(
            text = "$displayNumber",
            textAlign = TextAlign.End,
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(min = 60.dp)
                .padding(end = 20.dp, bottom = 20.dp)
        )
    }
}