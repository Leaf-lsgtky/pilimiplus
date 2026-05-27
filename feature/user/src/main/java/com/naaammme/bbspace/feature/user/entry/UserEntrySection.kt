package com.naaammme.bbspace.feature.user.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.feature.user.UserDest
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Favorites
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UserEntrySection(
    onNavigateToBbSpace: () -> Unit,
    onNavigate: (UserDest) -> Unit,
    onNavigateToDownload: () -> Unit
) {
    FeatureEntryRow(
        onNavigate = onNavigate,
        onNavigateToDownload = onNavigateToDownload
    )

    Spacer(modifier = Modifier.height(16.dp))

    BbSpaceEntryCard(onClick = onNavigateToBbSpace)
}

@Composable
private fun FeatureEntryRow(
    onNavigate: (UserDest) -> Unit,
    onNavigateToDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureEntry(Icons.Default.Refresh, "离线缓存", onClick = onNavigateToDownload)
            FeatureEntry(Icons.Default.DateRange, "历史记录", onClick = { onNavigate(UserDest.History) })
            FeatureEntry(MiuixIcons.Favorites, "收藏", onClick = { onNavigate(UserDest.Favorite) })
            FeatureEntry(Icons.Default.Star, "稍后再看", onClick = { onNavigate(UserDest.WatchLater) })
        }
    }
}

@Composable
private fun BbSpaceEntryCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.primary
            )
            Text(
                text = "bb空间",
                style = MiuixTheme.textStyles.body1
            )
        }
    }
}

@Composable
private fun FeatureEntry(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = MiuixTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote2,
            textAlign = TextAlign.Center
        )
    }
}
