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
import com.example.buildnote.StatusSettings
import com.example.buildnote.StatusRequestScreen
import com.example.buildnote.AppointmentDetailsScreen

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
        // Zeiterfassung (ohne navController-Parameter)
        composable(BottomNavItem.Zeiterfassung.route) {
            ZeiterfassungScreen(
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
