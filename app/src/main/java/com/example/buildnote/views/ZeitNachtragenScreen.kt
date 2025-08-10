package com.example.buildnote.views

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.buildnote.model.ActionType
import com.example.buildnote.viewmodel.AppointmentViewModel
import com.example.buildnote.model.TimeEntry
import com.example.buildnote.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val ROUTE_ZEIT_NACHTRAGEN = "zeit_nachtragen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeitNachtragenScreen(
    navController: NavHostController,
    vm: ProjectViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dayStart = vm.selectedDayStart
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    // Projekt-Dropdown
    var projExpanded by remember { mutableStateOf(false) }
    var projSearch   by remember { mutableStateOf("") }
    var selectedProj by remember { mutableStateOf(vm.timeTrackingProject) }
    val projects = vm.getFilteredSortedProjects().filter {
        it.projectName.contains(projSearch, ignoreCase = true)
    }

    // Zeitfelder
    var startText by remember { mutableStateOf("08:00") }
    var endText   by remember { mutableStateOf("09:00") }

    // Aktion
    var selectedAction by remember { mutableStateOf(ActionType.ARBEIT) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1) Projekt-Auswahl
        item {
            ExposedDropdownMenuBox(
                expanded = projExpanded,
                onExpandedChange = { projExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedProj?.projectName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Projekt wählen") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(projExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = projExpanded,
                    onDismissRequest = { projExpanded = false }
                ) {
                    OutlinedTextField(
                        value = projSearch,
                        onValueChange = { projSearch = it },
                        label = { Text("Suche…") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    Divider()
                    projects.forEach { proj ->
                        DropdownMenuItem(
                            text = { Text(proj.projectName) },
                            onClick = {
                                selectedProj = proj
                                projExpanded = false
                                projSearch = ""
                            }
                        )
                    }
                }
            }
        }

        // 2) Zeitfelder
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = startText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Von") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = {
                            val (h, m) = startText
                                .split(":")
                                .mapNotNull(String::toIntOrNull)
                                .let { if (it.size == 2) it else listOf(8, 0) }
                            TimePickerDialog(
                                context,
                                { _, hh, mm -> startText = "%02d:%02d".format(hh, mm) },
                                h, m, true
                            ).show()
                        }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Startzeit wählen")
                        }
                    }
                )
                OutlinedTextField(
                    value = endText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bis") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = {
                            val (h, m) = endText
                                .split(":")
                                .mapNotNull(String::toIntOrNull)
                                .let { if (it.size == 2) it else listOf(9, 0) }
                            TimePickerDialog(
                                context,
                                { _, hh, mm -> endText = "%02d:%02d".format(hh, mm) },
                                h, m, true
                            ).show()
                        }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Endzeit wählen")
                        }
                    }
                )
            }
        }

        // 3) Aktionsauswahl
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionType.values().forEach { action ->
                    val isSel = action == selectedAction
                    ElevatedButton(
                        onClick = { selectedAction = action },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (isSel) Color(0xFFFFA500) else Color.White,
                            contentColor   = if (isSel) Color.White      else Color(0xFFFFA500)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            when (action) {
                                ActionType.ARBEIT -> Icons.Default.Build
                                ActionType.FAHRT  -> Icons.Default.DirectionsCar
                                ActionType.PAUSE  -> Icons.Default.Restaurant
                            },
                            contentDescription = action.name
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(action.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }

        // 4) Speichern-Button
        item {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        val (hV, mV) = startText
                            .split(":")
                            .mapNotNull(String::toIntOrNull)
                            .let { if (it.size == 2) it else listOf(8, 0) }
                        val (hB, mB) = endText
                            .split(":")
                            .mapNotNull(String::toIntOrNull)
                            .let { if (it.size == 2) it else listOf(9, 0) }
                        val startMs = dayStart + ((hV * 3600 + mV * 60) * 1000)
                        val endMs   = dayStart + ((hB * 3600 + mB * 60) * 1000)

                        when {
                            selectedProj == null -> {
                                // hier ggf. eigene Fehlermeldung
                            }
                            vm.overlaps(startMs, endMs) -> {
                                // hier ggf. eigene Fehlermeldung
                            }
                            else -> {
                                vm.addManualEntry(
                                    TimeEntry(
                                        projectName = selectedProj!!.projectName,
                                        action      = selectedAction,
                                        start       = startMs,
                                        end         = endMs
                                    )
                                )
                                navController.popBackStack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA500),
                        contentColor   = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Zeit nachtragen")
                }
            }
        }
    }
}
