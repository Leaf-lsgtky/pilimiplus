package com.naaammme.bbspace.feature.home.video

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.designsystem.component.AdaptiveMediaGrid
import com.naaammme.bbspace.core.designsystem.component.CoverImage
import com.naaammme.bbspace.core.designsystem.component.VideoGridCardSkeleton
import com.naaammme.bbspace.core.model.FeedItem
import com.naaammme.bbspace.core.model.LiveRoute
import com.naaammme.bbspace.core.model.SpaceRoute
import com.naaammme.bbspace.core.model.ThreePointItem
import com.naaammme.bbspace.core.model.ThreePointReason
import com.naaammme.bbspace.core.model.VideoTarget
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.More
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.overlay.OverlayDialog

@Composable
fun HomeVideoPage(
    items: List<FeedItem>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    errorMessage: String?,
    toastMessage: String,
    dislikedReasons: Map<String, String>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onOpenVideo: (VideoTarget) -> Unit,
    onOpenSpace: (SpaceRoute) -> Unit,
    onOpenLive: (LiveRoute) -> Unit,
    onDislike: (FeedItem, ThreePointReason) -> Unit,
    onCancelDislike: (FeedItem) -> Unit,
    onToastShown: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(toastMessage, context) {
        if (toastMessage.isNotEmpty()) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            onToastShown()
        }
    }
    AdaptiveMediaGrid(
        items = items,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        modifier = Modifier.fillMaxSize(),
        errorMessage = errorMessage,
        scrollToTopOnRefresh = true,
        key = { _, item -> item.actionKey() },
        contentType = { _, item -> item.cardType },
        loadingContent = {
            VideoGridCardSkeleton()
        }
    ) { item ->
        FeedCard(
            item = item,
            onOpenSpace = onOpenSpace,
            dislikedReason = dislikedReasons[item.actionKey()],
            onDislike = onDislike,
            onCancelDislike = onCancelDislike,
            onClick = {
                item.liveRoute?.let(onOpenLive)
                    ?: item.target?.let(onOpenVideo)
            }
        )
    }
}

@Composable
private fun FeedCard(
    item: FeedItem,
    onOpenSpace: (SpaceRoute) -> Unit,
    dislikedReason: String?,
    onDislike: (FeedItem, ThreePointReason) -> Unit,
    onCancelDislike: (FeedItem) -> Unit,
    onClick: () -> Unit
) {
    val isDisliked = dislikedReason != null
    val canOpen = !isDisliked && (item.target != null || item.liveRoute != null)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                CoverImage(
                    url = item.cover,
                    contentDescription = item.title,
                    shape = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                ) {
                    val hasLeftText = item.coverLeftText1 != null
                    if (hasLeftText) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp)
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item.coverLeftText1?.let {
                                Text(it, color = Color.White, style = MiuixTheme.textStyles.footnote1)
                            }
                        }
                    }

                    item.coverRightText?.let { text ->
                        Text(
                            text = text,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                            color = Color.White,
                            style = MiuixTheme.textStyles.footnote1
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    val spaceRoute = remember(item.args, item.target) {
                        item.args?.let { args ->
                            if (args.upId <= 0L && args.upName.isNullOrBlank()) {
                                null
                            } else {
                                SpaceRoute(
                                    mid = args.upId,
                                    name = args.upName,
                                    fromViewAid = args.aid.takeIf { it > 0L }
                                        ?: (item.target as? VideoTarget.Ugc)?.aid?.takeIf { it > 0L }
                                )
                            }
                        }
                    }
                    Text(
                        text = item.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MiuixTheme.textStyles.body2
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val upName = item.descButton?.text ?: item.args?.upName ?: ""
                        if (upName.isNotEmpty()) {
                            Text(
                                text = upName,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .then(
                                        if (spaceRoute == null || isDisliked) {
                                            Modifier
                                        } else {
                                            Modifier.clickable { onOpenSpace(spaceRoute) }
                                        }
                                    )
                            )
                        }

                        val threePoint = item.threePointV2
                        if (!isDisliked && !threePoint.isNullOrEmpty()) {
                            MoreMenu(
                                item = item,
                                items = threePoint,
                                onDislike = onDislike
                            )
                        }
                    }

                    item.rcmdReason?.let { reason ->
                        if (reason.text.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            val rcmdBgColor = MiuixTheme.colorScheme.secondaryContainer
                            val rcmdBgShape = RoundedCornerShape(4.dp)
                            Text(
                                text = reason.text,
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .background(rcmdBgColor, rcmdBgShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (isDisliked) {
                DislikedOverlay(
                    reason = dislikedReason,
                    onUndo = { onCancelDislike(item) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MoreMenu(
    item: FeedItem,
    items: List<ThreePointItem>,
    onDislike: (FeedItem, ThreePointReason) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(24.dp)
            .clickable { show = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(MiuixIcons.More, contentDescription = null)
    }
    if (show) {
        OverlayDialog(
            show = show,
            onDismissRequest = { show = false }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, menuItem ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    TextButton(
                        text = menuItem.title,
                        onClick = { show = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                    val options = menuItem.reasons.orEmpty() + menuItem.feedbacks.orEmpty()
                    if (options.isNotEmpty()) {
                        options.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                pair.forEach { reason ->
                                    TextButton(
                                        text = reason.name,
                                        onClick = {
                                            show = false
                                            onDislike(item, reason)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            TextButton(
                text = "取消",
                onClick = { show = false },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DislikedOverlay(
    reason: String,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MiuixTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = reason,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                text = "撤回",
                onClick = onUndo
            )
        }
    }
}

private fun FeedItem.actionKey(): String {
    return "$goto|$param|$idx"
}
