package com.naaammme.bbspace.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Immutable
data class AppUpdateDialogState(
    val title: String,
    val desc: String,
    val confirmText: String? = null,
    val url: String? = null
)

@Composable
fun AppUpdateDialog(
    state: AppUpdateDialogState,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    OverlayDialog(
        show = true,
        onDismissRequest = onDismiss,
        title = state.title
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = state.desc,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.url != null) {
                TextButton(
                    text = "关闭",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(
                text = state.confirmText ?: "知道了",
                onClick = {
                    val url = state.url
                    if (url == null) {
                        onDismiss()
                    } else {
                        onOpenUrl(url)
                    }
                },
                colors = if (state.url != null) ButtonDefaults.textButtonColorsPrimary() else ButtonDefaults.textButtonColors(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
