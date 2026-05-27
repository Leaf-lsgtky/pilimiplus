package com.naaammme.bbspace.feature.settings.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.feature.settings.SettingsViewModel
import kotlin.math.roundToInt
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun FeedSettingsScreen(
    onBack: () -> Unit,
    onNavigateToInterest: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val hdFeed by viewModel.hdFeed.collectAsStateWithLifecycle()
    val hdFeedAvailable by viewModel.hdFeedAvailable.collectAsStateWithLifecycle()
    val personalizedRcmd by viewModel.personalizedRcmd.collectAsStateWithLifecycle()
    val lessonsMode by viewModel.lessonsMode.collectAsStateWithLifecycle()
    val teenagersMode by viewModel.teenagersMode.collectAsStateWithLifecycle()
    val teenagersAge by viewModel.teenagersAge.collectAsStateWithLifecycle()
    var teenagersAgeDraft by remember(teenagersAge) { mutableFloatStateOf(teenagersAge.toFloat()) }
    val teenagersAgeText = teenagersAgeDraft.roundToInt().coerceIn(1, 17).toString()

    LaunchedEffect(Unit) {
        viewModel.refreshHdFeedAvailable()
    }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = "推荐设置",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, "返回")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SwitchPreference(
                    title = "HD 推荐模式",
                    subtitle = if (hdFeedAvailable) {
                        "切换 HD 推荐接口，每页返回更多条目"
                    } else {
                        "需先扫码绑定当前账号 HD key"
                    },
                    checked = hdFeed,
                    enabled = hdFeedAvailable,
                    onCheckedChange = viewModel::updateHdFeed
                )
            }
            item {
                SwitchPreference(
                    title = "个性化推荐",
                    subtitle = "基于观看历史推荐内容，关闭后随机推荐",
                    checked = personalizedRcmd,
                    onCheckedChange = viewModel::updatePersonalizedRcmd
                )
            }
            item {
                SwitchPreference(
                    title = "课堂推荐模式",
                    subtitle = "只推荐学习相关视频",
                    checked = lessonsMode,
                    onCheckedChange = viewModel::updateLessonsMode
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("未成年推荐", style = MiuixTheme.textStyles.subtitle)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "按指定年龄请求未成年推荐内容",
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                            Switch(
                                checked = teenagersMode,
                                onCheckedChange = viewModel::updateTeenagersMode
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "年龄",
                                style = MiuixTheme.textStyles.body2,
                                color = if (teenagersMode) {
                                    MiuixTheme.colorScheme.onSurface
                                } else {
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                                }
                            )
                            Text(
                                teenagersAgeText,
                                style = MiuixTheme.textStyles.body2,
                                color = if (teenagersMode) {
                                    MiuixTheme.colorScheme.primary
                                } else {
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                                }
                            )
                        }
                        Slider(
                            value = teenagersAgeDraft,
                            onValueChange = { teenagersAgeDraft = it },
                            onValueChangeFinished = {
                                viewModel.updateTeenagersAge(teenagersAgeDraft.roundToInt())
                            },
                            enabled = teenagersMode,
                            valueRange = 1f..17f,
                            steps = 15
                        )
                    }
                }
            }
            item {
                ArrowPreference(
                    icon = MiuixIcons.Settings,
                    title = "内容偏好调节",
                    subtitle = "查看和调整你的内容兴趣标签",
                    onClick = onNavigateToInterest
                )
            }
        }
    }
}
