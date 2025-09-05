package com.buildnote.android.views.navigationbar

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
import com.buildnote.android.AppRoute
import com.buildnote.android.ui.theme.Orange
import com.buildnote.android.views.OverviewScreen

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val items = listOf(
        AppRoute.OVERVIEW,
        AppRoute.PROJECTS,
        AppRoute.ZEITERFASSUNG,
        AppRoute.CHAT
    )

    NavigationBar(
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
                    selectedIconColor   = Orange,
                    selectedTextColor   = Orange,
                    unselectedIconColor = Color(0xFF333333),
                    unselectedTextColor = Color(0xFF333333),
                    indicatorColor      = Orange.copy(alpha = 0.12f)
                )
            )
        }
    }
}
