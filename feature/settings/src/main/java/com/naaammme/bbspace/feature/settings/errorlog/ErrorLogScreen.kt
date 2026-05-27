package com.naaammme.bbspace.feature.settings.errorlog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naaammme.bbspace.feature.settings.errorlog.ErrorLogViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.common.log.ErrorLog
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ErrorLogScreen(
    onBack: () -> Unit,
    viewModel: ErrorLogViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fmt = remember { SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()) }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("错误日志 (${logs.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = null)
                    }
                },
                actions = {
                    if (logs.isNotEmpty()) {
                        TextButton(onClick = {
                            val text = logs.joinToString("\n---\n") { it.toReportText(fmt) }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("error_logs", text))
                            Toast.makeText(context, "已复制 ${logs.size} 条日志", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("复制全部")
                        }
                        IconButton(onClick = { viewModel.clear() }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (logs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("暂无错误日志", style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs, key = { it.time.toString() + it.tag }) { log ->
                    ErrorLogItem(log, fmt)
                }
            }
        }
    }
}

@Composable
private fun ErrorLogItem(log: ErrorLog, fmt: SimpleDateFormat) {
    var expanded by remember { mutableStateOf(false) }
    val hasStack = !log.stackTrace.isNullOrEmpty()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (hasStack) Modifier.clickable { expanded = !expanded } else Modifier),
        color = MiuixTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.tag,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.error
                )
                Text(
                    text = fmt.format(Date(log.time)),
                    style = MiuixTheme.textStyles.footnote2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
            Text(
                text = log.message,
                style = MiuixTheme.textStyles.footnote1,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (hasStack) {
                Text(
                    text = if (expanded) log.stackTrace!! else "点击查看堆栈",
                    style = MiuixTheme.textStyles.footnote1,
                    fontFamily = if (expanded) FontFamily.Monospace else FontFamily.Default,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun ErrorLog.toReportText(fmt: SimpleDateFormat): String {
    val sb = StringBuilder()
    sb.append("[${fmt.format(Date(time))}] [$tag] $message")
    if (!stackTrace.isNullOrEmpty()) sb.append("\n$stackTrace")
    return sb.toString()
}
