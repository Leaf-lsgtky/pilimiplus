package com.naaammme.bbspace.feature.history

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.BiliPullToRefreshBox
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.designsystem.component.CoverImage
import com.naaammme.bbspace.core.designsystem.component.FilledTabRow
import com.naaammme.bbspace.core.designsystem.component.VideoListCardSkeleton
import com.naaammme.bbspace.core.model.HistoryItem
import com.naaammme.bbspace.core.model.HistoryTab
import com.naaammme.bbspace.core.model.HistoryTarget
import com.naaammme.bbspace.core.model.LiveRoute
import com.naaammme.bbspace.core.model.VideoTarget
import com.naaammme.bbspace.feature.history.component.HistoryEmptyState
import com.naaammme.bbspace.feature.history.component.HistoryErrorState
import com.naaammme.bbspace.feature.history.component.HistoryListLoading
import com.naaammme.bbspace.feature.history.component.formatVideoDuration
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenVideo: (VideoTarget) -> Unit,
    onOpenLive: (LiveRoute) -> Unit,
    onOpenDynamicDetail: (String, Int) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val uiState by rememberUpdatedState(state)

    LaunchedEffect(listState) {
        snapshotFlow {
            val total = listState.layoutInfo.totalItemsCount
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            total to last
        }
            .distinctUntilChanged()
            .filter { (total, last) ->
                uiState.canLoadMore &&
                        !uiState.isLoadingMore &&
                        uiState.errorMessage == null &&
                        total > 0 &&
                        last >= total - LOAD_MORE_TRIGGER_OFFSET
            }
            .collect {
                viewModel.loadMore()
            }
    }

    LaunchedEffect(state.tab) {
        val needScrollTop = listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        if (needScrollTop && listState.layoutInfo.totalItemsCount > 0) {
            listState.scrollToItem(0)
        }
    }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("历史记录") },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilledTabRow(
                tabs = HistoryTab.entries.map { it.title },
                selectedIndex = state.tab.ordinal,
                onSelect = { index -> viewModel.selectTab(HistoryTab.entries[index]) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )

            BiliPullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.weight(1f)
            ) {
                when {
                    state.isLoading && state.items.isEmpty() -> {
                        HistoryListLoading(
                            skeletonPrefix = "history_skeleton",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    state.errorMessage != null && state.items.isEmpty() -> {
                        HistoryErrorState(
                            message = state.errorMessage.orEmpty(),
                            fallbackMessage = "加载历史记录失败",
                            onRetry = viewModel::refresh,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    state.items.isEmpty() -> {
                        HistoryEmptyState(
                            text = "暂无${state.tab.title}历史",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = state.items,
                                key = { it.key },
                                contentType = { it.type }
                            ) { item ->
                                HistoryItemCard(
                                    item = item,
                                    onClick = {
                                        when (val target = item.target) {
                                            is HistoryTarget.Video -> onOpenVideo(target.target)
                                            is HistoryTarget.Live -> onOpenLive(target.route)
                                            is HistoryTarget.Article -> onOpenDynamicDetail(target.opusId, 1)
                                            null -> Unit
                                        }
                                    }
                                )
                            }

                            if (state.isLoadingMore) {
                                items(
                                    count = LOAD_MORE_SKELETON_COUNT,
                                    key = { index -> "history_loading_$index" },
                                    contentType = { "loading" }
                                ) {
                                    VideoListCardSkeleton()
                                }
                            }

                            if (state.errorMessage != null && state.items.isNotEmpty()) {
                                item(
                                    key = "history_error",
                                    contentType = "error"
                                ) {
                                    HistoryErrorState(
                                        message = state.errorMessage.orEmpty(),
                                        fallbackMessage = "加载历史记录失败",
                                        onRetry = if (state.errorOnLoadMore) {
                                            viewModel::loadMore
                                        } else {
                                            viewModel::refresh
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val content: @Composable () -> Unit = {
        HistoryItemContent(item = item)
    }

    if (item.isOpenable) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth()
        ) {
            content()
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun HistoryItemContent(
    item: HistoryItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HistoryCover(
            item = item,
            modifier = Modifier.weight(0.38f)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title,
                style = MiuixTheme.textStyles.subtitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildInfoLine(item),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildMetaLine(item),
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            progressText(item)?.let { progress ->
                Text(
                    text = progress,
                    style = MiuixTheme.textStyles.footnote2,
                    color = MiuixTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .background(
                            MiuixTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}

@Composable
private fun HistoryCover(
    item: HistoryItem,
    modifier: Modifier = Modifier
) {
    CoverImage(
        url = item.cover,
        contentDescription = item.title,
        modifier = modifier.aspectRatio(16f / 10f),
        fallbackContent = {
            Text(
                text = item.typeLabel,
                modifier = Modifier.align(Alignment.Center),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Text(
            text = item.typeLabel,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .background(Color.Black.copy(alpha = 0.56f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            style = MiuixTheme.textStyles.footnote2,
            color = Color.White
        )
    }
}

private fun buildInfoLine(item: HistoryItem): String {
    return listOfNotNull(
        item.ownerName,
        item.badge,
        item.subtitle
    ).joinToString(" · ").ifBlank { item.typeLabel }
}

private fun buildMetaLine(item: HistoryItem): String {
    return listOfNotNull(
        item.deviceLabel,
        DateFormat.format("MM-dd HH:mm", item.viewedAtSec * 1000).toString()
    ).joinToString(" · ")
}

private fun progressText(item: HistoryItem): String? {
    val progress = item.progressSec ?: return null
    if (progress < 0L) return "已看完"
    val duration = item.durationSec
    if (duration == null || duration <= 0L) return null
    return if (progress >= duration) {
        "已看完"
    } else {
        "进度 ${formatVideoDuration(progress)} / ${formatVideoDuration(duration)}"
    }
}

private const val LOAD_MORE_SKELETON_COUNT = 2
private const val LOAD_MORE_TRIGGER_OFFSET = 3
