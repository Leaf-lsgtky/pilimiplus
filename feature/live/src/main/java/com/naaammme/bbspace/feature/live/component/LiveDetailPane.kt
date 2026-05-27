package com.naaammme.bbspace.feature.live.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.model.LivePlaybackViewState
import com.naaammme.bbspace.core.model.LiveRoomMessage
import com.naaammme.bbspace.core.model.LiveRoomSessionState
import com.naaammme.bbspace.core.model.LiveRoomSessionStatus
import com.naaammme.bbspace.core.model.LiveRoute
import com.naaammme.bbspace.feature.live.toUiMessage
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun LiveDetailPane(
    route: LiveRoute?,
    playbackState: LivePlaybackViewState,
    roomSessionState: StateFlow<LiveRoomSessionState>,
    showHeader: Boolean,
    modifier: Modifier = Modifier,
    horizontalPad: Dp = 16.dp
) {
    val roomSession by roomSessionState.collectAsStateWithLifecycle()
    val timeFmt = remember {
        DateFormat.getTimeInstance(DateFormat.SHORT)
    }
    val listState = rememberLazyListState()
    val messages = roomSession.messages
    var followNew by remember { mutableStateOf(true) }

    LaunchedEffect(messages.lastOrNull()?.localId) {
        if (followNew) {
            delay(AUTO_SCROLL_COALESCE_MS)
            if (!followNew || listState.isScrollInProgress) return@LaunchedEffect
            val total = listState.layoutInfo.totalItemsCount
            if (total > 0) {
                listState.scrollToItem(total - 1)
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { scrolling ->
                if (!scrolling) {
                    val layout = listState.layoutInfo
                    val lastVisibleIdx = layout.visibleItemsInfo.lastOrNull()?.index ?: -1
                    followNew = lastVisibleIdx >= layout.totalItemsCount - 1
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = horizontalPad, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showHeader) {
            item("title") {
                Text(
                    text = route?.title ?: "直播间 ${route?.roomId ?: 0L}",
                    style = MiuixTheme.textStyles.subtitle,
                    color = MiuixTheme.colorScheme.onBackground
                )
            }

            route?.ownerName?.let { ownerName ->
                item("owner") {
                    Text(
                        text = ownerName,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item("meta") {
            LiveMetaSection(
                route = route,
                playbackState = playbackState,
                status = roomSession.status,
                queueId = roomSession.queueId,
                lastError = roomSession.lastError
            )
        }

        if (messages.isEmpty() && roomSession.status == LiveRoomSessionStatus.Running) {
            item("empty_msg") {
                EmptyMessageCard()
            }
        } else if (messages.isNotEmpty()) {
            liveMessageItems(
                messages = messages,
                timeFmt = timeFmt
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.liveMessageItems(
    messages: List<LiveRoomMessage>,
    timeFmt: DateFormat
) {
    items(
        items = messages,
        key = { it.msgId ?: "live_msg_${it.localId}" },
        contentType = { "live_message" }
    ) { msg ->
        LiveMessageCard(
            message = msg,
            timeFmt = timeFmt
        )
    }
}

private const val AUTO_SCROLL_COALESCE_MS = 80L

@Composable
private fun LiveMetaSection(
    route: LiveRoute?,
    playbackState: LivePlaybackViewState,
    status: LiveRoomSessionStatus,
    queueId: String?,
    lastError: String?
) {
    val tags = remember(route?.roomId, route?.onlineText) {
        listOfNotNull(
            route?.roomId?.takeIf { it > 0L }?.let { "房间 $it" },
            route?.onlineText?.takeIf(String::isNotBlank)
        )
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (tags.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEach { text ->
                    MetaTag(text)
                }
            }
        }

        SessionInfoCard(
            status = status,
            queueId = queueId,
            lastError = lastError
        )

        playbackState.error?.let { error ->
            Text(
                text = error.toUiMessage(),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.error
            )
        }

        playbackState.playerError?.let { error ->
            Text(
                text = error,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun EmptyMessageCard() {
    Surface(
        color = MiuixTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "暂时还没有收到弹幕",
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun SessionInfoCard(
    status: LiveRoomSessionStatus,
    queueId: String?,
    lastError: String?
) {
    Surface(
        color = MiuixTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "消息会话",
                style = MiuixTheme.textStyles.body2
            )
            Text(
                text = "状态: ${roomSessionStatusText(status)}",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )
            queueId?.takeIf(String::isNotBlank)?.let { value ->
                Text(
                    text = "队列: $value",
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariant
                )
            }
            lastError?.takeIf(String::isNotBlank)?.let { err ->
                Text(
                    text = err,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun LiveMessageCard(
    message: LiveRoomMessage,
    timeFmt: DateFormat
) {
    val user = message.user
    val headText = remember(
        message.title,
        user?.name,
        message.medal?.name,
        message.medal?.level
    ) {
        listOfNotNull(
            message.title,
            user?.name?.takeIf(String::isNotBlank),
            message.medal?.let { "${it.name} ${it.level}" }
        ).joinToString(" · ").ifBlank { "匿名用户" }
    }
    val timeText = remember(message.sendTimeMs) {
        if (message.sendTimeMs <= 0L) {
            "--:--"
        } else {
            timeFmt.format(Date(message.sendTimeMs))
        }
    }
    Surface(
        color = MiuixTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headText,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeText,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = message.content,
                style = MiuixTheme.textStyles.body2
            )
        }
    }
}

@Composable
private fun MetaTag(text: String) {
    Surface(
        color = MiuixTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun roomSessionStatusText(status: LiveRoomSessionStatus): String {
    return when (status) {
        LiveRoomSessionStatus.Idle -> "未启动"
        LiveRoomSessionStatus.Connecting -> "连接中"
        LiveRoomSessionStatus.Authorizing -> "认证中"
        LiveRoomSessionStatus.Running -> "已连接"
        LiveRoomSessionStatus.Reconnecting -> "重连中"
        LiveRoomSessionStatus.Closed -> "已关闭"
    }
}
