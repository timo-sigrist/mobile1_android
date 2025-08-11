package com.buildnote.android.views

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
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.viewmodel.AppointmentViewModel

private val Orange = Color(0xFFFFA500)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeasurementScreen(
    navController: NavHostController,
    vm: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    val lengthUnits = listOf("m", "cm", "mm", "km")
    val areaUnits   = listOf("m²", "cm²", "km²")
    val roomUnits   = listOf("m³", "cm³", "km³")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1) Aufmaßbezeichnung
        OutlinedTextField(
            value = vm.aufmassBezeichnung,
            onValueChange = { vm.aufmassBezeichnung = it },
            label = { Text("Aufmaßbezeichnung") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Orange,   // statt focusedIndicatorColor
                focusedLabelColor = Orange,
                cursorColor = Orange
            )
        )
        Spacer(Modifier.height(12.dp))

        // 2) Notizen
        OutlinedTextField(
            value = vm.notizen,
            onValueChange = { vm.notizen = it },
            label = { Text("Notizen") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Orange,
                focusedLabelColor = Orange,
                cursorColor = Orange
            )
        )
        Spacer(Modifier.height(16.dp))

        // 3) Art des Aufmaßes
        var typeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it }
        ) {
            OutlinedTextField(
                value = vm.artAufmass,
                onValueChange = { },
                readOnly = true,
                label = { Text("Art des Aufmaßes") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Orange,
                    focusedLabelColor = Orange,
                    cursorColor = Orange
                )
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
                modifier = Modifier.menuAnchor()
            ) {
                listOf("Länge", "Fläche", "Raum").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            typeExpanded = false
                            vm.artAufmass = option
                            vm.lengthEntries.clear()
                            vm.areaEntries.clear()
                            vm.roomEntries.clear()
                            when (option) {
                                "Länge"  -> vm.addLengthEntry(LengthEntry("", null, false, null))
                                "Fläche" -> vm.addAreaEntry(
                                    AreaEntry(
                                        "",
                                        null,
                                        null,
                                        false,
                                        null,
                                        null
                                    )
                                )
                                "Raum"   -> vm.addRoomEntry(
                                    RoomEntry(
                                        "",
                                        null,
                                        null,
                                        null,
                                        false,
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 4) Dynamische Eingabeblöcke
        when (vm.artAufmass) {
            "Länge" -> {
                vm.lengthEntries.forEachIndexed { idx, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.laengenbezeichnung,
                                onValueChange = { vm.lengthEntries[idx] = entry.copy(laengenbezeichnung = it) },
                                label = { Text("Längenbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.laenge?.toString() ?: "",
                                onValueChange = {
                                    vm.lengthEntries[idx] = entry.copy(laenge = it.toDoubleOrNull())
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            // Einheit wählen
                            var uExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = uExpanded,
                                onExpandedChange = { uExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.lengthUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(uExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Orange,
                                        focusedLabelColor = Orange,
                                        cursorColor = Orange
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = uExpanded,
                                    onDismissRequest = { uExpanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    lengthUnits.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                vm.lengthUnit = unit
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
                                    checked = entry.includeAbzug,
                                    onCheckedChange = {
                                        vm.lengthEntries[idx] = entry.copy(includeAbzug = it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Orange,
                                        checkedTrackColor = Orange
                                    )
                                )
                            }
                            if (entry.includeAbzug) {
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = entry.abzug?.toString() ?: "",
                                    onValueChange = {
                                        vm.lengthEntries[idx] = entry.copy(abzug = it.toDoubleOrNull())
                                    },
                                    label = { Text("Längenabzug") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Orange,
                                        focusedLabelColor = Orange,
                                        cursorColor = Orange
                                    )
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                TextButton(onClick = { vm.addLengthEntry(LengthEntry("", null, false, null)) }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Orange)
                    Spacer(Modifier.width(4.dp))
                    Text("Aufmaßblock hinzufügen", color = Orange)
                }
            }
            "Fläche" -> {
                vm.areaEntries.forEachIndexed { idx, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.flaechenbezeichnung,
                                onValueChange = { vm.areaEntries[idx] = entry.copy(flaechenbezeichnung = it) },
                                label = { Text("Flächenbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.laenge?.toString() ?: "",
                                onValueChange = {
                                    vm.areaEntries[idx] = entry.copy(laenge = it.toDoubleOrNull())
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.breite?.toString() ?: "",
                                onValueChange = {
                                    vm.areaEntries[idx] = entry.copy(breite = it.toDoubleOrNull())
                                },
                                label = { Text("Breite") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            var u2Expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = u2Expanded,
                                onExpandedChange = { u2Expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.areaUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(u2Expanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Orange,
                                        focusedLabelColor = Orange,
                                        cursorColor = Orange
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = u2Expanded,
                                    onDismissRequest = { u2Expanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    areaUnits.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                vm.areaUnit = unit
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
                                    checked = entry.includeAbzug,
                                    onCheckedChange = {
                                        vm.areaEntries[idx] = entry.copy(includeAbzug = it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Orange,
                                        checkedTrackColor = Orange
                                    )
                                )
                            }
                            if (entry.includeAbzug) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = entry.abzugLaenge?.toString() ?: "",
                                        onValueChange = {
                                            vm.areaEntries[idx] = entry.copy(abzugLaenge = it.toDoubleOrNull())
                                        },
                                        label = { Text("Abzug Länge") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedIndicatorColor = Orange,
                                            focusedLabelColor = Orange,
                                            cursorColor = Orange
                                        )
                                    )
                                    OutlinedTextField(
                                        value = entry.abzugBreite?.toString() ?: "",
                                        onValueChange = {
                                            vm.areaEntries[idx] = entry.copy(abzugBreite = it.toDoubleOrNull())
                                        },
                                        label = { Text("Abzug Breite") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedIndicatorColor = Orange,
                                            focusedLabelColor = Orange,
                                            cursorColor = Orange
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                TextButton(onClick = { vm.addAreaEntry(AreaEntry("", null, null, false, null, null)) }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Orange)
                    Spacer(Modifier.width(4.dp))
                    Text("Aufmaßblock hinzufügen", color = Orange)
                }
            }
            "Raum" -> {
                vm.roomEntries.forEachIndexed { idx, entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = entry.raumbezeichnung,
                                onValueChange = {
                                    vm.roomEntries[idx] = entry.copy(raumbezeichnung = it)
                                },
                                label = { Text("Raumbezeichnung") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.laenge?.toString() ?: "",
                                onValueChange = {
                                    vm.roomEntries[idx] = entry.copy(laenge = it.toDoubleOrNull())
                                },
                                label = { Text("Länge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.breite?.toString() ?: "",
                                onValueChange = {
                                    vm.roomEntries[idx] = entry.copy(breite = it.toDoubleOrNull())
                                },
                                label = { Text("Breite") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = entry.hoehe?.toString() ?: "",
                                onValueChange = {
                                    vm.roomEntries[idx] = entry.copy(hoehe = it.toDoubleOrNull())
                                },
                                label = { Text("Höhe") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Orange,
                                    focusedLabelColor = Orange,
                                    cursorColor = Orange
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            var u3Expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = u3Expanded,
                                onExpandedChange = { u3Expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = vm.roomUnit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Einheit") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(u3Expanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Orange,
                                        focusedLabelColor = Orange,
                                        cursorColor = Orange
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = u3Expanded,
                                    onDismissRequest = { u3Expanded = false },
                                    modifier = Modifier.menuAnchor()
                                ) {
                                    roomUnits.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                vm.roomUnit = unit
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
                                    checked = entry.includeAbzug,
                                    onCheckedChange = {
                                        vm.roomEntries[idx] = entry.copy(includeAbzug = it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Orange,
                                        checkedTrackColor = Orange
                                    )
                                )
                            }
                            if (entry.includeAbzug) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = entry.abzugLaenge?.toString() ?: "",
                                        onValueChange = {
                                            vm.roomEntries[idx] = entry.copy(abzugLaenge = it.toDoubleOrNull())
                                        },
                                        label = { Text("Abzug Länge") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedIndicatorColor = Orange,
                                            focusedLabelColor = Orange,
                                            cursorColor = Orange
                                        )
                                    )
                                    OutlinedTextField(
                                        value = entry.abzugBreite?.toString() ?: "",
                                        onValueChange = {
                                            vm.roomEntries[idx] = entry.copy(abzugBreite = it.toDoubleOrNull())
                                        },
                                        label = { Text("Abzug Breite") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedIndicatorColor = Orange,
                                            focusedLabelColor = Orange,
                                            cursorColor = Orange
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                TextButton(onClick = { vm.addRoomEntry(
                    RoomEntry(
                        "",
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null
                    )
                ) }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Orange)
                    Spacer(Modifier.width(4.dp))
                    Text("Aufmaßblock hinzufügen", color = Orange)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 5) Gesamtabmaß berechnen
        val total = when (vm.artAufmass) {
            "Länge"  -> vm.totalLength()
            "Fläche" -> vm.totalArea()
            "Raum"   -> vm.totalRoom()
            else     -> 0.0
        }
        val unit = when (vm.artAufmass) {
            "Länge"  -> vm.lengthUnit
            "Fläche" -> vm.areaUnit
            "Raum"   -> vm.roomUnit
            else     -> ""
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
