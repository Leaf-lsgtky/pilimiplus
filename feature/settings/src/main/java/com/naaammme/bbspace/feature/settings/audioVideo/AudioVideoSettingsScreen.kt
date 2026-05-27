package com.naaammme.bbspace.feature.settings.audioVideo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AudioVideoSettingsScreen(
    onBack: () -> Unit,
    viewModel: AudioVideoSettingsViewModel = hiltViewModel()
) {
    val enableHdrAnd8k by viewModel.enableHdrAnd8k.collectAsStateWithLifecycle()
    val defaultVideoQuality by viewModel.defaultVideoQuality.collectAsStateWithLifecycle()
    val defaultAudioQuality by viewModel.defaultAudioQuality.collectAsStateWithLifecycle()
    val forceHost by viewModel.forceHost.collectAsStateWithLifecycle()
    val needTrial by viewModel.needTrial.collectAsStateWithLifecycle()
    val preferredCodec by viewModel.preferredCodec.collectAsStateWithLifecycle()
    val enableWebPlayback by viewModel.enableWebPlayback.collectAsStateWithLifecycle()

    var showVideoQualityDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showCodecDialog by remember { mutableStateOf(false) }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = "音视频设置",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingCategory(title = "画质")

            SwitchPreference(
                title = "启用 HDR 和 8K",
                subtitle = "允许请求 HDR 和 8K 视频流（如果视频支持）",
                checked = enableHdrAnd8k,
                onCheckedChange = { viewModel.updateEnableHdrAnd8k(it) }
            )

            SettingCategory(title = "本地选择")

            QualityItem(
                title = "默认视频画质",
                quality = defaultVideoQuality,
                onClick = { showVideoQualityDialog = true }
            )

            QualityItem(
                title = "默认音频质量",
                quality = defaultAudioQuality,
                onClick = { showAudioQualityDialog = true }
            )

            SettingCategory(title = "其他")

            QualityItem(
                title = "优先编码格式",
                quality = preferredCodec,
                label = getCodecName(preferredCodec),
                onClick = { showCodecDialog = true }
            )

            SwitchPreference(
                title = "使用https播放",
                checked = forceHost > 0,
                onCheckedChange = { viewModel.updateForceHost(if (it) 1 else 0) }
            )

            SwitchPreference(
                title = "需要4k",
                checked = needTrial,
                onCheckedChange = { viewModel.updateNeedTrial(it) }
            )

            SwitchPreference(
                title = "免登录看1080p",
                subtitle = "打开就算不登录也能看1080p喵",
                checked = enableWebPlayback,
                onCheckedChange = { viewModel.updateEnableWebPlayback(it) }
            )
        }

        if (showVideoQualityDialog) {
            VideoQualityDialog(
                currentQuality = defaultVideoQuality,
                onSelect = { viewModel.updateDefaultVideoQuality(it) },
                onDismiss = { showVideoQualityDialog = false }
            )
        }

        if (showAudioQualityDialog) {
            AudioQualityDialog(
                currentQuality = defaultAudioQuality,
                onSelect = { viewModel.updateDefaultAudioQuality(it) },
                onDismiss = { showAudioQualityDialog = false }
            )
        }

        if (showCodecDialog) {
            CodecDialog(
                currentCodec = preferredCodec,
                onSelect = { viewModel.updatePreferredCodec(it) },
                onDismiss = { showCodecDialog = false }
            )
        }
    }
}

@Composable
fun SettingCategory(title: String) {
    Text(
        text = title,
        style = MiuixTheme.textStyles.body2,
        color = MiuixTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun QualityItem(
    title: String,
    quality: Int,
    onClick: () -> Unit,
    label: String? = null
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title)
            Text(
                text = label ?: when {
                    quality == 0 -> "自动"
                    quality < 100 -> getVideoQualityName(quality)
                    else -> getAudioQualityName(quality)
                },
                style = MiuixTheme.textStyles.footnote1
            )
        }
    }
}

@Composable
fun VideoQualityDialog(
    currentQuality: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val qualities = listOf(16, 32, 64, 80, 112, 116, 120, 125, 126, 127)
    OverlayDialog(
        show = true,
        onDismissRequest = onDismiss,
        title = "选择默认视频画质",
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                qualities.forEach { quality ->
                    Text(
                        text = if (quality == currentQuality) "✓ ${getVideoQualityName(quality)}" else getVideoQualityName(quality),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(quality); onDismiss() }
                            .padding(vertical = 8.dp)
                    )
                }
            }
            TextButton(
                text = "关闭",
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun AudioQualityDialog(
    currentQuality: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val qualities = listOf(0, 30216, 30232, 30280, 30250, 30251)
    OverlayDialog(
        show = true,
        onDismissRequest = onDismiss,
        title = "选择默认音频质量",
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                qualities.forEach { quality ->
                    Text(
                        text = if (quality == currentQuality) "✓ ${getAudioQualityName(quality)}" else getAudioQualityName(quality),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(quality); onDismiss() }
                            .padding(vertical = 8.dp)
                    )
                }
            }
            TextButton(
                text = "关闭",
                onClick = onDismiss
            )
        }
    )
}

private fun getVideoQualityName(quality: Int): String {
    return when (quality) {
        16 -> "360P"
        32 -> "480P"
        64 -> "720P"
        80 -> "1080P"
        112 -> "1080P+"
        116 -> "1080P 60fps"
        120 -> "4K"
        125 -> "HDR"
        126 -> "杜比视界"
        127 -> "8K"
        else -> "未知"
    }
}

private fun getAudioQualityName(quality: Int): String {
    return when (quality) {
        0 -> "自动"
        30216 -> "64K"
        30232 -> "132K"
        30280 -> "192K"
        30250 -> "杜比全景声"
        30251 -> "Hi-Res 无损"
        else -> "未知"
    }
}

@Composable
fun CodecDialog(
    currentCodec: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val codecs = listOf(1, 2, 3)
    OverlayDialog(
        show = true,
        onDismissRequest = onDismiss,
        title = "选择优先编码格式",
        content = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                codecs.forEach { codec ->
                    Text(
                        text = if (codec == currentCodec) "✓ ${getCodecName(codec)}" else getCodecName(codec),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(codec); onDismiss() }
                            .padding(vertical = 8.dp)
                    )
                }
            }
            TextButton(
                text = "关闭",
                onClick = onDismiss
            )
        }
    )
}

private fun getCodecName(codec: Int): String {
    return when (codec) {
        1 -> "AVC/H.264"
        2 -> "HEVC/H.265"
        3 -> "AV1"
        else -> "未知"
    }
}
