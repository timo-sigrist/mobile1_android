package com.buildnote.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildnote.android.viewmodel.AppointmentViewModel
import com.buildnote.android.viewmodel.ChatViewModel
import com.buildnote.android.viewmodel.ProjectViewModel
import com.buildnote.android.viewmodel.SharedViewModel
import com.buildnote.android.views.project.AddMaterialScreen
import com.buildnote.android.views.AppointmentDetailsScreen
import com.buildnote.android.views.project.CreateMeasurementScreen
import com.buildnote.android.views.DokumenteScreen
import com.buildnote.android.views.project.MaterialListScreen
import com.buildnote.android.views.OverviewScreen
import com.buildnote.android.views.StatusRequestScreen
import com.buildnote.android.views.StatusSettings
import com.buildnote.android.views.ZeitNachtragenScreen
import com.buildnote.android.views.ZeiterfassungScreen
import com.buildnote.android.views.chat.ChatScreen
import com.buildnote.android.views.project.MeasurementListScreen
import com.buildnote.android.views.project.ProjectDetailsScreen
import com.buildnote.android.views.project.ProjectSelectionScreen
import com.buildnote.android.AppRoute.*

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val sharedVersion: SharedViewModel = viewModel()
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = OVERVIEW.route,
        modifier = modifier
    ) {
        // Übersicht
        composable(OVERVIEW.route) {
            OverviewScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Projekte
        composable(PROJECTS.route) {
            ProjectSelectionScreen(
                navController = navController,
                modifier = modifier,
                vm = projectViewModel
            )
        }
        // Zeiterfassung
        composable(ZEITERFASSUNG.route) {
            ZeiterfassungScreen(
                vm = appointmentViewModel,
                navController = navController,
                modifier = modifier
            )
        }
        // Chat (projektbasiert)
        composable(CHAT.route) {
            ChatScreen(
                vm = chatViewModel,
                modifier = modifier
            )
        }
        // Neue Seite: Zeit nachtragen
        composable(ZEIT_NACHTRAGEN.route) {
            ZeitNachtragenScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Projektdetails
        composable(PROJECT_DETAILS.route) {
            ProjectDetailsScreen(
                navController = navController,
                modifier = modifier,
                vm = projectViewModel
            )
        }
        // Dokumente
        composable(DOCUMENTS.route) {
            DokumenteScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Materialliste
        composable(MATERIAL_LIST.route) {
            MaterialListScreen(
                navController = navController,
                modifier = modifier,
                vm = projectViewModel
            )
        }
        // Material hinzufügen
        composable(ADD_MATERIAL.route) {
            AddMaterialScreen(
                navController = navController,
                modifier = modifier,
                vm = projectViewModel
            )
        }
        // Aufmaß erstellen
        composable(MEASUREMENT_LIST.route) {
            MeasurementListScreen(
                navController = navController,
                vm = projectViewModel,
                modifier = modifier
            )
        }
        // Aufmaß erstellen
        composable(MEASUREMENT_DETAIL.route) {
            CreateMeasurementScreen(
                navController = navController,
                vm = projectViewModel,
                modifier = modifier
            )
        }
        // Statusverwaltung
        composable(STATUS_SETTINGS.route) {
            StatusSettings(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // Statusanfrage
        composable(STATUS_REQUEST.route) {
            StatusRequestScreen(navController = navController)
        }
        // Termindetails
        composable(APPOINTMENT_DETAILS.route) {
            AppointmentDetailsScreen(
                navController = navController,
                vm = appointmentViewModel
            )
        }
    }
}
