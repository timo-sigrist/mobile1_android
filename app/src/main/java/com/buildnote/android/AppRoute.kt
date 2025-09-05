package com.buildnote.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Overview
    OVERVIEW("overview", "Übersicht", Icons.Filled.Home),
    APPOINTMENT_DETAILS("appointmentdetails", "Termindetails", Icons.Filled.CalendarMonth),

    // Porject
    PROJECTS("projekte", "Projekte", Icons.Filled.Business),
    PROJECT_DETAILS("projectdetails", "Projektkartei", Icons.Filled.Business),
    MATERIAL_LIST("materialliste", "Materialliste", Icons.Filled.Category),
    ADD_MATERIAL("addmaterial", "Material hinzufügen", Icons.Filled.AddCircleOutline),
    MEASUREMENT_LIST("measurementList", "Aufmaßliste", Icons.Filled.Calculate),
    MEASUREMENT_DETAIL("measurementDetails", "Aufmaß", Icons.Filled.AddCircleOutline),
    DOCUMENTS("documents", "Dokumente", Icons.Filled.FolderOpen),

    // Zeiterfassung
    ZEITERFASSUNG("zeiterfassung", "Zeiterfassung", Icons.Filled.Schedule),
    ZEIT_NACHTRAGEN("zeit_nachtragen", "Zeit nachtragen", Icons.Filled.Schedule),
    STATUS_SETTINGS("statussettings", "Statusverwaltung", Icons.Filled.Home ),
    STATUS_REQUEST("statusrequest", "Statusanfrage", Icons.Filled.Home),

    // Chat
    CHAT("chat", "Chat", Icons.Filled.ChatBubble);

    companion object {
        fun fromRoute(route: String): AppRoute? =
            entries.firstOrNull { it.route == route }
    }
}