package com.naaammme.bbspace.feature.download

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.designsystem.component.CoverImage
import com.naaammme.bbspace.core.designsystem.component.FilledTabRow
import com.naaammme.bbspace.core.model.VideoDownloadKind
import com.naaammme.bbspace.core.model.VideoDownloadOption
import com.naaammme.bbspace.core.model.VideoDownloadOptions
import com.naaammme.bbspace.core.model.VideoDownloadProgress
import com.naaammme.bbspace.core.model.VideoDownloadTask
import com.naaammme.bbspace.core.model.VideoDownloadTaskStatus
import com.naaammme.bbspace.core.model.summaryLabel
import java.util.Locale

@Composable
fun DownloadScreen(
    onBack: () -> Unit,
    onOpenPlayer: (Long) -> Unit,
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("离线缓存") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = "返回"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        DownloadContent(
            state = state,
            onSelectTab = viewModel::selectTab,
            onInputChange = viewModel::updateInput,
            onSelectKind = viewModel::selectKind,
            onSelectQuality = viewModel::selectQuality,
            onSelectAudio = viewModel::selectAudio,
            onStartInputTask = viewModel::startInputTask,
            onStartDownload = viewModel::startDownload,
            onPauseTask = viewModel::pauseTask,
            onResumeTask = viewModel::resumeTask,
            onDeleteTask = viewModel::deleteTask,
            onExportTask = viewModel::exportTask,
            onOpenPlayer = onOpenPlayer,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun DownloadContent(
    state: DownloadUiState,
    onSelectTab: (DownloadTab) -> Unit,
    onInputChange: (String) -> Unit,
    onSelectKind: (VideoDownloadKind) -> Unit,
    onSelectQuality: (Int) -> Unit,
    onSelectAudio: (Int) -> Unit,
    onStartInputTask: () -> Unit,
    onStartDownload: () -> Unit,
    onPauseTask: (Long) -> Unit,
    onResumeTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onExportTask: (VideoDownloadTask) -> Unit,
    onOpenPlayer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FilledTabRow(
            tabs = DownloadTab.entries.map { it.title },
            selectedIndex = state.tab.ordinal,
            onSelect = { index -> onSelectTab(DownloadTab.entries[index]) },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )

        when (state.tab) {
            DownloadTab.CONFIG -> ConfigTab(
                state = state,
                onInputChange = onInputChange,
                onSelectKind = onSelectKind,
                onSelectQuality = onSelectQuality,
                onSelectAudio = onSelectAudio,
                onStartInputTask = onStartInputTask,
                onStartDownload = onStartDownload,
                modifier = Modifier.fillMaxSize()
            )

            DownloadTab.QUEUE -> QueueTab(
                state = state,
                onPauseTask = onPauseTask,
                onResumeTask = onResumeTask,
                onDeleteTask = onDeleteTask,
                onExportTask = onExportTask,
                onOpenPlayer = onOpenPlayer,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ConfigTab(
    state: DownloadUiState,
    onInputChange: (String) -> Unit,
    onSelectKind: (VideoDownloadKind) -> Unit,
    onSelectQuality: (Int) -> Unit,
    onSelectAudio: (Int) -> Unit,
    onStartInputTask: () -> Unit,
    onStartDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canDownload = state.hasTask && !state.loading

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item("input") {
            InputCard(
                input = state.input,
                enabled = !state.loading,
                hasTask = state.hasTask,
                canDownload = canDownload,
                onInputChange = onInputChange,
                onClear = { onInputChange("") },
                onStart = onStartInputTask,
                onDownload = onStartDownload
            )
        }
        state.pendingTitle?.let { title ->
            item("pending") {
                StateCard("已解析目标：$title")
            }
        }
        item("kind") {
            KindCard(state.kind, onSelectKind)
        }
        if (state.kind == VideoDownloadKind.VIDEO) {
            item("video_quality") {
                QualityCard(
                    title = "视频画质偏好",
                    options = VideoDownloadOptions.videoQualities,
                    selected = state.videoQuality,
                    onSelect = onSelectQuality
                )
            }
        }
        item("audio_quality") {
            QualityCard(
                title = "音频质量偏好",
                options = VideoDownloadOptions.audioQualities,
                selected = state.audioQuality,
                onSelect = onSelectAudio
            )
        }
        if (state.loading) {
            item("loading") {
                StateCard("正在解析缓存目标")
            }
        }
        state.error?.takeIf(String::isNotBlank)?.let { message ->
            item("error") {
                StateCard(message, isError = true)
            }
        }
    }
}

@Composable
private fun QueueTab(
    state: DownloadUiState,
    onPauseTask: (Long) -> Unit,
    onResumeTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onExportTask: (VideoDownloadTask) -> Unit,
    onOpenPlayer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks = state.tasks.sortedWith(
        compareBy<VideoDownloadTask>({ taskOrder(it) }, { it.id })
    )
    var pendingDelete by remember { mutableStateOf<VideoDownloadTask?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.export.message?.takeIf(String::isNotBlank)?.let { message ->
            item("export_message") {
                StateCard(message, isError = state.export.isError)
            }
        }
        if (tasks.isEmpty()) {
            item("queue_empty") {
                StateCard("暂无缓存任务")
            }
        } else {
            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                TaskCard(
                    task = task,
                    onPauseTask = onPauseTask,
                    onResumeTask = onResumeTask,
                    onDeleteTask = { pendingDelete = task },
                    onExportTask = onExportTask,
                    export = state.export,
                    onOpenPlayer = onOpenPlayer
                )
            }
        }
    }

    pendingDelete?.let { task ->
        OverlayDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除缓存") },
            message = { Text("确认删除 ${task.title} 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                        onDeleteTask(task.id)
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun InputCard(
    input: String,
    enabled: Boolean,
    hasTask: Boolean,
    canDownload: Boolean,
    onInputChange: (String) -> Unit,
    onClear: () -> Unit,
    onStart: () -> Unit,
    onDownload: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("缓存目标", style = MiuixTheme.textStyles.subtitle)
            TextField(
                value = input,
                onValueChange = onInputChange,
                enabled = enabled,
                singleLine = true,
                label = { Text("链接、av号或BV号") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStart,
                    enabled = enabled && input.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("解析目标")
                }
                if (hasTask) {
                    Button(
                        onClick = onDownload,
                        enabled = canDownload,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("加入队列")
                    }
                } else {
                    TextButton(
                        onClick = onClear,
                        enabled = enabled && input.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("清空输入")
                    }
                }
            }
        }
    }
}

@Composable
private fun KindCard(
    selected: VideoDownloadKind,
    onSelect: (VideoDownloadKind) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("缓存内容", style = MiuixTheme.textStyles.subtitle)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val videoSelected = selected == VideoDownloadKind.VIDEO
                Surface(
                    color = if (videoSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onSelect(VideoDownloadKind.VIDEO) }
                ) {
                    Text(
                        "缓存视频",
                        color = if (videoSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                val audioSelected = selected == VideoDownloadKind.AUDIO
                Surface(
                    color = if (audioSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onSelect(VideoDownloadKind.AUDIO) }
                ) {
                    Text(
                        "缓存音频",
                        color = if (audioSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QualityCard(
    title: String,
    options: List<VideoDownloadOption>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MiuixTheme.textStyles.subtitle)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSelected = option.value == selected
                    Surface(
                        color = if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { onSelect(option.value) }
                    ) {
                        Text(
                            text = option.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: VideoDownloadTask,
    onPauseTask: (Long) -> Unit,
    onResumeTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onExportTask: (VideoDownloadTask) -> Unit,
    export: DownloadExportState,
    onOpenPlayer: (Long) -> Unit
) {
    val canToggle = task.status != VideoDownloadTaskStatus.DONE &&
            task.status != VideoDownloadTaskStatus.FAILED
    val exporting = export.taskId == task.id
    val exportProgress = export.progress.takeIf { exporting }
    val exportEnabled = export.taskId == null

    @Composable
    fun DeleteButton(modifier: Modifier) {
        TextButton(
            onClick = { onDeleteTask(task.id) },
            modifier = modifier
        ) {
            Text("删除")
        }
    }

    Card(
        onClick = { onOpenPlayer(task.id) },
        enabled = task.isPlayable,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CoverImage(
                    url = task.cover,
                    contentDescription = task.title,
                    modifier = Modifier
                        .width(112.dp)
                        .aspectRatio(16f / 10f)
                ) {
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MiuixTheme.textStyles.subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    ownerLine(task)?.let { owner ->
                        Text(
                            text = owner,
                            style = MiuixTheme.textStyles.footnote1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MiuixTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = task.summaryLabel(),
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = taskStatusText(task),
                style = MiuixTheme.textStyles.footnote1,
                color = statusColor(task)
            )
            when (val progress = task.progress) {
                is VideoDownloadProgress.Downloading -> {
                    val fraction = if (progress.totalBytes > 0L) {
                        progress.doneBytes.toFloat() / progress.totalBytes.toFloat()
                    } else {
                        0f
                    }
                    LinearProgressIndicator(
                        progress = { fraction.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                VideoDownloadProgress.Preparing -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                else -> Unit
            }
            if (canToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (task.status == VideoDownloadTaskStatus.PAUSED) {
                        Button(
                            onClick = { onResumeTask(task.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("继续")
                        }
                    } else {
                        TextButton(
                            onClick = { onPauseTask(task.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("暂停")
                        }
                    }
                    DeleteButton(Modifier.weight(1f))
                }
            } else if (task.status == VideoDownloadTaskStatus.DONE && task.isPlayable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onExportTask(task) },
                        enabled = exportEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (exporting) {
                                exportProgress?.let { "导出中 $it%" } ?: "导出中"
                            } else {
                                "导出"
                            }
                        )
                    }
                    DeleteButton(Modifier.weight(1f))
                }
                exportProgress?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0, 100) / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                DeleteButton(Modifier.fillMaxWidth())
            }
            task.error?.takeIf(String::isNotBlank)?.let { message ->
                Text(
                    text = message,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun StateCard(
    text: String,
    isError: Boolean = false
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.body2,
            color = if (isError) {
                MiuixTheme.colorScheme.error
            } else {
                MiuixTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun taskOrder(task: VideoDownloadTask): Int {
    return when (task.status) {
        VideoDownloadTaskStatus.RUNNING -> 0
        VideoDownloadTaskStatus.WAITING -> 1
        VideoDownloadTaskStatus.PAUSED -> 2
        VideoDownloadTaskStatus.FAILED -> 3
        VideoDownloadTaskStatus.DONE -> 4
    }
}

private fun taskStatusText(task: VideoDownloadTask): String {
    return when (task.status) {
        VideoDownloadTaskStatus.WAITING -> "等待缓存"
        VideoDownloadTaskStatus.PAUSED -> "已暂停"
        VideoDownloadTaskStatus.RUNNING -> when (val progress = task.progress) {
            VideoDownloadProgress.Preparing -> "准备缓存"
            is VideoDownloadProgress.Downloading -> {
                "正在缓存${progress.label} ${formatBytes(progress.doneBytes)} / ${formatBytes(progress.totalBytes)}"
            }
            VideoDownloadProgress.Done -> "缓存完成"
            null -> "缓存中"
        }
        VideoDownloadTaskStatus.DONE -> "缓存完成"
        VideoDownloadTaskStatus.FAILED -> "缓存失败"
    }
}

@Composable
private fun statusColor(task: VideoDownloadTask) = when (task.status) {
    VideoDownloadTaskStatus.FAILED -> MiuixTheme.colorScheme.error
    VideoDownloadTaskStatus.DONE -> MiuixTheme.colorScheme.primary
    VideoDownloadTaskStatus.RUNNING -> MiuixTheme.colorScheme.onSecondaryContainer
    VideoDownloadTaskStatus.PAUSED -> MiuixTheme.colorScheme.onSurfaceVariant
    VideoDownloadTaskStatus.WAITING -> MiuixTheme.colorScheme.onSurfaceVariant
}

private fun ownerLine(task: VideoDownloadTask): String? {
    val uid = task.ownerUid?.takeIf { it > 0L }?.let { "UID $it" }
    return listOfNotNull(
        task.ownerName?.takeIf(String::isNotBlank),
        uid
    ).takeIf { it.isNotEmpty() }?.joinToString(" · ")
}

private fun formatBytes(value: Long): String {
    if (value <= 0L) return "未知"
    val kb = value / 1024f
    val mb = kb / 1024f
    val gb = mb / 1024f
    return when {
        gb >= 1f -> String.format(Locale.ROOT, "%.1f GB", gb)
        mb >= 1f -> String.format(Locale.ROOT, "%.1f MB", mb)
        kb >= 1f -> String.format(Locale.ROOT, "%.1f KB", kb)
        else -> "$value B"
    }
}
