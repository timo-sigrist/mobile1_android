package com.buildnote.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.buildnote.android.ui.theme.BuildNoteAppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

import androidx.compose.ui.Modifier
import com.buildnote.android.views.LoginScreen
import com.buildnote.android.views.navigationbar.BottomNavigationBar
import com.buildnote.android.views.navigationbar.CustomTopBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildNoteAppTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                } else {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = AppRoute.fromRoute(navBackStackEntry?.destination?.route ?: "")
                    val currentTitle = currentRoute?.title ?: "Buildnote"
                    val currentIcon = currentRoute?.icon ?: Icons.Filled.Home

                    Scaffold(
                        contentWindowInsets = WindowInsets(0),

                        topBar = {
                            CustomTopBar(
                                title = currentTitle,
                                pageIcon = currentIcon,
                                onProfileClick = { navController.navigate("settings") },
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) { innerPadding ->
                        // Schlüssel: padding(innerPadding) + consumeWindowInsets + fillMaxSize
                        Box(
                            Modifier
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                                .fillMaxSize()
                        ) {
                            AppNavigation(
                                navController = navController,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
