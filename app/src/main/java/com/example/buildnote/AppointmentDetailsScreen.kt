package com.example.buildnote

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.buildnote.AppointmentViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppointmentDetailsScreen(
    navController: NavController,
    vm: AppointmentViewModel = viewModel()
) {
    val appt = vm.selectedAppointment
    val context = LocalContext.current

    if (appt == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Kein Termin ausgewählt.", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        return
    }

    val project = vm.getProjectFor(appt)
    var location by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(project) {
        if (project != null) {
            val fullAddress = "${project.street}, ${project.cityZip}"
            withContext(Dispatchers.IO) {
                try {
                    val geo = Geocoder(context).getFromLocationName(fullAddress, 1)
                    geo?.firstOrNull()?.let {
                        location = LatLng(it.latitude, it.longitude)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Termindetails",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bezeichnung:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(appt.projectName, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                Text("Datum:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(appt.date, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text("Von:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(appt.start, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Bis:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(appt.end, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Notizen:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = if (appt.notes.isNotEmpty()) appt.notes else "Keine Notizen",
                        fontSize = 14.sp
                    )
                }
            }
        }

        // --- Google Maps Block ---
        if (project != null) {
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(0.95f),
                shape = RoundedCornerShape(16.dp),
                colors = cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Projektadresse:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("${project.street}, ${project.cityZip}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(8.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        if (location != null) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(
                                    state = com.google.maps.android.compose.MarkerState(position = location!!),
                                    title = project.projectName
                                )

                            }
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Lade Karte…")
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    val uri = Uri.encode("${project.street}, ${project.cityZip}")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$uri"))
                    context.startActivity(intent)
                }) {
                    Text("In Google Maps öffnen")
                }
            }
        }
    }
}
