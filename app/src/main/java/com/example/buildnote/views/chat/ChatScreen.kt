package com.example.buildnote.views.chat

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.buildnote.model.Project
import com.example.buildnote.viewmodel.AppointmentViewModel
import com.example.buildnote.viewmodel.ChatViewModel
import java.io.OutputStream

private val Orange = Color(0xFFFFA500)
private val GreyBubble = Color(0xFFEFEFEF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    // Projektauswahl wie gehabt…
    var projExpanded by remember { mutableStateOf(false) }
    var projSearch by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    val allProjects = vm.getFilteredSortedProjects()
    val filtered = allProjects.filter {
        it.projectName.contains(projSearch, ignoreCase = true)
    }

    Column(modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = projExpanded,
            onExpandedChange = { projExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedProject?.projectName ?: "",
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
                filtered.forEach { proj ->
                    DropdownMenuItem(
                        text = { Text(proj.projectName) },
                        onClick = {
                            selectedProject = proj
                            projExpanded = false
                            projSearch = ""
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val project = selectedProject
        if (project == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bitte Projekt auswählen", color = Color.Gray)
            }
            return
        }

        // Eingangs-States
        val attachments = remember { mutableStateListOf<Uri>() }
        var inputText by remember { mutableStateOf("") }
        var previewUri by remember { mutableStateOf<Uri?>(null) }  // jetzt var!

        // Picker für Anhänge
        val picker = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris: List<Uri> ->
            attachments.addAll(uris)
        }

        // Chat-Verlauf
        val messages = vm.getMessages(project.projectName)

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    Column(
                        horizontalAlignment = if (msg.isMine) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg.senderName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (msg.isMine) Orange else Color.Gray
                        )
                        if (msg.text.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(
                                        color = if (msg.isMine) Orange else GreyBubble,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isMine) Color.White else Color.Black
                                )
                            }
                        }
                        if (msg.attachments.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
                            ) {
                                msg.attachments.forEach { uri ->
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 4.dp)
                                            .clickable { previewUri = uri }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (attachments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    attachments.forEachIndexed { idx, uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 4.dp)
                                .clickable { attachments.removeAt(idx) }
                        )
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { picker.launch(arrayOf("*/*")) }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Anhang")
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Nachricht…") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() || attachments.isNotEmpty()) {
                            vm.sendMessage(
                                projectName = project.projectName,
                                text = inputText,
                                attachments = attachments.toList()
                            )
                            inputText = ""
                            attachments.clear()
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Senden", tint = Orange)
                }
            }
        }
    }

    // Vollbild-Vorschau
    var previewUri by remember { mutableStateOf<Uri?>(null) }

    previewUri?.let { uri ->
        Dialog(onDismissRequest = { previewUri = null }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick = { previewUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Color.White)
                }
                IconButton(
                    onClick = { saveUriToDownloads(context, uri) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Speichern", tint = Color.White)
                }
            }
        }
    }
}

/**
 * Speichert die URI in den Downloads-Ordner.
 */
private fun saveUriToDownloads(context: Context, uri: Uri) {
    val resolver = context.contentResolver
    val mime = resolver.getType(uri) ?: "application/octet-stream"
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "anhang_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, mime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
    }
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Files.getContentUri("external")
    }
    val newUri = resolver.insert(collection, values) ?: return
    resolver.openInputStream(uri)?.use { input ->
        resolver.openOutputStream(newUri)?.use { output: OutputStream ->
            input.copyTo(output)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(newUri, values, null, null)
    }
}
