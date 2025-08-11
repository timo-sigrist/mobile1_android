package com.buildnote.android.views

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Hilfsfunktion zur Berechnung der Arbeitstage (nur Montag bis Freitag) zwischen zwei Daten
fun calculateWorkingDays(fromDate: Date, toDate: Date): Int {
    val cal = Calendar.getInstance()
    cal.time = fromDate
    var workDays = 0
    while (!cal.time.after(toDate)) {
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // Montag (2) bis Freitag (6)
        if (dayOfWeek in 2..6) {
            workDays++
        }
        cal.add(Calendar.DATE, 1)
    }
    return workDays
}

@Composable
fun StatusRequestScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Kontext für DatePickerDialog
    val context = LocalContext.current

    // Standard-Datumformat
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // Zustände für die Auswahl des Statusanfrage-Typs
    var selectedRequestType by remember { mutableStateOf("Urlaub") } // "Urlaub", "Krankmelden", "Sonstiges"

    // Datumszustände für "Von" und "Bis"
    var fromDate by remember { mutableStateOf(Date()) }
    var toDate by remember { mutableStateOf(Date()) }

    // Berechnete Arbeitstage (nur Montag bis Freitag)
    var computedDays by remember { mutableStateOf(calculateWorkingDays(fromDate, toDate)) }

    // Platzhalter für verfügbare Urlaubstage (nur relevant für Urlaub)
    val availableVacationDays = "20 Tage"

    // Textinput-Zustände
    var additionalInfo by remember { mutableStateOf("") }
    var statusLabel by remember { mutableStateOf("") }

    // Zustände zur Steuerung der DatePickerDialoge
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    // Öffne DatePickerDialog für "Von"
    if (showFromPicker) {
        val calendar = Calendar.getInstance()
        calendar.time = fromDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                fromDate = cal.time
                computedDays = calculateWorkingDays(fromDate, toDate)
                showFromPicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    // Öffne DatePickerDialog für "Bis"
    if (showToPicker) {
        val calendar = Calendar.getInstance()
        calendar.time = toDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                toDate = cal.time
                computedDays = calculateWorkingDays(fromDate, toDate)
                showToPicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Gesamter Seiteninhalt – scrollbar und zentriert
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Seitenüberschrift
        Text(
            text = "Statusanfrage erstellen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Auswahl des Statusanfrage-Typs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option "Urlaub"
            Box(
                modifier = Modifier
                    .clickable { selectedRequestType = "Urlaub" }
                    .background(
                        color = if (selectedRequestType == "Urlaub") Color(0xFFFFE0B2) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.BeachAccess,
                        contentDescription = "Urlaub",
                        tint = if (selectedRequestType == "Urlaub") Color(0xFFFFA500) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Urlaub",
                        fontSize = 14.sp,
                        color = if (selectedRequestType == "Urlaub") Color(0xFFFFA500) else Color.Gray
                    )
                }
            }
            // Option "Krankmelden"
            Box(
                modifier = Modifier
                    .clickable { selectedRequestType = "Krankmelden" }
                    .background(
                        color = if (selectedRequestType == "Krankmelden") Color(0xFFFFE0B2) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Bedtime,
                        contentDescription = "Krankmelden",
                        tint = if (selectedRequestType == "Krankmelden") Color(0xFFFFA500) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Krankmelden",
                        fontSize = 14.sp,
                        color = if (selectedRequestType == "Krankmelden") Color(0xFFFFA500) else Color.Gray
                    )
                }
            }
            // Option "Sonstiges"
            Box(
                modifier = Modifier
                    .clickable { selectedRequestType = "Sonstiges" }
                    .background(
                        color = if (selectedRequestType == "Sonstiges") Color(0xFFFFE0B2) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Check, // Platzhalter-Icon für Sonstiges
                        contentDescription = "Sonstiges",
                        tint = if (selectedRequestType == "Sonstiges") Color(0xFFFFA500) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Sonstiges",
                        fontSize = 14.sp,
                        color = if (selectedRequestType == "Sonstiges") Color(0xFFFFA500) else Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Datumsfelder: "Von" und "Bis" – zentriert
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Feld "Von"
            Box(
                modifier = Modifier
                    .clickable { showFromPicker = true }
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Von Datum wählen",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Von: ${sdf.format(fromDate)}", fontSize = 14.sp)
                }
            }
            // Feld "Bis"
            Box(
                modifier = Modifier
                    .clickable { showToPicker = true }
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Bis Datum wählen",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Bis: ${sdf.format(toDate)}", fontSize = 14.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Anzeige der berechneten Arbeitstage (Montag bis Freitag) in Echtzeit.
        Text(
            text = when(selectedRequestType) {
                "Urlaub" -> "Ausgewählte Urlaubstage: $computedDays"
                "Krankmelden" -> "Anzahl Krankheitstage: $computedDays"
                "Sonstiges" -> "Ausgewählte Arbeitstage: $computedDays"
                else -> ""
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Zusätzliche Anzeige für "Urlaub": Verfügbare Urlaubstage
        if (selectedRequestType == "Urlaub") {
            Text(
                text = "Verfügbare Urlaubstage: $availableVacationDays",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bei "Sonstiges" wird ein Eingabefeld zur Statusbezeichnung gezeigt.
        if (selectedRequestType == "Sonstiges") {
            OutlinedTextField(
                value = statusLabel,
                onValueChange = { statusLabel = it },
                label = { Text("Statusbezeichnung") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Textfeld für sonstige Informationen an Arbeitgeber – größeres Eingabefeld.
        OutlinedTextField(
            value = additionalInfo,
            onValueChange = { additionalInfo = it },
            label = { Text("Sonstige Informationen an Arbeitgeber") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // (Optional: Für den Krankmelden-Fall: Anhänge-Icon (Hefter) unter dem Textfeld, falls Krankmelden ausgewählt)
        if (selectedRequestType == "Krankmelden") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "Datei anhängen",
                    tint = Color(0xFF757575),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(28.dp)
                        .clickable {
                            // Hier Dateiauswahl implementieren
                        }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Sende-Button: Unten rechts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clickable {
                        // Hier Implementierung für das Senden der Statusanfrage (später)
                    }
                    .background(
                        color = Color(0xFFFFA500),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Statusanfrage übermitteln",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))


    }
}
