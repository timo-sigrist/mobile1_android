package com.buildnote.android.views.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.AppRoute
import com.buildnote.android.model.ProjectSortMode
import com.buildnote.android.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSelectionScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: ProjectViewModel
) {
    // Sortierung als Dropdown
    var sortExpanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Neueste zuerst", "Älteste zuerst", "Alphabetisch")
    val currentSort = when (vm.sortMode) {
        ProjectSortMode.NEWEST_FIRST -> "Neueste zuerst"
        ProjectSortMode.OLDEST_FIRST -> "Älteste zuerst"
        ProjectSortMode.ALPHABETICAL -> "Alphabetisch"
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.searchQuery,
            onValueChange = { vm.updateSearchQuery(it) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Suche")
            },
            placeholder = { Text("Projektnamen suchen") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = sortExpanded,
            onExpandedChange = { sortExpanded = it }
        ) {
            OutlinedTextField(
                value = currentSort,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sortierung") },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        Modifier.clickable { sortExpanded = !sortExpanded }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()  // ← sorgt dafür, dass das Menü am TextField ansetzt
            )
            ExposedDropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false },
                modifier = Modifier.menuAnchor()  // ← ankern
            ) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            sortExpanded = false
                            when (option) {
                                "Neueste zuerst" -> vm.updateSortMode(ProjectSortMode.NEWEST_FIRST)
                                "Älteste zuerst" -> vm.updateSortMode(ProjectSortMode.OLDEST_FIRST)
                                "Alphabetisch"   -> vm.updateSortMode(ProjectSortMode.ALPHABETICAL)
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Projektliste
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.getFilteredSortedProjects()) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.selectProject(project)
                            navController.navigate(AppRoute.PROJECT_DETAILS.route)
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA726))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                .format(Date(project.createdAt)),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

    }
}
