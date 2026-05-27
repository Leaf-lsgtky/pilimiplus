package com.naaammme.bbspace.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AvatarImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    fallbackText: String? = null,
    fallbackContainerColor: Color = Color.Unspecified,
    fallbackContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val shape = CircleShape
    val containerColor = if (fallbackContainerColor != Color.Unspecified) {
        fallbackContainerColor
    } else {
        MiuixTheme.colorScheme.surfaceVariant
    }
    if (url.isNullOrBlank()) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            when {
                fallbackContent != null -> fallbackContent()
                !fallbackText.isNullOrBlank() -> {
                    Text(
                        text = fallbackText,
                        style = MiuixTheme.textStyles.subtitle,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }
    BiliAsyncImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        variant = BiliImageVariant.Avatar
    )
}
