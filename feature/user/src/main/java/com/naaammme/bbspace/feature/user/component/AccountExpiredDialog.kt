package com.naaammme.bbspace.feature.user.component

import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog

@Composable
fun AccountExpiredDialog(onDismiss: () -> Unit) {
    OverlayDialog(
        onDismissRequest = onDismiss,
        title = { Text("账号已过期") },
        message = { Text("当前账号已经过期，请删除账号。") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("知道了")
            }
        }
    )
}
