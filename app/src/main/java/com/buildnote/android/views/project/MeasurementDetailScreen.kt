package com.buildnote.android.views.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.model.AreaUnit
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.LengthUnit
import com.buildnote.android.model.MeasurementType
import com.buildnote.android.model.RoomUnit
import com.buildnote.android.ui.theme.Orange
import com.buildnote.android.ui.theme.buildNoteSwitchColors
import com.buildnote.android.ui.theme.buildNoteTextFieldColors
import com.buildnote.android.viewmodel.ProjectViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeasurementScreen(
    navController: NavHostController,
    vm: ProjectViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1) Aufmaßbezeichnung
        OutlinedTextField(
            value = vm.selectedMeasurement!!.name,
            onValueChange = { vm.selectedMeasurement = vm.selectedMeasurement?.copy(name = it) },
            label = { Text("Aufmaßbezeichnung") },
            modifier = Modifier.fillMaxWidth(),
            colors = buildNoteTextFieldColors()
        )
        Spacer(Modifier.height(12.dp))

        // 2) Notizen
        OutlinedTextField(
            value = vm.selectedMeasurement!!.notes,
            onValueChange = { vm.selectedMeasurement = vm.selectedMeasurement?.copy(notes = it) },
            label = { Text("Notizen") },
            modifier = Modifier.fillMaxWidth(),
            colors = buildNoteTextFieldColors()
        )
        Spacer(Modifier.height(16.dp))

        // 3) Art des Aufmaßes
        var typeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it }
        ) {
            OutlinedTextField(
                value = vm.selectedMeasurement!!.measurementType.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Art des Aufmaßes") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = buildNoteTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
                modifier = Modifier.menuAnchor()
            ) {
                MeasurementType.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            typeExpanded = false
                            vm.selectedMeasurement!!.measurementType = option
                            vm.selectedMeasurement!!.lengthEntries.clear()
                            vm.selectedMeasurement!!.areaEntries.clear()
                            vm.selectedMeasurement!!.roomEntries.clear()
                            when (option) {
                                MeasurementType.LENGTH -> vm.addLengthEntry()
                                MeasurementType.AREA -> vm.addAreaEntry()
                                MeasurementType.ROOM -> vm.addRoomEntry()
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 4) Dynamische Eingabeblöcke
        when (vm.selectedMeasurement!!.measurementType) {
            // Länge Input
            MeasurementType.LENGTH -> {
                vm.selectedMeasurement!!.lengthEntries.forEachIndexed { idx, entry: LengthEntry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.description,
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.lengthEntries[idx] =
                                            rec.lengthEntries[idx].copy(description = new)
                                    }
                                },
                                label = { Text("Längenbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.length?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.lengthEntries[idx] =
                                            rec.lengthEntries[idx].copy(length = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            // Einheit wählen
                            var uExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = uExpanded,
                                onExpandedChange = { uExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.selectedMeasurement!!.lengthUnit.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            uExpanded
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = buildNoteTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = uExpanded,
                                    onDismissRequest = { uExpanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    LengthUnit.entries.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit.displayName) },
                                            onClick = {
                                                vm.selectedMeasurement =
                                                    vm.selectedMeasurement?.copy(lengthUnit = unit)
                                                uExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Abzug hinzufügen", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = entry.includeDeduction,
                                    onCheckedChange = {new ->
                                        vm.selectedMeasurement?.let { rec ->
                                            rec.lengthEntries[idx] =
                                                rec.lengthEntries[idx].copy(includeDeduction = new)
                                        }
                                    },
                                    colors = buildNoteSwitchColors()
                                )
                            }
                            if (entry.includeDeduction || (entry.deductionLength != null && entry.deductionLength > 0.0)) {
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = entry.deductionLength?.toString() ?: "",
                                    onValueChange = { new ->
                                        vm.selectedMeasurement?.let { rec ->
                                            rec.lengthEntries[idx] =
                                                rec.lengthEntries[idx].copy(deductionLength = new.toDoubleOrNull())
                                        }
                                    },
                                    label = { Text("Längenabzug") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = buildNoteTextFieldColors()
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Fläche Input
            MeasurementType.AREA -> {
                vm.selectedMeasurement!!.areaEntries.forEachIndexed { idx, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.description,
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.areaEntries[idx] =
                                            rec.areaEntries[idx].copy(description = new)
                                    }
                                },
                                label = { Text("Flächenbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.length?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.areaEntries[idx] =
                                            rec.areaEntries[idx].copy(length = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.width?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.areaEntries[idx] =
                                            rec.areaEntries[idx].copy(width = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Breite") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            var u2Expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = u2Expanded,
                                onExpandedChange = { u2Expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.selectedMeasurement!!.areaUnit.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            u2Expanded
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = buildNoteTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = u2Expanded,
                                    onDismissRequest = { u2Expanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    AreaUnit.entries.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit.displayName) },
                                            onClick = {
                                                vm.selectedMeasurement =
                                                    vm.selectedMeasurement?.copy(areaUnit = unit)
                                                u2Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Abzug hinzufügen", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = entry.includeDeduction,
                                    onCheckedChange = { new ->
                                        vm.selectedMeasurement?.let { rec ->
                                            rec.areaEntries[idx] =
                                                rec.areaEntries[idx].copy(includeDeduction = new)
                                        }
                                    },
                                    colors = buildNoteSwitchColors()
                                )
                            }
                            if (entry.includeDeduction) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = entry.deductionLength?.toString() ?: "",
                                        onValueChange = { new ->
                                            vm.selectedMeasurement?.let { rec ->
                                                rec.areaEntries[idx] =
                                                    rec.areaEntries[idx].copy(deductionLength = new.toDoubleOrNull())
                                            }
                                        },
                                        label = { Text("Abzug Länge") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = buildNoteTextFieldColors()
                                    )
                                    OutlinedTextField(
                                        value = entry.deductionWidth?.toString() ?: "",
                                        onValueChange = { new ->
                                            vm.selectedMeasurement?.let { rec ->
                                                rec.areaEntries[idx] =
                                                    rec.areaEntries[idx].copy(deductionWidth = new.toDoubleOrNull())
                                            }
                                        },
                                        label = { Text("Abzug Breite") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = buildNoteTextFieldColors()
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Raum
            MeasurementType.ROOM -> {
                vm.selectedMeasurement!!.roomEntries.forEachIndexed { idx, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.description,
                                onValueChange = {new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.roomEntries[idx] =
                                            rec.roomEntries[idx].copy(description = new)
                                    }
                                },
                                label = { Text("Raumbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.length?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.roomEntries[idx] =
                                            rec.roomEntries[idx].copy(length = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.width?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.roomEntries[idx] =
                                            rec.roomEntries[idx].copy(width = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Breite") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.height?.toString() ?: "",
                                onValueChange = { new ->
                                    vm.selectedMeasurement?.let { rec ->
                                        rec.roomEntries[idx] =
                                            rec.roomEntries[idx].copy(height = new.toDoubleOrNull())
                                    }
                                },
                                label = { Text("Höhe") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = buildNoteTextFieldColors()
                            )
                            Spacer(Modifier.height(8.dp))
                            var u3Expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = u3Expanded,
                                onExpandedChange = { u3Expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.selectedMeasurement!!.roomUnit.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            u3Expanded
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = buildNoteTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = u3Expanded,
                                    onDismissRequest = { u3Expanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    RoomUnit.entries.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit.displayName) },
                                            onClick = {
                                                vm.selectedMeasurement =
                                                    vm.selectedMeasurement?.copy(roomUnit = unit)
                                                u3Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Abzug hinzufügen", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = entry.includeDeduction,
                                    onCheckedChange = {new ->
                                        vm.selectedMeasurement?.let { rec ->
                                            rec.roomEntries[idx] =
                                                rec.roomEntries[idx].copy(includeDeduction = new)
                                        }
                                    },
                                    colors = buildNoteSwitchColors()
                                )
                            }
                            if (entry.includeDeduction) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = entry.deductionLength?.toString() ?: "",
                                        onValueChange = { new ->
                                            vm.selectedMeasurement?.let { rec ->
                                                rec.roomEntries[idx] =
                                                    rec.roomEntries[idx].copy(deductionLength = new.toDoubleOrNull())
                                            }
                                        },
                                        label = { Text("Abzug Länge") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = buildNoteTextFieldColors()
                                    )
                                    OutlinedTextField(
                                        value = entry.deductionWidth?.toString() ?: "",
                                        onValueChange = { new ->
                                            vm.selectedMeasurement?.let { rec ->
                                                rec.roomEntries[idx] =
                                                    rec.roomEntries[idx].copy(deductionWidth = new.toDoubleOrNull())
                                            }
                                        },
                                        label = { Text("Abzug Breite") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = buildNoteTextFieldColors()
                                    )
                                    OutlinedTextField(
                                        value = entry.deductionHeight?.toString() ?: "",
                                        onValueChange = { new ->
                                            vm.selectedMeasurement?.let { rec ->
                                                rec.roomEntries[idx] =
                                                    rec.roomEntries[idx].copy(deductionHeight = new.toDoubleOrNull())
                                            }
                                        },
                                        label = { Text("Abzug Höhe") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = buildNoteTextFieldColors()
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

            }
        }

        // Aufmassblock hinzufügen
        TextButton(onClick = {
            when(vm.selectedMeasurement!!.measurementType) {
                MeasurementType.LENGTH -> vm.addLengthEntry()
                MeasurementType.AREA -> vm.addAreaEntry()
                MeasurementType.ROOM -> vm.addRoomEntry()
            }

        }) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Orange)
            Spacer(Modifier.width(4.dp))
            Text("Aufmaßblock hinzufügen", color = Orange)
        }

        Spacer(Modifier.height(24.dp))

        // Gesamtabmaß berechnen
        val total = when (vm.selectedMeasurement!!.measurementType) {
            MeasurementType.LENGTH -> vm.totalLength()
            MeasurementType.AREA -> vm.totalArea()
            MeasurementType.ROOM -> vm.totalRoom()
        }
        val unit = when (vm.selectedMeasurement!!.measurementType) {
            MeasurementType.LENGTH -> vm.selectedMeasurement!!.lengthUnit
            MeasurementType.AREA -> vm.selectedMeasurement!!.areaUnit
            MeasurementType.ROOM -> vm.selectedMeasurement!!.roomUnit
        }
        Text("Gesamtmaß: $total $unit", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        // 6) Übermitteln-Button nur aktiv wenn total != 0
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val enabled = total != 0.0
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("showMessage", true)
                    vm.sendMeasurement()
                    navController.popBackStack()
                },
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    disabledContainerColor = Orange.copy(alpha = 0.3f),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                Spacer(Modifier.width(8.dp))
                Text("Aufmaß übermitteln")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
