package com.naaammme.bbspace.feature.bbspace

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.icon.icons.Contacts
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.feature.bbspace.aicucomment.AicuCommentPane
import com.naaammme.bbspace.feature.bbspace.playback.PlaybackHistoryPane
import com.naaammme.bbspace.feature.bbspace.relation.RelationCheckPane

@Composable
fun BbSpaceScreen(
    onBack: () -> Unit,
    vm: BbSpaceViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var page by rememberSaveable { mutableStateOf(BbSpacePage.HOME) }
    val handleBack = {
        if (page == BbSpacePage.HOME) {
            onBack()
        } else {
            page = BbSpacePage.HOME
        }
    }

    BackHandler(enabled = page != BbSpacePage.HOME) {
        page = BbSpacePage.HOME
    }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = {
                    Text(
                        text = when (page) {
                            BbSpacePage.HOME -> "bb空间"
                            BbSpacePage.PLAYBACK_HISTORY -> "播放历史"
                            BbSpacePage.RELATION_CHECK -> "拉黑关系"
                            BbSpacePage.AICU_COMMENT -> "AICU 评论"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        when (page) {
            BbSpacePage.HOME -> {
                BbSpaceHomePane(
                    playbackCount = state.playbackCount,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onOpenPlaybackHistory = { page = BbSpacePage.PLAYBACK_HISTORY },
                    onOpenRelationCheck = { page = BbSpacePage.RELATION_CHECK },
                    onOpenAicuComment = { page = BbSpacePage.AICU_COMMENT }
                )
            }
            BbSpacePage.PLAYBACK_HISTORY -> {
                PlaybackHistoryPane(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                )
            }
            BbSpacePage.RELATION_CHECK -> {
                RelationCheckPane(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                )
            }
            BbSpacePage.AICU_COMMENT -> {
                AicuCommentPane(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun BbSpaceHomePane(
    playbackCount: Int,
    modifier: Modifier = Modifier,
    onOpenPlaybackHistory: () -> Unit,
    onOpenRelationCheck: () -> Unit,
    onOpenAicuComment: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BbSpaceEntryCard(
            title = "播放历史",
            subtitle = "当前有 $playbackCount 条本地记录",
            icon = Icons.Default.DateRange,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenPlaybackHistory
        )
        BbSpaceEntryCard(
            title = "查询关系",
            subtitle = "输入两个 UID 查询 关系",
            icon = MiuixIcons.Contacts,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenRelationCheck
        )
        BbSpaceEntryCard(
            title = "AICU 评论",
            subtitle = "输入 UID 查询 AICU 评论",
            icon = Icons.Default.DateRange,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenAicuComment
        )
    }
}

@Composable
private fun BbSpaceEntryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.subtitle,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "进入",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class BbSpacePage {
    HOME,
    PLAYBACK_HISTORY,
    RELATION_CHECK,
    AICU_COMMENT
}
