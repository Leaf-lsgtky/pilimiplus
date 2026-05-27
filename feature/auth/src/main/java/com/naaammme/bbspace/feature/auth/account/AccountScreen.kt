package com.naaammme.bbspace.feature.auth.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.AvatarImage
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.model.LoginCredential
import com.naaammme.bbspace.core.model.User
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onAddAccount: () -> Unit = {},
    onSwitched: () -> Unit = {}
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val currentMid by viewModel.currentMid.collectAsStateWithLifecycle()
    val userInfoMap by viewModel.userInfoMap.collectAsStateWithLifecycle()
    var pendingRemoveAccount by remember { mutableStateOf<LoginCredential?>(null) }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("账号管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            accounts.forEach { account ->
                    AccountCard(
                        account = account,
                        isCurrent = account.mid == currentMid,
                        userInfo = userInfoMap[account.mid],
                        onSwitch = { viewModel.switchAccount(account.mid); onSwitched() },
                        onRemove = { pendingRemoveAccount = account },
                        onLogout = { pendingRemoveAccount = account }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                GuestCard(
                    isCurrent = currentMid == 0L,
                    onSwitch = { viewModel.switchToGuest(); onSwitched() }
                )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddAccount,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加账号")
            }
        }
    }

    pendingRemoveAccount?.let { account ->
        val isCurrent = account.mid == currentMid
        OverlayDialog(
            onDismissRequest = { pendingRemoveAccount = null },
            title = { Text(if (isCurrent) "退出当前账号" else "删除账号") },
            message = {
                Text(
                    if (isCurrent) {
                        "确认退出并删除当前账号吗？"
                    } else {
                        "确认删除这个账号吗？"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingRemoveAccount = null
                        if (isCurrent) {
                            viewModel.logout(account)
                        } else {
                            viewModel.removeAccount(account.mid)
                        }
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoveAccount = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun GuestCard(
    isCurrent: Boolean,
    onSwitch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                url = null,
                contentDescription = "游客",
                modifier = Modifier.size(56.dp),
                fallbackContent = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "游客",
                    style = MiuixTheme.textStyles.body1,
                    fontWeight = FontWeight.Medium
                )
                if (isCurrent) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MiuixTheme.colorScheme.primary
                    )
                }
            }
            if (!isCurrent) {
                TextButton(
                    onClick = onSwitch,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text("切换", style = MiuixTheme.textStyles.footnote2)
                }
            }
        }
    }
}

//@Composable
//private fun EmptyState() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 48.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Icon(
//                Icons.Default.Person,
//                contentDescription = null,
//                modifier = Modifier.size(64.dp),
//                tint = MiuixTheme.colorScheme.onSurfaceVariant
//            )
//          Spacer(modifier = Modifier.height(12.dp))
//            Text(
//                text = "暂无账号",
//                style = MiuixTheme.textStyles.body1,
//                color = MiuixTheme.colorScheme.onSurfaceVariant
//            )
//      }
//    }
//}

@Composable
private fun AccountCard(
    account: LoginCredential,
    isCurrent: Boolean,
    userInfo: User?,
    onSwitch: () -> Unit,
    onRemove: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            AvatarImage(
                url = userInfo?.avatar?.takeIf(String::isNotBlank),
                contentDescription = userInfo?.name ?: "UID: ${account.mid}",
                modifier = Modifier.size(56.dp),
                fallbackContent = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = userInfo?.name ?: "UID: ${account.mid}",
                        style = MiuixTheme.textStyles.subtitle,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isCurrent) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MiuixTheme.colorScheme.primary
                        )
                    }
                }
                if (userInfo != null) {
                    Text(
                        text = "Lv${userInfo.level}  硬币 ${userInfo.coins.toInt()}",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                    if (userInfo.sign.isNotEmpty()) {
                        Text(
                            text = userInfo.sign,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = "UID: ${account.mid}",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 操作按钮
            if (!isCurrent) {
                TextButton(
                    onClick = onSwitch,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text("切换", style = MiuixTheme.textStyles.footnote2)
                }
            }

            IconButton(onClick = if (isCurrent) onLogout else onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = if (isCurrent) "退出登录" else "移除",
                    tint = MiuixTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
