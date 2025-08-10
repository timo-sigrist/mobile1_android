package com.example.buildnote.views

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import com.example.buildnote.utils.StatusUpdate
import com.example.buildnote.utils.getTodaysUpdate

@Composable
fun StatusSettings(
    navController: NavController,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    // Formatierer für Datum im gewünschten Format
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val todayString = sdf.format(Date())

    // Teststatusupdates – Testdaten.
    // In diesem Beispiel enthält ein Statusupdate heute, sodass der aktuelle Status "Urlaub" sein soll.
    val testStatusUpdates = listOf(
        // Dieses Update deckt heute ab:
        StatusUpdate("Urlaub", todayString, todayString),
        StatusUpdate("Krank", "10.04.2025", "12.04.2025"),
        StatusUpdate("Teammeeting", "08.05.2025", "08.05.2025")
    )

    // Ermittlung des heutigen Statusupdates mithilfe der ausgelagerten Funktion.
    val todaysUpdate = getTodaysUpdate(testStatusUpdates)
    // Setze den aktuellen Status basierend auf den Testdaten. Falls kein Update für heute vorliegt, wird "Verfügbar" verwendet.
    val currentStatus = todaysUpdate?.type ?: "Verfügbar"

    // Auswahl des Symbols basierend auf dem aktuellen Status:
    val statusIcon = when (currentStatus.lowercase(Locale.getDefault())) {
        "krank" -> Icons.Filled.Bedtime       // Bei "Krank" Bett-Symbol
        "urlaub" -> Icons.Filled.BeachAccess   // Bei "Urlaub" Strand-Symbol
        "verfügbar" -> Icons.Filled.Check      // Bei "Verfügbar" grüner Haken
        else -> Icons.Filled.Check             // Standard: Haken
    }
    // Farbe: Wenn "Verfügbar", dann grün, sonst grau.
    val iconTint = if (currentStatus.lowercase(Locale.getDefault()) == "verfügbar")
        Color(0xFF4CAF50) else Color.Gray

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Block: Aktueller Status ---
        Text(
            text = "Aktueller Status",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Aktueller Status",
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentStatus,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // --- Block: Geplante Statusmeldungen ---
        Text(
            text = "Geplante Statusmeldungen",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.95f)
                .height(240.dp)
                .background(
                    color = Color(0xFFEFEFEF),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                testStatusUpdates.forEach { update ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                color = Color(0xFFFFE0B2),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { /* Klick-Verhalten später definieren */ }
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Art: ${update.type}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = "Von ${update.from} bis ${update.to}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Plus-Symbol: linksbündig unter dem Statusmeldungsblock zum Hinzufügen eines neuen Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Neuen Status hinzufügen",
                tint = Color(0xFFFFA500),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(28.dp)
                    .clickable {
                        // Beispiel: Navigiere zu einer Seite zum Erstellen einer neuen Statusanfrage
                        navController.navigate("statusrequest")
                    }
            )

        }
    }
}
