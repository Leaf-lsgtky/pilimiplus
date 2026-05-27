package com.naaammme.bbspace.feature.bbspace.relation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RelationCheckPane(
    modifier: Modifier = Modifier,
    vm: RelationCheckViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "拉黑关系查询",
                    style = MiuixTheme.textStyles.subtitle,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "只查 up 是否拉黑 user，交换两个 UID 可查反向",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariant
                )
                TextField(
                    value = state.upInput,
                    onValueChange = vm::updateUpInput,
                    modifier = Modifier.fillMaxWidth(),
                    label = "up UID",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = state.userInput,
                    onValueChange = vm::updateUserInput,
                    modifier = Modifier.fillMaxWidth(),
                    label = "user UID",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = vm::query,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isLoading
                    ) {
                        Text(if (state.isLoading) "查询中" else "查询")
                    }
                    TextButton(
                        text = "交换",
                        onClick = vm::swapAndQuery,
                        enabled = !state.isLoading && state.upInput.isNotBlank() && state.userInput.isNotBlank()
                    )
                }
            }
        }

        state.result?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = result,
                        style = MiuixTheme.textStyles.subtitle,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.ttl > 0) {
                        Text(
                            text = "缓存剩余 ${state.ttl} 秒",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        state.error?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.error
                )
            }
        }
    }
}
