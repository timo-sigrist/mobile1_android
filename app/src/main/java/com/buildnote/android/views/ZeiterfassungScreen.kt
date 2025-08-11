package com.buildnote.android.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.model.ActionType
import com.buildnote.android.viewmodel.AppointmentViewModel
import com.buildnote.android.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ROUTE_ZEIT_NACHTRAGEN = "zeit_nachtragen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeiterfassungScreen(
    navController: NavHostController,
    vm: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val projects = vm.getFilteredSortedProjects()
    var projExpanded by remember { mutableStateOf(false) }
    var projSearch by remember { mutableStateOf("") }
    val filteredProjects = projects.filter {
        it.projectName.contains(projSearch, ignoreCase = true)
    }

    // Einträge für den gewählten Tag
    val entries = vm.getEntriesForSelectedDay().filter { it.end != null }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1. Projekt-Auswahl
        item {
            ExposedDropdownMenuBox(
                expanded = projExpanded,
                onExpandedChange = { projExpanded = it }
            ) {
                OutlinedTextField(
                    value = vm.timeTrackingProject?.projectName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Projekt wählen") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(projExpanded) },
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
                    filteredProjects.forEach { proj ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(proj.projectName, Modifier.weight(1f))
                                    Text(
                                        dateFmt.format(Date(proj.createdAt)),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            onClick = {
                                vm.selectTimeProject(proj)
                                projExpanded = false
                                projSearch = ""
                            }
                        )
                    }
                }
            }
        }

        // 2. Aktionsauswahl
        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionType.values().forEach { action ->
                        val isSelected = vm.timeTrackingAction == action
                        ElevatedButton(
                            onClick = { vm.selectTimeAction(action) },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (isSelected) Color(0xFFFFA500) else Color.White,
                                contentColor = if (isSelected) Color.White else Color(0xFFFFA500)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(72.dp),
                            elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
                        ) {
                            Icon(
                                when (action) {
                                    ActionType.ARBEIT -> Icons.Default.Build
                                    ActionType.FAHRT -> Icons.Default.DirectionsCar
                                    ActionType.PAUSE -> Icons.Default.Restaurant
                                },
                                contentDescription = action.name,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Arbeit", "Fahrt", "Pause").forEach { lbl ->
                        Box(
                            Modifier.size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                lbl,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 3. Moderner Start/Stop-Button mit Deaktivierung ohne Projekt
        item {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val running = vm.timerRunning
                val hasProject = vm.timeTrackingProject != null
                ElevatedButton(
                    onClick = { vm.toggleTimer() },
                    enabled = hasProject,
                    shape = CircleShape,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (running)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (running)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    ),
                    modifier = Modifier.size(100.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            if (running) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (running) "Stopp" else "Start",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (running) "Stopp" else "Start",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4. Datumskopf mit Navigation
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.prevDay() }) {
                    Icon(
                        Icons.Default.ArrowBackIos,
                        contentDescription = "Zurück",
                        tint = Color(0xFFFFA500)
                    )
                }
                Spacer(Modifier.weight(1f))
                val headerText = if (vm.selectedDayStart == vm.todayStart) {
                    "Erfasste Zeiten: Heute"
                } else {
                    "Erfasste Zeiten: ${dateFmt.format(Date(vm.selectedDayStart))}"
                }
                Text(
                    headerText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { vm.nextDay() },
                    enabled = vm.selectedDayStart < vm.todayStart
                ) {
                    Icon(
                        Icons.Default.ArrowForwardIos,
                        contentDescription = "Vor",
                        tint = if (vm.selectedDayStart < vm.todayStart) Color(0xFFFFA500) else Color.Gray
                    )
                }
            }
            Divider()
        }

        // 5. Einträge-Liste für gewählten Tag mit Lösch-Button
        if (entries.isEmpty()) {
            item {
                Text(
                    "Noch keine Einträge",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            /*
            items(entries) { e ->
                val from = timeFmt.format(Date(e.start))
                val to = timeFmt.format(Date(e.end))
                val mins = ((e.end - e.start) / 60000).toInt()
                val hrs = mins / 60
                val rem = mins % 60

                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                e.projectName,
                                fontWeight = FontWeight.Bold
                            )
                            Text(e.action.name, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "Von $from – Bis $to" +
                                        if (hrs > 0) " (${hrs}h ${rem}m)" else " (${rem}m)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { vm.deleteTimeEntry(e) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eintrag löschen",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
            */
        }

        // 6. Plus‑Button am Ende
        item {
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = { navController.navigate(ROUTE_ZEIT_NACHTRAGEN) },
                    shape = CircleShape,
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Neue Zeit hinzufügen")
                }
            }
        }
    }
}
