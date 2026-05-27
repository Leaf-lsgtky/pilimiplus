package com.naaammme.bbspace.feature.bbspace.playback

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CoverImage
import com.naaammme.bbspace.core.model.PlaybackHistory
import com.naaammme.bbspace.feature.bbspace.rememberExportJson
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Contacts
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun PlaybackHistoryPane(
    modifier: Modifier = Modifier,
    vm: PlaybackHistoryViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val exportJson = rememberExportJson()
    val context = LocalContext.current
    var pendingDelete by remember { mutableStateOf<PlaybackHistory?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        item {
            PlaybackHistoryManageCard(
                count = state.items.size,
                onExport = {
                    if (state.items.isEmpty()) {
                        toast(context, "暂无播放历史")
                    } else {
                        exportJson(
                            "bbspace_playback_history.json",
                            vm.export(state.items)
                        )
                    }
                },
                onClear = {
                    if (state.items.isEmpty()) {
                        toast(context, "暂无可删除记录")
                    } else {
                        showClearDialog = true
                    }
                }
            )
        }

        if (state.items.isEmpty()) {
            item {
                EmptyPlaybackHistory()
            }
        } else {
            items(
                items = state.items,
                key = { it.id }
            ) { item ->
                PlaybackHistoryCard(
                    item = item,
                    onDelete = { pendingDelete = item }
                )
            }
        }
    }

    pendingDelete?.let { item ->
        OverlayDialog(
            show = true,
            onDismissRequest = { pendingDelete = null },
            title = "删除记录",
            summary = "删除 ${item.title.ifBlank { "视频 ${item.aid}" }}",
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        text = "取消",
                        onClick = { pendingDelete = null }
                    )
                    TextButton(
                        text = "删除",
                        onClick = {
                            pendingDelete = null
                            vm.delete(item)
                        }
                    )
                }
            }
        )
    }

    if (showClearDialog) {
        OverlayDialog(
            show = true,
            onDismissRequest = { showClearDialog = false },
            title = "清空播放历史",
            summary = "这会删除所有本地播放历史记录",
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        text = "取消",
                        onClick = { showClearDialog = false }
                    )
                    TextButton(
                        text = "清空",
                        onClick = {
                            showClearDialog = false
                            vm.clear()
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun PlaybackHistoryManageCard(
    count: Int,
    onExport: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "播放历史",
                style = MiuixTheme.textStyles.subtitle,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "当前有 $count 条本地记录",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    text = "导出",
                    onClick = onExport,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    text = "清空",
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EmptyPlaybackHistory() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "还没有本地播放历史",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

@Composable
private fun PlaybackHistoryCard(
    item: PlaybackHistory,
    onDelete: () -> Unit
) {
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    val title = remember(item.title, item.aid) {
        item.title.ifBlank { "视频 ${item.aid}" }
    }
    val sub = remember(item.part, item.partTitle, item.ownerName) {
        buildList {
            item.part?.let { add("P$it") }
            item.partTitle?.takeIf(String::isNotBlank)?.let(::add)
            item.ownerName?.takeIf(String::isNotBlank)?.let(::add)
        }.joinToString(" · ")
    }
    val progress = remember(item.progressMs, item.durationMs, item.finished) {
        buildString {
            append(formatMs(item.progressMs))
            if (item.durationMs > 0L) {
                append(" / ")
                append(formatMs(item.durationMs))
            }
            if (item.finished) {
                append(" · 已看完")
            }
        }
    }
    val updated = remember(item.updatedAt) {
        if (item.updatedAt <= 0L) {
            "--"
        } else {
            DateFormat.format("yyyy-MM-dd HH:mm", item.updatedAt).toString()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaybackHistoryCover(item = item)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.subtitle,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (expanded && sub.isNotBlank()) {
                    Text(
                        text = sub,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
                if (expanded) {
                    Text(
                        text = "UID ${item.uid}",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Text(
                        text = "进度 $progress",
                        style = MiuixTheme.textStyles.body2
                    )
                    Text(
                        text = "最后更新 $updated",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    TextButton(
                        text = "删除",
                        onClick = onDelete,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Text(
                text = if (expanded) "收起" else "展开",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

@Composable
private fun PlaybackHistoryCover(item: PlaybackHistory) {
    if (!item.cover.isNullOrBlank()) {
        CoverImage(
            url = item.cover,
            contentDescription = item.title,
            modifier = Modifier
                .size(width = 96.dp, height = 60.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .size(width = 96.dp, height = 60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MiuixTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (item.ownerName.isNullOrBlank()) {
                    MiuixIcons.Play
                } else {
                    MiuixIcons.Contacts
                },
                contentDescription = null,
                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

private fun toast(
    context: Context,
    text: String
) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

private fun formatMs(value: Long): String {
    val totalSec = (value.coerceAtLeast(0L) / 1000L).toInt()
    val hour = totalSec / 3600
    val minute = (totalSec % 3600) / 60
    val second = totalSec % 60
    return if (hour > 0) {
        "%d:%02d:%02d".format(hour, minute, second)
    } else {
        "%02d:%02d".format(minute, second)
    }
}
