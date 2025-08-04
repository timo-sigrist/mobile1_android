package com.example.buildnote

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buildnote.ProjectSelectionScreen
import com.example.buildnote.ProjectDetailsScreen
import com.example.buildnote.MaterialListScreen
import com.example.buildnote.AddMaterialScreen
import com.example.buildnote.CreateMeasurementScreen
import com.example.buildnote.ZeiterfassungScreen
import com.example.buildnote.ZeitNachtragenScreen
import com.example.buildnote.DokumenteScreen
import com.example.buildnote.AppointmentDetailsScreen
import com.example.buildnote.StatusSettings
import com.example.buildnote.StatusRequestScreen
import com.example.buildnote.ChatScreen

private const val ROUTE_ZEIT_NACHTRAGEN = "zeit_nachtragen"
const val ROUTE_DOCUMENTS = "documents"

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val appointmentViewModel: AppointmentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Overview.route,
        modifier = modifier
    ) {
        // Übersicht
        composable(BottomNavItem.Overview.route) {
            OverviewScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Projekte
        composable(BottomNavItem.Projekte.route) {
            ProjectSelectionScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Zeiterfassung
        composable(BottomNavItem.Zeiterfassung.route) {
            ZeiterfassungScreen(
                vm = appointmentViewModel,
                navController = navController,
                modifier = modifier
            )
        }
        // Chat (projektbasiert)
        composable(BottomNavItem.Chat.route) {
            ChatScreen(
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Neue Seite: Zeit nachtragen
        composable(ROUTE_ZEIT_NACHTRAGEN) {
            ZeitNachtragenScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Projektdetails
        composable("projectdetails") {
            ProjectDetailsScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Dokumente
        composable(ROUTE_DOCUMENTS) {
            DokumenteScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Materialliste
        composable("materialliste") {
            MaterialListScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Material hinzufügen
        composable("addmaterial") {
            AddMaterialScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Aufmaß erstellen
        composable("createMeasurement") {
            CreateMeasurementScreen(
                navController = navController,
                vm = appointmentViewModel,
                modifier = modifier
            )
        }
        // Statusverwaltung
        composable("statussettings") {
            StatusSettings(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // Statusanfrage
        composable("statusrequest") {
            StatusRequestScreen(navController = navController)
        }
        // Termindetails
        composable("appointmentdetails") {
            AppointmentDetailsScreen(
                navController = navController,
                vm = appointmentViewModel
            )
        }
    }
}
