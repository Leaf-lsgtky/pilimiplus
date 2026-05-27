package com.naaammme.bbspace.feature.im.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.designsystem.component.AvatarImage
import com.naaammme.bbspace.core.model.ImSessionItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun ImSessionCard(
    item: ImSessionItem,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        val summaryText = remember(
            item.summary,
            item.isPinned,
            item.isMuted,
            item.sessionTypeLabel
        ) {
            buildSummary(item)
        }
        val timeText = remember(item.timeMicros) {
            formatImTime(item.timeMicros)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                url = item.avatar,
                contentDescription = item.name,
                modifier = Modifier.size(48.dp),
                fallbackText = item.name.take(1)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.weight(1f),
                        style = MiuixTheme.textStyles.subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = timeText,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }

                Text(
                    text = summaryText,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            item.unreadText?.let { unread ->
                Text(
                    text = unread,
                    modifier = Modifier
                        .background(
                            color = MiuixTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MiuixTheme.textStyles.footnote2,
                    color = MiuixTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1
                )
            }
        }
    }
}

private fun buildSummary(item: ImSessionItem): String {
    val prefix = buildList {
        if (item.isPinned) add("置顶")
        if (item.isMuted) add("免打扰")
        item.sessionTypeLabel?.let(::add)
    }.joinToString(" · ")
    return if (prefix.isBlank()) item.summary else "$prefix · ${item.summary}"
}

private fun formatImTime(micros: Long): String {
    if (micros <= 0L) return ""
    val instant = Instant.ofEpochMilli(micros / 1000L)
    return IM_TIME_FORMAT.format(instant.atZone(ZoneId.systemDefault()))
}

private val IM_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
