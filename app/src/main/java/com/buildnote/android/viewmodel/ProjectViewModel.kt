package com.buildnote.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.buildnote.android.api.repository.ProjectRepository
import com.buildnote.android.model.Appointment
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.ChatMessage
import com.buildnote.android.model.Customer
import com.buildnote.android.model.DocumentEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.MaterialEntry
import com.buildnote.android.model.NewMeasurement
import com.buildnote.android.model.Project
import com.buildnote.android.model.ProjectSortMode
import com.buildnote.android.model.RoomEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 Ein einziger, klarer UI-Zustand pro Screen (Unidirectional Data Flow).

Warum so? Die UI muss nur einen State beobachten und je nach Feldern rendern – kein Wirrwarr aus mehreren LiveDatas/Flows.
 */
data class ProjectUiState(
    val isLoading: Boolean = false,
    val items: List<Project> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repo: ProjectRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectUiState()) // veränderbar nur im ViewModel.
    val state: StateFlow<ProjectUiState> = _state // unveränderbar (read-only) für die UI.


    init { refresh() } // Beim Erstellen des ViewModels wird sofort geladen (gut für „Screen öffnet → Daten da“).

    fun refresh() = viewModelScope.launch { // startet eine Coroutine, die sich am VM-Lifecycle orientiert (wird automatisch gecancelt, wenn das VM beendet wird).
        _state.update { it.copy(isLoading = true, error = null) } // nutzt die Immutability der data class (wir erzeugen einen neuen State, statt den alten zu mutieren).
        runCatching { repo.getProjects() }
            .onSuccess { list -> _state.update { it.copy(isLoading = false, items = list) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message ?: "Fehler") } }
    }

    // Test‑Kunden
    private val customers = listOf(
        Customer(1, "Max Mustermann", "max@example.com", "+49 170 1234567"),
        Customer(2, "Erika Musterfrau", "erika@example.com", "+49 170 9876543"),
        Customer(3, "Hans Meier", "hans@example.com", "+49 170 5555555")
    )

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

    // Material
    private val _materialEntries = mutableStateListOf(
        MaterialEntry("Projekt Alpha", "Putz", 5, "m³"),
        MaterialEntry("Projekt Beta", "Ziegel", 100, "stk"),
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

    var selectedProject: Project? by mutableStateOf(null); private set
    fun selectProject(proj: Project) { selectedProject = proj }


    fun getProjectFor(appt: Appointment): Project? =
        projects.firstOrNull { it.projectName == appt.projectName }

    var searchQuery by mutableStateOf("")
    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)
    fun updateSearchQuery(q: String) { searchQuery = q }
    fun updateSortMode(updatedSortMode: ProjectSortMode) { sortMode = updatedSortMode }
    fun getFilteredSortedProjects(): List<Project> {
        val f = projects.filter { it.projectName.contains(searchQuery, true) }
        return when (sortMode) {
            ProjectSortMode.NEWEST_FIRST -> f.sortedByDescending { it.createdAt }
            ProjectSortMode.OLDEST_FIRST -> f.sortedBy { it.createdAt }
            ProjectSortMode.ALPHABETICAL -> f.sortedBy { it.projectName }
        }
    }

    // Aufmaß
    var aufmassBezeichnung by mutableStateOf("")
    var notizen by mutableStateOf("")
    var artAufmass by mutableStateOf("")        // "Länge","Fläche","Raum"
    var lengthUnit by mutableStateOf("m")
    var areaUnit by mutableStateOf("m²")
    var roomUnit by mutableStateOf("m³")

    val lengthEntries = mutableStateListOf<LengthEntry>()
    val areaEntries = mutableStateListOf<AreaEntry>()
    val roomEntries = mutableStateListOf<RoomEntry>()

    fun addLengthEntry(e: LengthEntry) { lengthEntries += e }
    fun addAreaEntry(e: AreaEntry)   { areaEntries += e }
    fun addRoomEntry(e: RoomEntry)   { roomEntries += e }

    fun totalLength(): Double = lengthEntries.sumOf {
        (it.laenge ?: 0.0) - (if (it.includeAbzug) (it.abzug ?: 0.0) else 0.0)
    }
    fun totalArea(): Double = areaEntries.sumOf {
        val base = (it.laenge ?: 0.0) * (it.breite ?: 0.0)
        val abz  = if (it.includeAbzug) ((it.abzugLaenge ?: 0.0) * (it.abzugBreite ?: 0.0)) else 0.0
        base - abz
    }
    fun totalRoom(): Double = roomEntries.sumOf {
        val base = (it.laenge ?: 0.0) * (it.breite ?: 0.0) * (it.hoehe ?: 0.0)
        val abz  = if (it.includeAbzug) ((it.abzugLaenge ?: 0.0) * (it.abzugBreite ?: 0.0) * (it.abzugHoehe ?: 0.0)) else 0.0
        base - abz
    }
    fun sendMeasurement() {
        val payload = NewMeasurement(
            aufmassBezeichnung = aufmassBezeichnung,
            notizen = notizen,
            artAufmass = artAufmass,
            lengthUnit = lengthUnit,
            areaUnit = areaUnit,
            roomUnit = roomUnit,
            lengthEntries = lengthEntries.toList(),
            areaEntries = areaEntries.toList(),
            roomEntries = roomEntries.toList()
        )
        // TODO: HTTP call
    }


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
