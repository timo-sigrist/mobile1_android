package com.buildnote.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildnote.android.viewmodel.AppointmentViewModel
import com.buildnote.android.viewmodel.ChatViewModel
import com.buildnote.android.viewmodel.MaterialViewModel
import com.buildnote.android.viewmodel.ProjectViewModel
import com.buildnote.android.viewmodel.SharedViewModel
import com.buildnote.android.views.project.AddMaterialScreen
import com.buildnote.android.views.AppointmentDetailsScreen
import com.buildnote.android.views.CreateMeasurementScreen
import com.buildnote.android.views.DokumenteScreen
import com.buildnote.android.views.project.MaterialListScreen
import com.buildnote.android.views.OverviewScreen
import com.buildnote.android.views.StatusRequestScreen
import com.buildnote.android.views.StatusSettings
import com.buildnote.android.views.ZeitNachtragenScreen
import com.buildnote.android.views.ZeiterfassungScreen
import com.buildnote.android.views.chat.ChatScreen
import com.buildnote.android.views.navigationbar.BottomNavItem
import com.buildnote.android.views.project.ProjectDetailsScreen
import com.buildnote.android.views.project.ProjectSelectionScreen

private const val ROUTE_ZEIT_NACHTRAGEN = "zeit_nachtragen"
const val ROUTE_DOCUMENTS = "documents"

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val sharedVersion: SharedViewModel = viewModel()
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val materialViewModel: MaterialViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

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
                modifier = modifier,
                vm = projectViewModel
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
                vm = chatViewModel,
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
                modifier = modifier,
                vm = projectViewModel
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
