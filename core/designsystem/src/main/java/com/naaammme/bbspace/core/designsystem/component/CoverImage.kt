package com.naaammme.bbspace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CoverImage(
    url: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape? = RoundedCornerShape(12.dp),
    fallbackContent: (@Composable BoxScope.() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    val bgColor = MiuixTheme.colorScheme.surfaceVariant
    val coverModifier = if (shape == null) {
        modifier.background(bgColor)
    } else {
        modifier
            .clip(shape)
            .background(bgColor)
    }

    Box(
        modifier = coverModifier,
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            BiliAsyncImage(
                url = url,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                variant = BiliImageVariant.CardCover
            )
        } else {
            fallbackContent?.invoke(this)
        }
        content?.invoke(this)
    }
}
