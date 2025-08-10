package com.example.buildnote.views.navigationbar

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ChatBubble

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Overview : BottomNavItem("overview", "Übersicht", Icons.Filled.Home)
    object Projekte : BottomNavItem("projekte", "Projekte", Icons.Filled.Business)
    object Zeiterfassung : BottomNavItem("zeiterfassung", "Zeiterfassung", Icons.Filled.Schedule)
    object Chat : BottomNavItem("chat", "Chat", Icons.Filled.ChatBubble)
}

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val items = listOf(
        BottomNavItem.Overview,
        BottomNavItem.Projekte,
        BottomNavItem.Zeiterfassung,
        BottomNavItem.Chat
    )
    // Dein Orange
    val orangeColor = Color(0xFFFFA500)

    NavigationBar(
        // Wichtig: hier das Padding für die Navigationsleiste hinzufügen
        modifier = modifier
            .navigationBarsPadding(),
        containerColor = Color.White,
        contentColor = Color(0xFF333333)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = orangeColor,
                    selectedTextColor   = orangeColor,
                    unselectedIconColor = Color(0xFF333333),
                    unselectedTextColor = Color(0xFF333333),
                    indicatorColor      = orangeColor.copy(alpha = 0.12f)
                )
            )
        }
    }
}
