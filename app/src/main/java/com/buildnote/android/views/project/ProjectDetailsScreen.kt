// ProjectDetailsScreen.kt
package com.buildnote.android.views.project

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.buildnote.android.AppRoute
import com.buildnote.android.viewmodel.ProjectViewModel
import kotlinx.coroutines.delay

private val Orange = Color(0xFFFFA500)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: ProjectViewModel
) {
    val project = vm.selectedProject
    if (project == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Kein Projekt ausgewählt.")
        }
        return
    }

    val backEntry = navController.currentBackStackEntry!!
    val showSuccessFlow = backEntry.savedStateHandle.getStateFlow("showMessage", false)
    val showSuccess by showSuccessFlow.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Erfolg-Banner
        if (showSuccess) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Aufmaß erfolgreich übermittelt",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LaunchedEffect(showSuccess) {
                if (showSuccess) {
                    delay(3000L)
                    backEntry.savedStateHandle["showMessage"] = false
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        val customer = vm.getCustomerForSelected()
        val context = LocalContext.current

        // Header mit Menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Projektdetails",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                var menuExpanded by remember { mutableStateOf(false) }
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Aktionen")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ) {
                    DropdownMenuItem(
                        text = { Text(AppRoute.MEASUREMENT_LIST.title) },
                        onClick = {
                            menuExpanded = false
                            navController.navigate(AppRoute.MEASUREMENT_LIST.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(AppRoute.MATERIAL_LIST.title) },
                        onClick = {
                            menuExpanded = false
                            navController.navigate(AppRoute.MATERIAL_LIST.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Handwerker chat") },
                        onClick = { navController.navigate(AppRoute.CHAT.route) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Projektinformationen
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Projektinformationen", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                InfoRow("Bezeichnung:", project.name)
                Divider()
                InfoRow("Adresse:", "${project.street}, ${project.cityZip}")
                if (project.additionalInfo.isNotBlank()) {
                    Divider()
                    InfoRow("Zusatz:", project.additionalInfo)
                }
                Divider()
                Text(
                    "Beschreibung:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(
                onClick = { navController.navigate(AppRoute.MATERIAL_LIST.route) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
                modifier = Modifier.size(72.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "Materialliste",
                        tint = Orange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Material\nliste",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(color = Orange)
                    )
                }
            }
            ElevatedButton(
                onClick = { navController.navigate(AppRoute.MEASUREMENT_DETAIL.route) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
                modifier = Modifier.size(72.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = "Aufmaß nehmen",
                        tint = Orange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Aufmaß\nnehmen",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(color = Orange)
                    )
                }
            }
        }
        // Kundeninformationen + „Kunde kontaktieren“-Button
        customer?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Kundeninformationen", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Name:", it.name)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    InfoRow("E-Mail:", it.email)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    InfoRow("Telefon:", it.phone)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        setData(Uri.parse("tel:${it.phone}"))
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Telefon", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Kunde kontaktieren", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            }

            // Projektdokumente
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Projektdokumente", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    // TODO: Falls bereits Dokumente vorliegen, hier eine Vorschau einfügen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate(AppRoute.DOCUMENTS.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("Zu Dokumenten", color = Color.White)
                    }

                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
