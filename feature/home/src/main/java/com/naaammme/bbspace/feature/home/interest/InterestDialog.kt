package com.naaammme.bbspace.feature.home.interest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naaammme.bbspace.core.model.InterestChoose

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestDialog(
    data: InterestChoose,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String) -> Unit
) {
    var selectedGenderId by remember { mutableStateOf<Int?>(null) }
    var selectedAgeId by remember { mutableStateOf<Int?>(null) }
    val selected = remember { mutableStateOf(setOf<String>()) }

    val interestPosIds = remember(data) {
        data.items.joinToString(",") { item ->
            if (item.subItems.isEmpty()) "${item.id}"
            else item.subItems.joinToString(",") { sub -> "${item.id}.${sub.id}" }
        }
    }

    fun buildInterestResult(): String {
        val parts = mutableListOf<String>()
        data.items.forEach { item ->
            if (item.subItems.isEmpty()) {
                if ("${item.id}" in selected.value) parts.add("${item.id}")
            } else {
                item.subItems.forEach { sub ->
                    if ("${item.id}.${sub.id}" in selected.value) parts.add("${item.id}.${sub.id}")
                }
            }
        }
        selectedGenderId?.let { parts.add("$it") }
        selectedAgeId?.let { parts.add("$it") }
        return parts.joinToString(",")
    }

    val canConfirm = selected.value.isNotEmpty() || selectedGenderId != null || selectedAgeId != null

    OverlayBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Text(data.title, style = MiuixTheme.textStyles.headline)
            Text(data.subTitle, style = MiuixTheme.textStyles.body2, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
            Spacer(Modifier.height(16.dp))

            if (data.genders.isNotEmpty()) {
                Text(data.genderTitle, style = MiuixTheme.textStyles.body2)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.genders.forEach { g ->
                        val isSelected = selectedGenderId == g.id
                        Text(
                            text = g.title,
                            modifier = Modifier
                                .background(
                                    if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedGenderId = if (selectedGenderId == g.id) null else g.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (data.ages.isNotEmpty()) {
                Text(data.ageTitle, style = MiuixTheme.textStyles.body2)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    data.ages.forEach { a ->
                        val isSelected = selectedAgeId == a.id
                        Text(
                            text = a.title,
                            modifier = Modifier
                                .background(
                                    if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedAgeId = if (selectedAgeId == a.id) null else a.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Text("你想看什么", style = MiuixTheme.textStyles.body2)
            Spacer(Modifier.height(8.dp))
            data.items.forEach { item ->
                Text(item.name, style = MiuixTheme.textStyles.body2, modifier = Modifier.padding(vertical = 4.dp))
                if (item.subItems.isEmpty()) {
                    val key = "${item.id}"
                    val isSelected = key in selected.value
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .background(
                                if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selected.value = if (key in selected.value) selected.value - key else selected.value + key
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = if (isSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.subItems.forEach { sub ->
                            val key = "${item.id}.${sub.id}"
                            val isSelected = key in selected.value
                            Text(
                                text = sub.name,
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selected.value = if (key in selected.value) selected.value - key else selected.value + key
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isSelected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onConfirm(data.uniqueId, buildInterestResult(), interestPosIds) },
                enabled = canConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(data.confirmText.ifEmpty { "确认" })
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
