package com.naaammme.bbspace.feature.comment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.designsystem.component.AvatarImage
import com.naaammme.bbspace.core.designsystem.component.PreviewImage
import com.naaammme.bbspace.core.designsystem.component.PreviewImageRow
import com.naaammme.bbspace.core.model.CommentReply
import com.naaammme.bbspace.core.model.CommentUser
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CommentCard(
    reply: CommentReply,
    currentMid: Long,
    isBusy: (Long) -> Boolean,
    onTranslate: (Long) -> Unit,
    onDelete: (CommentReply) -> Unit,
    onReply: (CommentReply) -> Unit,
    onSaveImage: (PreviewImage) -> Unit,
    onOpenReplies: (CommentReply) -> Unit,
    onOpenUser: (CommentUser) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            ReplyBody(
                reply = reply,
                currentMid = currentMid,
                isBusy = isBusy,
                onTranslate = onTranslate,
                onDelete = onDelete,
                onReply = onReply,
                onSaveImage = onSaveImage,
                onOpenUser = onOpenUser,
                modifier = Modifier.padding(16.dp)
            )

            if (reply.replyCount > 0L) {
                HorizontalDivider()
                TextButton(
                    text = reply.replyEntryText
                        ?.takeIf(String::isNotBlank)
                        ?: "查看 ${reply.replyCount.formatCount()} 条回复",
                    onClick = { onOpenReplies(reply) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ThreadReplyCard(
    reply: CommentReply,
    currentMid: Long,
    isBusy: (Long) -> Boolean,
    onTranslate: (Long) -> Unit,
    onDelete: (CommentReply) -> Unit,
    onReply: (CommentReply) -> Unit,
    onSaveImage: (PreviewImage) -> Unit,
    onOpenUser: (CommentUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MiuixTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        ReplyBody(
            reply = reply,
            currentMid = currentMid,
            isBusy = isBusy,
            onTranslate = onTranslate,
            onDelete = onDelete,
            onReply = onReply,
            onSaveImage = onSaveImage,
            onOpenUser = onOpenUser,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReplyBody(
    reply: CommentReply,
    currentMid: Long,
    isBusy: (Long) -> Boolean,
    onTranslate: (Long) -> Unit,
    onDelete: (CommentReply) -> Unit,
    onReply: (CommentReply) -> Unit,
    onSaveImage: (PreviewImage) -> Unit,
    onOpenUser: (CommentUser) -> Unit,
    modifier: Modifier = Modifier
) {
    val previewImages = remember(reply.pictures) {
        reply.pictures.map { picture ->
            PreviewImage(
                url = picture.url,
                width = picture.width,
                height = picture.height
            )
        }
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            onClick = { onOpenUser(reply.user) },
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MiuixTheme.colorScheme.surface
        ) {
            AvatarImage(
                url = reply.user.face,
                contentDescription = reply.user.name,
                modifier = Modifier.fillMaxSize(),
                fallbackContainerColor = MiuixTheme.colorScheme.secondaryContainer,
                fallbackContent = {
                    Text(
                        text = reply.user.name.take(1).ifBlank { "?" },
                        style = MiuixTheme.textStyles.subtitle,
                        color = MiuixTheme.colorScheme.onSecondaryContainer
                    )
                }
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        modifier = Modifier.clickable { onOpenUser(reply.user) },
                        text = reply.user.name,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        reply.user.level?.let { level ->
                            MiniChip("Lv.$level")
                        }
                        reply.user.vipLabel?.takeIf(String::isNotBlank)?.let { vip ->
                            MiniChip(vip)
                        }
                        reply.user.medal?.let { medal ->
                            MiniChip(
                                if (medal.level > 0) {
                                    "${medal.name} ${medal.level}"
                                } else {
                                    medal.name
                                }
                            )
                        }
                    }
                }
                reply.topLabel?.let { label ->
                    MiniChip(label)
                }
            }

            ReplyMessage(reply)

            if (previewImages.isNotEmpty()) {
                PreviewImageRow(
                    images = previewImages,
                    onSaveImage = onSaveImage
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reply.timeText.ifBlank { "刚刚" },
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    reply.locationText.takeIf(String::isNotBlank)?.let { location ->
                        Text(
                            text = location,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                    Text(
                        text = "点赞 ${reply.likeCount.formatCount()}",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    text = "回复",
                    onClick = { onReply(reply) }
                )
                ReplyMenuButton(
                    busy = isBusy(reply.rpid),
                    canDelete = currentMid > 0L && reply.user.mid == currentMid,
                    onTranslate = { onTranslate(reply.rpid) },
                    onDelete = { onDelete(reply) }
                )
            }
        }
    }
}

@Composable
private fun ReplyMessage(reply: CommentReply) {
    val message = reply.message.takeIf(String::isNotBlank)
    val translated = reply.translatedMessage?.takeIf(String::isNotBlank)
    if (message == null && translated == null) return
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        reply.parentName?.takeIf(String::isNotBlank)?.let { name ->
            Text(
                text = "回复 @$name",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
        message?.let {
            CommentRichText(
                text = it,
                emotes = reply.emotes,
                style = MiuixTheme.textStyles.body2
            )
        }
        translated?.let {
            Text(
                text = it,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

@Composable
private fun ReplyMenuButton(
    busy: Boolean,
    canDelete: Boolean,
    onTranslate: () -> Unit,
    onDelete: () -> Unit
) {
    var show by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { show = true },
            enabled = !busy
        ) {
            Icon(MiuixIcons.More, contentDescription = null)
        }
        OverlayListPopup(
            show = show,
            onDismissRequest = { show = false }
        ) {
            ListPopupColumn {
                val optionCount = if (canDelete) 2 else 1
                DropdownImpl(
                    text = "评论翻译",
                    optionSize = optionCount,
                    isSelected = false,
                    index = 0,
                    onSelectedIndexChange = {
                        show = false
                        onTranslate()
                    }
                )
                if (canDelete) {
                    DropdownImpl(
                        text = "删除评论",
                        optionSize = optionCount,
                        isSelected = false,
                        index = 1,
                        onSelectedIndexChange = {
                            show = false
                            confirmDelete = true
                        }
                    )
                }
            }
        }
    }
    if (confirmDelete) {
        OverlayDialog(
            show = true,
            onDismissRequest = { confirmDelete = false },
            title = "删除评论",
            summary = "确认删除这条评论吗？",
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        text = "取消",
                        onClick = { confirmDelete = false }
                    )
                    TextButton(
                        text = "删除",
                        onClick = {
                            confirmDelete = false
                            onDelete()
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun MiniChip(text: String) {
    Surface(
        color = MiuixTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

internal fun Long.formatCount(): String {
    return when {
        this >= 100_000_000L -> formatDecimal(this / 100_000_000f, "亿")
        this >= 10_000L -> formatDecimal(this / 10_000f, "万")
        else -> toString()
    }
}

private fun formatDecimal(
    value: Float,
    suffix: String
): String {
    val text = String.format(Locale.ROOT, "%.1f", value).trimEnd('0').trimEnd('.')
    return "$text$suffix"
}
