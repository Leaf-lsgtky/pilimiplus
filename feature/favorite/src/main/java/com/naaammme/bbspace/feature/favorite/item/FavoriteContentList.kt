package com.naaammme.bbspace.feature.favorite.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.designsystem.component.CoverImage
import com.naaammme.bbspace.core.designsystem.component.VideoListCardSkeleton
import com.naaammme.bbspace.core.model.FavoriteContentItem
import com.naaammme.bbspace.core.model.FavoriteContentTarget
import com.naaammme.bbspace.feature.favorite.FavoriteErrorState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun FavoriteContentList(
    items: List<FavoriteContentItem>,
    isLoadingMore: Boolean,
    errorMessage: String?,
    errorOnLoadMore: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onOpenContent: (FavoriteContentTarget) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val shouldLoadMore by rememberUpdatedState(canLoadMore)
    val hasLoadMoreError by rememberUpdatedState(errorOnLoadMore)

    LaunchedEffect(listState) {
        snapshotFlow {
            val total = listState.layoutInfo.totalItemsCount
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            total to last
        }
            .distinctUntilChanged()
            .filter { (total, last) ->
                shouldLoadMore &&
                    !hasLoadMoreError &&
                    total > 0 &&
                    last >= total - LOAD_MORE_TRIGGER_OFFSET
            }
            .collect {
                onLoadMore()
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = items,
            key = { it.key },
            contentType = { it.otype }
        ) { item ->
            FavoriteContentCard(
                item = item,
                onOpenContent = onOpenContent
            )
        }

        if (isLoadingMore) {
            items(
                count = LOAD_MORE_SKELETON_COUNT,
                key = { index -> "favorite_content_loading_$index" },
                contentType = { "loading" }
            ) {
                VideoListCardSkeleton()
            }
        }

        if (errorMessage != null && errorOnLoadMore) {
            item(
                key = "favorite_content_error",
                contentType = "error"
            ) {
                FavoriteErrorState(
                    message = errorMessage,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
private fun FavoriteContentCard(
    item: FavoriteContentItem,
    onOpenContent: (FavoriteContentTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardModifier = modifier.fillMaxWidth()
    val containerColor = MiuixTheme.colorScheme.surfaceContainerLow
    val target = item.target
    if (target != null) {
        Card(
            modifier = cardModifier.clickable { onOpenContent(target) },
            containerColor = containerColor
        ) {
            FavoriteContent(item = item)
        }
    } else {
        Card(
            modifier = cardModifier,
            containerColor = containerColor
        ) {
            FavoriteContent(item = item)
        }
    }
}

@Composable
private fun FavoriteContent(
    item: FavoriteContentItem,
    modifier: Modifier = Modifier
) {
    val meta = buildMetaLine(item)
    val ownerName = item.ownerName?.takeIf { it.isNotBlank() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CoverImage(
            url = item.cover,
            contentDescription = null,
            modifier = Modifier
                .weight(0.38f)
                .aspectRatio(16f / 10f)
        ) {
            item.playbackDesc?.let { text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.56f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MiuixTheme.textStyles.footnote2,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }

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
            ownerName?.let { name ->
                Text(
                    text = name,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun buildMetaLine(item: FavoriteContentItem): String {
    return buildList {
        item.typeDesc?.takeIf { it.isNotBlank() }?.let(::add)
        item.viewText?.takeIf { it.isNotBlank() }?.let { add("播放 $it") }
        item.danmakuText?.takeIf { it.isNotBlank() }?.let { add("弹幕 $it") }
        if (item.isInvalid) add("已失效")
    }.joinToString(" · ")
}

private const val LOAD_MORE_SKELETON_COUNT = 2
private const val LOAD_MORE_TRIGGER_OFFSET = 3
