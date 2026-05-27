package com.naaammme.bbspace.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.VerticalSplit
import top.yukonga.miuix.kmp.icon.extended.Favorites
import top.yukonga.miuix.kmp.icon.extended.Messages
import top.yukonga.miuix.kmp.icon.extended.Contacts

enum class TopLevelRoute(val label: String, val icon: ImageVector, val route: String) {
    HOME("首页", MiuixIcons.VerticalSplit, "home"),
    DYNAMIC("动态", MiuixIcons.Favorites, "dynamic"),
    MESSAGE("消息", MiuixIcons.Messages, "message"),
    PROFILE("我的", MiuixIcons.Contacts, "profile")
}
