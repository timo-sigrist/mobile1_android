package com.example.buildnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.buildnote.ui.theme.ExampleAppTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExampleAppTheme {
                // Login-Zustand: Setze isLoggedIn immer initial auf false, damit man sich neu einloggen muss.
                var isLoggedIn by remember { mutableStateOf(false) }



                if (!isLoggedIn) {
                    // Zeige die Login-Seite an, falls nicht eingeloggt.
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                } else {
                    // Bei erfolgreichem Login: Zeige die Hauptanwendung.
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val currentTitle = when (currentRoute) {
                        "overview"           -> "Übersicht"
                        "projekte"           -> "Projekte"
                        "zeiterfassung"      -> "Zeiterfassung"
                        "zeit_nachtragen"     -> "Zeit nachtragen"
                        "documents"          -> "Dokumente"         // ← hier eingefügt
                        "chat"               -> "Chat"
                        "statussettings"     -> "Statusverwaltung"
                        "statusrequest"      -> "Statusanfrage"
                        "appointmentdetails" -> "Termindetails"
                        "projectdetails"     -> "Projektkartei"
                        "materialliste"      -> "Materialliste"
                        "addmaterial"        -> "Material hinzufügen"
                        "createMeasurement"  -> "Aufmaß"
                        else                 -> "BuildNote"
                    }










                    val currentIcon = when (currentRoute) {
                        "overview" -> BottomNavItem.Overview.icon
                        "projekte" -> BottomNavItem.Projekte.icon
                        "zeiterfassung" -> BottomNavItem.Zeiterfassung.icon
                        "chat" -> BottomNavItem.Chat.icon
                        else -> Icons.Filled.Home
                    }



                    Scaffold(
                        topBar = {
                            CustomTopBar(
                                title = currentTitle,
                                pageIcon = currentIcon,
                                onProfileClick = { navController.navigate("settings") }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) { innerPadding ->
                        // HIER:
                        // - fillMaxSize() sorgt dafür, dass dein NavHost den gesamten
                        //   verbleibenden Bereich einnimmt.
                        // - padding(innerPadding) schiebt den Content genau unter die TopBar
                        //   und genau oberhalb der BottomBar.
                        AppNavigation(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
