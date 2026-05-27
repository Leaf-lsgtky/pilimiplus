package com.naaammme.bbspace.feature.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Send
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun CommentEditorFab(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.navigationBarsPadding()
    ) {
        Icon(
            imageVector = MiuixIcons.Send,
            contentDescription = contentDescription
        )
    }
}

@Composable
internal fun CommentEditorSheet(
    state: CommentEditorState,
    onDismiss: () -> Unit,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    OverlayBottomSheet(
        show = state.visible,
        onDismissRequest = {
            if (!state.loading) {
                onDismiss()
            }
        },
        title = if (state.target.isReply) "回复评论" else "发表评论"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (state.target.isReply) "回复评论" else "发表评论",
                        style = MiuixTheme.textStyles.subtitle
                    )
                    state.target.parentName?.takeIf(String::isNotBlank)?.let { name ->
                        Text(
                            text = "回复 @$name",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
                TextButton(
                    text = if (state.loading) "发送中" else "发送",
                    onClick = onSubmit,
                    enabled = state.canSubmit
                )
            }
            TextField(
                value = state.input,
                onValueChange = onInputChange,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                label = if (state.target.isReply) {
                    "输入你的回复"
                } else {
                    "发一条友善的评论"
                }
            )
        }
    }
}
