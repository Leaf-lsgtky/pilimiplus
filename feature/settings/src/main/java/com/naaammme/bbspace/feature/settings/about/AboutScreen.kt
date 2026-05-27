package com.naaammme.bbspace.feature.settings.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.designsystem.component.AppUpdateDialog as CoreAppUpdateDialog
import com.naaammme.bbspace.feature.settings.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    versionName: String,
    versionCode: Long,
    vm: AboutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val updateState by vm.updateState.collectAsStateWithLifecycle()
    val autoCheckUpdate by vm.autoCheckUpdate.collectAsStateWithLifecycle()
    val updateDialog by vm.updateDialog.collectAsStateWithLifecycle()

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("关于") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.about_banner),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(0.56f)
                                .aspectRatio(1f),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            "v$versionName - $versionCode",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = vm::checkUpdate,
                    enabled = updateState != UpdateState.Checking
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("检查更新", style = MiuixTheme.textStyles.subtitle)
                        when (val s = updateState) {
                            is UpdateState.Idle -> Text(
                                "点击这里检查新版本",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            is UpdateState.Checking -> Text(
                                "正在检查...",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            is UpdateState.UpToDate -> Text(
                                "已是最新版本",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.primary
                            )
                            is UpdateState.HasUpdate -> Text(
                                "发现新版本 v${s.version}",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.primary
                            )
                            is UpdateState.Error -> Text(
                                "检查失败 点击重试",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                SwitchPreference(
                    title = "自动检查更新",
                    subtitle = "应用初始化时自动检查并弹出更新说明",
                    checked = autoCheckUpdate,
                    onCheckedChange = vm::updateAutoCheckEnabled
                )
            }

            item {
                LinkCard(
                    title = "加入 QQ 群",
                    subtitle = "924787418",
                    onClick = {
                        val qqGroupNumber = "924787418"
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("QQ群号", qqGroupNumber))
                        Toast.makeText(context, "已复制QQ群号", Toast.LENGTH_SHORT).show()
                        runCatching {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$qqGroupNumber&card_type=group&source=qrcode")
                                )
                            )
                        }
                    }
                )
            }

            item {
                LinkCard(
                    title = "加入 Telegram 群组",
                    subtitle = "t.me/ourbbspace",
                    url = "https://t.me/ourbbspace"
                )
            }

            item {
                LinkCard(
                    title = "GitHub 开源仓库",
                    subtitle = "github.com/naaammme/bbspace",
                    url = "https://github.com/naaammme/bbspace"
                )
            }
        }
    }

    updateDialog?.let { release ->
        CoreAppUpdateDialog(
            state = release,
            onDismiss = vm::dismissUpdateDialog,
            onOpenUrl = {
                vm.dismissUpdateDialog()
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
        )
    }
}

@Composable
private fun LinkCard(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    url: String? = null
) {
    val context = LocalContext.current
    val enabled = onClick != null || url != null
    val resolvedOnClick: () -> Unit = onClick ?: {
        if (url != null) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = resolvedOnClick,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title, style = MiuixTheme.textStyles.subtitle)
            Text(
                subtitle,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}
