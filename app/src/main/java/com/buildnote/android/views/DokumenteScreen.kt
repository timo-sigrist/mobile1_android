package com.buildnote.android.views

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.content.Intent
import com.buildnote.android.viewmodel.AppointmentViewModel


private val Orange = Color(0xFFFFA500)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokumenteScreen(
    navController: NavHostController,
    vm: AppointmentViewModel,
    modifier: Modifier = Modifier
) {
    // Projekt muss in den Details changeset worden sein
    val projectName = vm.selectedProject?.name ?: return

    // alle vorhandenen Dokumente
    val docs by remember { derivedStateOf { vm.getDocumentsForProject(projectName) } }

    // Launcher für den Chooser
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val uris = mutableListOf<Uri>()
            intent?.data?.let { uris += it }
            intent?.clipData?.let { cd ->
                for (i in 0 until cd.itemCount) {
                    cd.getItemAt(i)?.uri?.let { uris += it }
                }
            }
            if (uris.isNotEmpty()) {
                vm.addDocumentsForProject(projectName, uris)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Überschrift
        Text(
            "Projektdokumente",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Grauer Hintergrund-Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
        ) {
            if (docs.isEmpty()) {
                Text(
                    "Noch keine Dokumente hochgeladen",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Je zwei Einträge nebeneinander
                    itemsIndexed(docs.chunked(2)) { _, row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { doc ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(120.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Box(Modifier.fillMaxSize()) {
                                        // Bild-Vorschau, wenn Bilddatei
                                        if (doc.uri.toString().matches(Regex(".*\\.(jpe?g|png|gif)\$", RegexOption.IGNORE_CASE))) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(doc.uri)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = doc.name,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            // Sonst Ordner-Icon + Name
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.FolderOpen,
                                                    contentDescription = doc.name,
                                                    tint = Orange,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    doc.name,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            // Wenn ungerade, Lücke
                            if (row.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Upload-Button rechtsbündig
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    // Chooser starten
                    val pickIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        setType("*/*")
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    val chooser = Intent.createChooser(pickIntent, "Dokument auswählen")
                    launcher.launch(chooser)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Hochladen", tint = Color.White)
                Spacer(Modifier.width(4.dp))
                Text("Dokumente hochladen", color = Color.White)
            }
        }
    }
}
