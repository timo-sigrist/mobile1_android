package com.buildnote.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.net.Uri
import com.buildnote.android.model.Appointment
import com.buildnote.android.model.ChatMessage
import com.buildnote.android.model.Customer
import com.buildnote.android.model.DocumentEntry
import com.buildnote.android.model.Project
import com.buildnote.android.model.ProjectSortMode

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
        Customer(3, "Hans Meier", "hans@example.com", "+49 170 5555555")
    )

    // Test‑Projekte mit Kunden‑Verknüpfung
    private val projects = listOf(
        Project(0,
            "Projekt Alpha",
            "Musterstraße 12",
            "12345 Musterstadt",
            "Ebene 3",
            "Alpha‑Projektbeschreibung",
            1713552000000,
            customerId = 1
        ),
        Project(1,
            "Projekt Beta",
            "Wolfsgartenstraße 27",
            "63329 Egelsbach",
            "Räume A/B",
            "Beta‑Projektbeschreibung",
            1713110400000,
            customerId = 2
        ),
        Project(2,
            "Projekt Gamma",
            "Teststraße 99",
            "11111 Testhausen",
            "",
            "Gamma‑Projektbeschreibung",
            1712755200000,
            customerId = 3
        ),
        Project(3,
            "Projekt Delta",
            "Neue Straße 1",
            "54321 Demo-Stadt",
            "",
            "Delta‑Projektbeschreibung",
            1712500000000,
            customerId = 1
        ),
        Project(4,
            "Projekt Epsilon",
            "Hauptweg 8",
            "22222 Epsilonia",
            "2. OG",
            "Epsilon‑Projektbeschreibung",
            1712650000000,
            customerId = 2
        )
    )



    // Auswahl‑State
    var selectedAppointment: Appointment? by mutableStateOf(null); private set
    fun selectAppointment(appt: Appointment) { selectedAppointment = appt }

    var selectedProject: Project? by mutableStateOf(null); private set
    fun selectProject(proj: Project) { selectedProject = proj }

    private var _showGreeting by mutableStateOf(true)
    val showGreeting: Boolean get() = _showGreeting
    fun setShowGreeting(v: Boolean) { _showGreeting = v }

    fun getProjectFor(appt: Appointment): Project? =
        projects.firstOrNull { it.name == appt.projectName }

    fun getCustomerForSelected(): Customer? =
        customers.firstOrNull { it.id == selectedProject?.customerId }

    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)
    fun updateSortMode(updatedSortMode: ProjectSortMode) { sortMode = updatedSortMode }
    fun getFilteredSortedProjects(): List<Project> {
        return when (sortMode) {
            ProjectSortMode.NEWEST_FIRST -> projects.sortedByDescending { it.createdAt }
            ProjectSortMode.OLDEST_FIRST -> projects.sortedBy { it.createdAt }
            ProjectSortMode.ALPHABETICAL -> projects.sortedBy { it.name }
        }
    }


// … in class AppointmentViewModel :

    // Beispiel-Dokumente (kannst du später leeren oder durch echte ersetzen)
    private val _documents = mutableStateListOf(
        DocumentEntry("Projekt Alpha", "Planung.pdf", Uri.parse("file:///android_asset/plan.pdf")),
        DocumentEntry("Projekt Beta", "Foto1.jpg", Uri.parse("file:///android_asset/foto1.jpg"))
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
