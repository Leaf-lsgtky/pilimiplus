package com.naaammme.bbspace.feature.user.component

import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog

@Composable
fun AccountExpiredDialog(onDismiss: () -> Unit) {
    OverlayDialog(
        show = true,
        onDismissRequest = onDismiss,
        title = "账号已过期",
        summary = "当前账号已经过期，请删除账号。",
        content = {
            TextButton(
                text = "知道了",
                onClick = onDismiss
            )
        }
    )
}
