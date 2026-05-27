package com.naaammme.bbspace.feature.space.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyListScope
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.designsystem.component.AvatarImage
import com.naaammme.bbspace.core.designsystem.component.BiliAsyncImage
import com.naaammme.bbspace.feature.space.SpaceHeaderUiState
import java.util.Locale

internal fun LazyListScope.spaceHeaderSection(
    state: SpaceHeaderUiState
) {
    state.bannerUrl?.let { banner ->
        item(
            key = "header_banner",
            contentType = "banner"
        ) {
            BannerCard(imageUrl = banner)
        }
    }

    item(
        key = "header_profile",
        contentType = "profile"
    ) {
        ProfileCard(state = state)
    }
}

@Composable
private fun BannerCard(imageUrl: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        BiliAsyncImage(
            url = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 6f),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ProfileCard(state: SpaceHeaderUiState) {
    val profile = state.profile
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(
                    url = profile.face,
                    contentDescription = profile.name,
                    modifier = Modifier.size(72.dp),
                    fallbackContent = {
                        Text(
                            text = profile.name.take(1),
                            style = MiuixTheme.textStyles.title2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = profile.name,
                        style = MiuixTheme.textStyles.title2,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UID ${profile.mid}",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Text(
                        text = "Lv${profile.level}",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            }

            if (profile.sign.isNotBlank()) {
                Text(
                    text = profile.sign,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpaceStatChip("粉丝", formatCount(profile.fansCount))
                SpaceStatChip("关注", formatCount(profile.followingCount))
                if (profile.likeCount > 0L) {
                    SpaceStatChip("获赞", formatCount(profile.likeCount))
                }
                SpaceStatChip("视频", profile.videoCount.toString())
                if (profile.articleCount > 0) {
                    SpaceStatChip("图文", profile.articleCount.toString())
                }
                if (profile.seasonCount > 0) {
                    SpaceStatChip("合集", profile.seasonCount.toString())
                }
                if (profile.seriesCount > 0) {
                    SpaceStatChip("系列", profile.seriesCount.toString())
                }
            }

            if (profile.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profile.tags.forEach { tag ->
                        TagChip(text = tag)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpaceStatChip(
    label: String,
    value: String
) {
    Surface(
        color = MiuixTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            Text(
                text = value,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        color = MiuixTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

private fun formatCount(count: Long): String {
    return when {
        count >= 100_000_000L -> formatDecimal(count / 100_000_000f, "亿")
        count >= 10_000L -> formatDecimal(count / 10_000f, "万")
        else -> count.toString()
    }
}

private fun formatDecimal(
    value: Float,
    suffix: String
): String {
    val text = String.format(Locale.ROOT, "%.1f", value)
        .trimEnd('0')
        .trimEnd('.')
    return "$text$suffix"
}
