package com.example.buildnote.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Calendar
import android.net.Uri
import com.example.buildnote.model.ActionType
import com.example.buildnote.model.Appointment
import com.example.buildnote.model.AreaEntry
import com.example.buildnote.model.ChatMessage
import com.example.buildnote.model.Customer
import com.example.buildnote.model.DocumentEntry
import com.example.buildnote.model.LengthEntry
import com.example.buildnote.model.MaterialEntry
import com.example.buildnote.model.NewMeasurement
import com.example.buildnote.model.Project
import com.example.buildnote.model.RoomEntry
import com.example.buildnote.model.TimeEntry
import com.example.buildnote.model.ProjectSortMode

class ChatViewModel : ViewModel() {

    // Test‑Termine
    val appointments = listOf(
        Appointment("19.04.2025", "09:00", "10:00", "Projekt Alpha", "Kick‑off Meeting"),
        Appointment("18.04.2025", "11:15", "12:45", "Projekt Beta", "Skizzen fertigstellen"),
        Appointment("18.04.2025", "13:30", "14:30", "Projekt Gamma", "Abnahme vor Ort")
    )

    // Test‑Kunden
    private val customers = listOf(
        Customer(1, "Max Mustermann", "max@example.com", "+49 170 1234567"),
        Customer(2, "Erika Musterfrau", "erika@example.com", "+49 170 9876543"),
        Customer(3, "Hans Meier",       "hans@example.com",  "+49 170 5555555")
    )

    // Test‑Projekte mit Kunden‑Verknüpfung
    private val projects = listOf(
        Project("Projekt Alpha",   "Musterstraße 12",   "12345 Musterstadt", "Ebene 3",    "Alpha‑Projektbeschreibung",   1713552000000, customerId = 1),
        Project(
            "Projekt Beta",
            "Wolfsgartenstraße 27",
            "63329 Egelsbach",
            "Räume A/B",
            "Beta‑Projektbeschreibung",
            1713110400000,
            customerId = 2
        ),
        Project("Projekt Gamma",   "Teststraße 99",      "11111 Testhausen",  "",           "Gamma‑Projektbeschreibung",   1712755200000, customerId = 3),
        Project("Projekt Delta",   "Neue Straße 1",      "54321 Demo-Stadt",  "",           "Delta‑Projektbeschreibung",   1712500000000, customerId = 1),
        Project("Projekt Epsilon", "Hauptweg 8",         "22222 Epsilonia",   "2. OG",      "Epsilon‑Projektbeschreibung", 1712650000000, customerId = 2),
        Project("Projekt Zeta",    "Rosenweg 10",        "33333 Zetastadt",   "",           "Zeta‑Projektbeschreibung",     1712800000000, customerId = 3),
        Project("Projekt Eta",     "Lindenallee 5",      "44444 Etagora",     "Souterrain","Eta‑Projektbeschreibung",    1712950000000, customerId = 1),
        Project("Projekt Theta",   "Bergstraße 7",       "55555 Thetaland",   "",           "Theta‑Projektbeschreibung",    1713100000000, customerId = 2),
        Project("Projekt Iota",    "Talweg 3",           "66666 Iotaville",   "Annex",      "Iota‑Projektbeschreibung",     1713250000000, customerId = 3),
        Project("Projekt Kappa",   "Waldweg 11",         "77777 Kappacity",   "",           "Kappa‑Projektbeschreibung",    1713400000000, customerId = 1)
    )

    // Material
    private val _materialEntries = mutableStateListOf(
        MaterialEntry("Projekt Alpha", "Putz",    5,   "m³"),
        MaterialEntry("Projekt Beta",  "Ziegel", 100, "stk"),
        MaterialEntry("Projekt Gamma", "Dämmung", 20, "m")
    )
    val materialEntries: List<MaterialEntry> get() = _materialEntries
    private val userAddedEntries = mutableListOf<MaterialEntry>()

    fun getMaterialEntriesForSelectedProject(): List<MaterialEntry> =
        selectedProject?.let { proj -> _materialEntries.filter { it.projectName == proj.projectName } }
            ?: emptyList()

    fun addMaterialEntry(entry: MaterialEntry) {
        _materialEntries.add(entry)
        userAddedEntries.add(entry)
    }

    fun undoLastMaterialEntry() {
        selectedProject?.let { proj ->
            val last = userAddedEntries.lastOrNull { it.projectName == proj.projectName } ?: return
            _materialEntries.remove(last)
            userAddedEntries.remove(last)
        }
    }

    // Auswahl‑State
    var selectedAppointment: Appointment? by mutableStateOf(null); private set
    fun selectAppointment(appt: Appointment) { selectedAppointment = appt }

    var selectedProject: Project? by mutableStateOf(null); private set
    fun selectProject(proj: Project) { selectedProject = proj }

    private var _showGreeting by mutableStateOf(true)
    val showGreeting: Boolean get() = _showGreeting
    fun setShowGreeting(v: Boolean) { _showGreeting = v }

    fun getProjectFor(appt: Appointment): Project? =
        projects.firstOrNull { it.projectName == appt.projectName }

    fun getCustomerForSelected(): Customer? =
        customers.firstOrNull { it.id == selectedProject?.customerId }

    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)
    fun updateSortMode(updatedSortMode: ProjectSortMode) { sortMode = updatedSortMode }
    fun getFilteredSortedProjects(): List<Project> {
        return when (sortMode) {
            ProjectSortMode.NEWEST_FIRST -> projects.sortedByDescending { it.createdAt }
            ProjectSortMode.OLDEST_FIRST -> projects.sortedBy { it.createdAt }
            ProjectSortMode.ALPHABETICAL -> projects.sortedBy { it.projectName }
        }
    }


// … in class AppointmentViewModel :

    // Beispiel-Dokumente (kannst du später leeren oder durch echte ersetzen)
    private val _documents = mutableStateListOf(
        DocumentEntry("Projekt Alpha", "Planung.pdf", Uri.parse("file:///android_asset/plan.pdf")),
        DocumentEntry("Projekt Beta",  "Foto1.jpg", Uri.parse("file:///android_asset/foto1.jpg"))
    )
    val documents: List<DocumentEntry> get() = _documents

    /**
     * Liefert alle Dokumente für ein Projekt.
     */
    fun getDocumentsForProject(projectName: String): List<DocumentEntry> =
        _documents.filter { it.projectName == projectName }

    /**
     * Fügt neue Dokument-URIs dem Projekt hinzu.
     */
    fun addDocumentsForProject(projectName: String, uris: List<Uri>) {
        uris.forEach { uri ->
            val name = uri.lastPathSegment ?: "Dokument"
            _documents.add(DocumentEntry(projectName, name, uri))
        }
    }




    private val _messages = mutableStateListOf<ChatMessage>()
    fun getMessages(projectName: String): List<ChatMessage> =
        _messages.filter { it.projectName == projectName }

    fun sendMessage(projectName: String, text: String, attachments: List<Uri>) {
        _messages.add(
            ChatMessage(
                projectName = projectName,
                senderName = "Du",
                text = text,
                attachments = attachments,
                isMine = true
            )
        )
        // Hier könntest du später noch simuliert Antworten von "Kollegen" hinzufügen…
    }




}
