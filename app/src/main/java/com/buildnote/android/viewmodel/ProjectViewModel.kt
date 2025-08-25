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
import com.buildnote.android.model.Project
import com.buildnote.android.model.Appointment
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.ChatMessage
import com.buildnote.android.model.Customer
import com.buildnote.android.model.DocumentEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.Material
import com.buildnote.android.model.NewMeasurement
import com.buildnote.android.model.ProjectSortMode
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.service.MaterialService
import com.buildnote.android.service.ProjectService


@SuppressLint("NewApi")
class ProjectViewModel(application: Application): AndroidViewModel(application) {

    // Project selection
    private val context = getApplication<Application>().applicationContext
    private var projectService: ProjectService? = null
    private var materialService: MaterialService? = null

    private var _projects: List<Project> = emptyList() // veränderbar nur im ViewModel
    val projects: List<Project> = _projects // unveränderbar (read-only) für die UI.
    var selectedProject: Project? by mutableStateOf(null)
    var searchQuery by mutableStateOf("")
    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)


    // Material Screen
    private var _materials: List<Material> = emptyList()
    val materials: List<Material> get() = _materials

    init {
        initService(context)
        loadProjects()
    }
    fun initService(context: Context) {
        projectService = ProjectService.create(context)
        materialService = MaterialService.create(context)
    }

    //---------------------------------      Project    ----------------------------------------------
    fun loadProjects() {
        projectService?.getProjects(
            onResult = { list -> _projects = list },
            onError = { e -> Log.e("Error on load projecsts", e.message.orEmpty()) }
        )
    }

    fun getFilteredSortedProjects(): List<Project> {
        val f = _projects.filter { it.name.contains(searchQuery, true) }
        return when (sortMode) {
            ProjectSortMode.NEWEST_FIRST -> f.sortedByDescending { it.createdAt }
            ProjectSortMode.OLDEST_FIRST -> f.sortedBy { it.createdAt }
            ProjectSortMode.ALPHABETICAL -> f.sortedBy { it.name }
        }
    }

    fun selectProject(proj: Project) {
        if (proj.id >= 0) {
            selectedProject = proj
            materialService?.getMaterialForProject(proj.id,
                onResult = { list -> _materials = list },
                onError = { e -> Log.e("Error on load materials for project ${proj.id}", e.message.orEmpty())})
        } else {
            Log.e("Error", "No id found for selected project")
        }
    }

    fun updateSearchQuery(q: String) { searchQuery = q }
    fun updateSortMode(updatedSortMode: ProjectSortMode) { sortMode = updatedSortMode }


    fun getCustomerForSelected(): Customer? =
        Customer(1, "Max Mustermann", "max@example.com", "+49 170 1234567")

    //---------------------------------      Material  ----------------------------------------------
    private val userAddedEntries = mutableListOf<Material>()

    fun addMaterialEntry(materialEntry: Material) {
        materialService?.postMaterial(
            material = materialEntry,
            onResult = { saved -> _materials = _materials + saved },
            onError = { e -> Log.e("Error to create material", e.message.orEmpty())}
        )
    }

    fun undoLastMaterialEntry() {
        selectedProject?.let { proj ->
            val last = userAddedEntries.lastOrNull { it.name == proj.name } ?: return
            userAddedEntries.remove(last)
        }
    }

    fun getProjectFor(appt: Appointment): Project? =
        _projects.firstOrNull { it.name == appt.projectName }

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
