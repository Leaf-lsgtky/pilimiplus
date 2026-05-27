package com.naaammme.bbspace.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.TabRow

@Composable
fun FilledTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (trailing == null) {
            TabRow(
                tabs = tabs,
                selectedTabIndex = selectedIndex,
                onTabSelected = onSelect,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TabRow(
                tabs = tabs,
                selectedTabIndex = selectedIndex,
                onTabSelected = onSelect,
                modifier = Modifier.weight(1f)
            )
            trailing.invoke(this)
        }
    }
}
