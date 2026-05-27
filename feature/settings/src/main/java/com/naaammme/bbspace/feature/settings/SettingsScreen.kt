package com.naaammme.bbspace.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.designsystem.component.SearchCapsuleField
import com.naaammme.bbspace.feature.settings.navigation.APPEARANCE_ROUTE
import com.naaammme.bbspace.feature.settings.navigation.FEED_SETTINGS_ROUTE
import com.naaammme.bbspace.feature.settings.navigation.PERFORMANCE_ROUTE
import com.naaammme.bbspace.feature.settings.navigation.PLAYBACK_ROUTE
import com.naaammme.bbspace.feature.settings.navigation.PRIVACY_ROUTE
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.icon.icons.Info
import top.yukonga.miuix.kmp.icon.icons.Settings
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToPerformance: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToPlayback: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToErrorLog: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

    val routeNav = mapOf(
        APPEARANCE_ROUTE to onNavigateToAppearance,
        PERFORMANCE_ROUTE to onNavigateToPerformance,
        FEED_SETTINGS_ROUTE to onNavigateToFeed,
        PLAYBACK_ROUTE to onNavigateToPlayback,
        PRIVACY_ROUTE to onNavigateToPrivacy,
    )

    val filtered = remember(query) {
        if (query.isBlank()) {
            emptyList()
        } else {
            allSettingEntries.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.subtitle.contains(query, ignoreCase = true)
            }
        }
    }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SearchCapsuleField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "搜索设置",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (query.isNotBlank()) {
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("没有匹配结果", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        }
                    }
                } else {
                    items(filtered) { entry ->
                        ArrowPreference(
                            title = entry.title,
                            subtitle = entry.subtitle,
                            onClick = { routeNav[entry.route]?.invoke() }
                        )
                    }
                }
            } else {
                item {
                    ArrowPreference(
                        icon = Icons.Default.Edit,
                        title = "外观设置",
                        subtitle = "主题 颜色 字体",
                        onClick = onNavigateToAppearance
                    )
                }
                item {
                    ArrowPreference(
                        icon = MiuixIcons.Settings,
                        title = "性能设置",
                        subtitle = "刷新率和渲染策略",
                        onClick = onNavigateToPerformance
                    )
                }
                item {
                    ArrowPreference(
                        icon = Icons.Default.PlayArrow,
                        title = "音视频设置",
                        subtitle = "画质 音质 和编码格式",
                        onClick = onNavigateToPlayback
                    )
                }
                item {
                    ArrowPreference(
                        icon = MiuixIcons.Settings,
                        title = "推荐设置",
                        subtitle = "HD 推荐模式",
                        onClick = onNavigateToFeed
                    )
                }
                item {
                    ArrowPreference(
                        icon = Icons.Default.Lock,
                        title = "隐私安全",
                        subtitle = "历史记录和缓存管理",
                        onClick = onNavigateToPrivacy
                    )
                }
                item {
                    ArrowPreference(
                        icon = MiuixIcons.Info,
                        title = "关于",
                        subtitle = "版本信息和开源许可",
                        onClick = onNavigateToAbout
                    )
                }
                item {
                    ArrowPreference(
                        icon = Icons.Default.Warning,
                        title = "错误日志",
                        subtitle = "查看和导出应用错误记录",
                        onClick = onNavigateToErrorLog
                    )
                }
                item {
                    Card(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "恢复默认设置",
                                    style = MiuixTheme.textStyles.subtitle,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                                Text(
                                    text = "一键重置外观 音视频 推荐和隐私等设置",
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showResetDialog) {
            OverlayDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("恢复默认设置") },
                text = {
                    Text(
                        "这会把当前各项设置恢复到默认值 不会退出登录"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetAllSettings()
                            showResetDialog = false
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}
