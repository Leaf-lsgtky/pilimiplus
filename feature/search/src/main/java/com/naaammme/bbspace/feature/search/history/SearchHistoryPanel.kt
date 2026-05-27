package com.naaammme.bbspace.feature.search.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.model.SearchHistoryOrder
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistoryPanel(
    histories: List<String>,
    order: SearchHistoryOrder,
    onToggleOrder: () -> Unit,
    onSearch: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var displayCap by remember { mutableStateOf(DISPLAY_STEP) }
    val visible = remember(histories, displayCap) {
        histories.take(displayCap)
    }
    val hasMore = histories.size > displayCap

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(
            key = "header",
            contentType = "header"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "搜索历史",
                    style = MiuixTheme.textStyles.subtitle
                )
                TextButton(onClick = onToggleOrder) {
                    Text(
                        text = when (order) {
                            SearchHistoryOrder.TIME -> "最热"
                            SearchHistoryOrder.HOT -> "最新"
                        }
                    )
                }
            }
        }

        item(
            key = "chips",
            contentType = "chips"
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                visible.forEachIndexed { index, item ->
                    SearchHistoryChip(
                        text = item,
                        featured = index == 0,
                        onClick = { onSearch(item) },
                        onLongClick = { onDelete(item) }
                    )
                }

                if (hasMore) {
                    val remaining = histories.size - displayCap
                    Surface(
                        onClick = { displayCap += DISPLAY_STEP },
                        shape = RoundedCornerShape(16.dp),
                        color = MiuixTheme.colorScheme.surfaceContainerLow
                    ) {
                        Text(
                            text = "展开更多 ($remaining)",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchHistoryChip(
    text: String,
    featured: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .widthIn(max = 160.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (featured) {
            MiuixTheme.colorScheme.tertiaryContainer
        } else {
            MiuixTheme.colorScheme.surfaceContainerLow
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            style = MiuixTheme.textStyles.body2,
            color = if (featured) {
                MiuixTheme.colorScheme.onTertiaryContainer
            } else {
                MiuixTheme.colorScheme.onSurface
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private const val DISPLAY_STEP = 100
