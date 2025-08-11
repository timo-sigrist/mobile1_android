package com.buildnote.android.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import com.buildnote.android.utils.StatusUpdate
import com.buildnote.android.utils.getTodaysUpdate
import com.buildnote.android.viewmodel.AppointmentViewModel
import java.util.Calendar

@Composable
fun OverviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: AppointmentViewModel
) {
    // Zustand: Welcher Tag wird angezeigt? (Initial: heute)
    var selectedDate by remember { mutableStateOf(Date()) }

    // Funktion, um einen Tag hinzuzufügen oder abzuziehen.
    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
    }

    // Datum formatieren
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val todayString = sdf.format(Date())
    val selectedDateString = sdf.format(selectedDate)

    // Header‑Text und Farbe
    val headerText = if (selectedDateString == todayString) "Heutige Termine" else selectedDateString
    val headerColor = if (selectedDateString == todayString) Color(0xFFFFA500) else Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 1) Begrüßungsblock
        AnimatedVisibility(
            visible = vm.showGreeting,
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 16.dp,
                    bottomEnd = 16.dp
                ),
                modifier = Modifier
                    .wrapContentWidth(Alignment.Start)
                    .padding(bottom = 16.dp)        // ← nur noch unten 16dp Abstand

            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Willkommen zurück Max!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        LaunchedEffect(vm.showGreeting) {
            if (vm.showGreeting) {
                delay(5000)
                vm.setShowGreeting(false)      // ← hier aufrufen
            }
        }

        // 2) Datumsauswahl
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Vortag",
                tint = Color.Gray,
                modifier = Modifier
                    .clickable { selectedDate = addDays(selectedDate, -1) }
                    .size(24.dp)
            )
            Text(
                text = headerText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = headerColor
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Nächster Tag",
                tint = Color.Gray,
                modifier = Modifier
                    .clickable { selectedDate = addDays(selectedDate, 1) }
                    .size(24.dp)
            )
        }

        // 3) Scrollbarer Termin-Bereich nimmt den ganzen Rest ein
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Column {
                vm.appointments
                    .filter { it.date == selectedDateString }
                    .forEach { appt ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                                .clickable {
                                    vm.selectAppointment(appt)
                                    navController.navigate("appointmentdetails")
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                "${appt.projectName}\nVon ${appt.start} bis ${appt.end}",
                                fontSize = 12.sp
                            )
                        }
                    }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4) Mein Status
        val testStatusUpdates = listOf(
            StatusUpdate("Urlaub", todayString, todayString),
            StatusUpdate("Krank", "10.04.2025", "12.04.2025"),
            StatusUpdate("Teammeeting", "08.05.2025", "08.05.2025")
        )
        val todaysUpdate = getTodaysUpdate(testStatusUpdates)
        val currentStatus = todaysUpdate?.type ?: "Verfügbar"
        val statusIcon = when (currentStatus.lowercase(Locale.getDefault())) {
            "krank" -> Icons.Filled.Bedtime
            "urlaub" -> Icons.Filled.BeachAccess
            "verfügbar" -> Icons.Filled.Check
            else -> Icons.Filled.Check
        }
        val iconTint = if (currentStatus.lowercase(Locale.getDefault()) == "verfügbar")
            Color(0xFF4CAF50) else Color.Gray

        Text(
            text = "Mein Status",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Verwaltung",
                    tint = Color(0xFF757575),
                    modifier = Modifier
                        .clickable { navController.navigate("statussettings") }
                        .size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5) Meine Angaben
        var showUrlaub by remember { mutableStateOf(false) }
        var showStunden by remember { mutableStateOf(false) }
        val testUrlaubTage = "20 Tage"
        val testStundenSaldo = "+5h 30m"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Meine Urlaubstage",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (showUrlaub) {
                            Text(
                                text = testUrlaubTage,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Icon(
                            imageVector = if (showUrlaub) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Urlaubstage",
                            tint = Color.Gray,
                            modifier = Modifier
                                .clickable { showUrlaub = !showUrlaub }
                                .size(24.dp)
                        )

                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mein Stundensaldo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (showStunden) {
                            Text(
                                text = testStundenSaldo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Icon(
                            imageVector = if (showStunden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Stundensaldo",
                            tint = Color.Gray,
                            modifier = Modifier
                                .clickable { showStunden = !showStunden }
                                .size(24.dp)
                        )

                    }
                }
            }
        }
        // Weitere Inhalte der Übersichtsseite …
    }
}
