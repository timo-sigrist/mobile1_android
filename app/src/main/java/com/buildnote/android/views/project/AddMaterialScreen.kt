package com.buildnote.android.views.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.model.Material
import com.buildnote.android.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    navController: NavHostController,
    vm: ProjectViewModel,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var unitExpanded by remember { mutableStateOf(false) }
    val unitOptions = listOf("stk","m","cm","m²","cm²","m³","cm³","liter")
    var selectedUnit by remember { mutableStateOf(unitOptions.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Material Eingabefelder
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Bezeichnung") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
                label = { Text("Stückzahl") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Dropdown für Einheit
            ExposedDropdownMenuBox(
                expanded = unitExpanded,
                onExpandedChange = { unitExpanded = !unitExpanded }
            ) {
                OutlinedTextField(
                    value = selectedUnit,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Einheit") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { unitExpanded = false },
                    modifier = Modifier.menuAnchor()
                ) {
                    unitOptions.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit) },
                            onClick = {
                                selectedUnit = unit
                                unitExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Button unten
        Button(
            onClick = {
                val qty = quantityText.toIntOrNull() ?: 0
                vm.addMaterialEntry(
                    Material(
                        name = name,
                        number = qty,
                        unit = selectedUnit,
                        projectId = vm.selectedProject!!.id
                    )
                )
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
            Spacer(Modifier.width(8.dp))
            Text("Zu Liste hinzufügen")
        }
    }
}
