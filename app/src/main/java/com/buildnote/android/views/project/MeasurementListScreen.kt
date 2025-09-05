package com.buildnote.android.views.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.AppRoute
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.AreaUnit
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.LengthUnit
import com.buildnote.android.model.MeasurementRecord
import com.buildnote.android.model.MeasurementType
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.model.RoomUnit
import com.buildnote.android.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: ProjectViewModel
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 16.dp)) {
        var menuExpanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Aufmaßeinträge",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .wrapContentSize()
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Aktionen")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("TODO Aktion") },
                        onClick = {
                            // TODO: AKtion
                        }
                    )
                }
            }
        }

        // --- Scrollbarer Block mit Hintergrund ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Tabellenkopf
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bezeichnung",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Stückzahl",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Typ",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Divider()
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(vm.measurements) { entry ->
                        // Material Entry
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            onClick = {
                                val m = entry
                                vm.selectedMeasurement = m.copy(
                                    lengthEntries = m.lengthEntries.toMutableStateList(),
                                    areaEntries   = m.areaEntries.toMutableStateList(),
                                    roomEntries   = m.roomEntries.toMutableStateList()
                                )
                                navController.navigate(AppRoute.MEASUREMENT_DETAIL.route)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    entry.name,
                                    modifier = Modifier.weight(1.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    entry.total.toString(),
                                    modifier = Modifier.weight(0.75f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    entry.measurementType.displayName,
                                    modifier = Modifier.weight(0.75f),
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Button unten ---
        Button(
            onClick = {
                vm.selectedMeasurement = MeasurementRecord(
                    name = "",
                    description = "",
                    notes = "",
                    total = 0.0,
                    measurementType = MeasurementType.LENGTH,
                    lengthUnit = LengthUnit.M,
                    areaUnit = AreaUnit.M,
                    roomUnit = RoomUnit.M,
                    lengthEntries = mutableStateListOf(),
                    areaEntries   = mutableStateListOf(),
                    roomEntries   = mutableStateListOf()
                )
                navController.navigate(AppRoute.MEASUREMENT_DETAIL.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
            Spacer(Modifier.width(8.dp))
            Text("Aufmaß hinzufügen")
        }
    }
}
