package com.buildnote.android.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.buildnote.android.model.ProjectDto
import com.buildnote.android.model.Appointment
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.ChatMessage
import com.buildnote.android.model.Customer
import com.buildnote.android.model.DocumentEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.MaterialEntry
import com.buildnote.android.model.NewMeasurement
import com.buildnote.android.model.ProjectSortMode
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.service.ProjectService


@SuppressLint("NewApi")
class ProjectViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private var service: ProjectService? = null
    //
    private var projects: List<ProjectDto> = emptyList()
    var selectedProject: ProjectDto? by mutableStateOf(null); private set
    var searchQuery by mutableStateOf("")
    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)

    init {
        initService(context)
        loadProjects()
    }

    fun initService(context: Context) {
        service = ProjectService.create(context)
    }

    fun loadProjects() {
        service?.getProjects(
            onResult = { list -> projects = list },
            onError = { e -> Log.e("Error on load projecsts", e.message.orEmpty()) }
        )
    }

    fun getFilteredSortedProjects(): List<ProjectDto> {
        val f = projects.filter { it.name.contains(searchQuery, true) }
        return when (sortMode) {
            ProjectSortMode.NEWEST_FIRST -> f.sortedByDescending { it.createdAt }
            ProjectSortMode.OLDEST_FIRST -> f.sortedBy { it.createdAt }
            ProjectSortMode.ALPHABETICAL -> f.sortedBy { it.name }
        }
    }

    fun selectProject(proj: ProjectDto) { selectedProject = proj }

    fun updateSearchQuery(q: String) { searchQuery = q }
    fun updateSortMode(updatedSortMode: ProjectSortMode) { sortMode = updatedSortMode }


    fun getCustomerForSelected(): Customer? =
        Customer(1, "Max Mustermann", "max@example.com", "+49 170 1234567")

    // Material
    private val _materialEntries = mutableStateListOf(
        MaterialEntry("Projekt Alpha", "Putz", 5, "m³"),
        MaterialEntry("Projekt Beta", "Ziegel", 100, "stk"),
        MaterialEntry("Projekt Gamma", "Dämmung", 20, "m")
    )
    val materialEntries: List<MaterialEntry> get() = _materialEntries
    private val userAddedEntries = mutableListOf<MaterialEntry>()

    fun getMaterialEntriesForSelectedProject(): List<MaterialEntry> =
        selectedProject?.let { proj -> _materialEntries.filter { it.projectName == proj.name } }
            ?: emptyList()

    fun addMaterialEntry(entry: MaterialEntry) {
        _materialEntries.add(entry)
        userAddedEntries.add(entry)
    }

    fun undoLastMaterialEntry() {
        selectedProject?.let { proj ->
            val last = userAddedEntries.lastOrNull { it.projectName == proj.name } ?: return
            _materialEntries.remove(last)
            userAddedEntries.remove(last)
        }
    }

    fun getProjectFor(appt: Appointment): ProjectDto? =
        projects.firstOrNull { it.name == appt.projectName }

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
