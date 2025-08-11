package com.buildnote.android.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Datenklasse für Statusupdates.
data class StatusUpdate(val type: String, val from: String, val to: String)

// Funktion, die überprüft, ob das heutige Datum zwischen den angegebenen Daten liegt.
// Erwartetes Format: "dd.MM.yyyy"
fun isTodayBetween(from: String, to: String): Boolean {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val fromDate = sdf.parse(from) ?: return false
    val toDate = sdf.parse(to) ?: return false
    val today = sdf.parse(sdf.format(Date())) ?: return false
    return !today.before(fromDate) && !today.after(toDate)
}

// Funktion, die aus einer Liste von Statusupdates das Update zurückgibt, das für heute gilt.
fun getTodaysUpdate(statusUpdates: List<StatusUpdate>): StatusUpdate? {
    return statusUpdates.find { isTodayBetween(it.from, it.to) }
}
