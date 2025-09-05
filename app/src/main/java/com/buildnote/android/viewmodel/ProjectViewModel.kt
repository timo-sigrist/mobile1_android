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
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.Customer
import com.buildnote.android.model.DocumentEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.Material
import com.buildnote.android.model.MeasurementRecord
import com.buildnote.android.model.ProjectSortMode
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.service.MaterialService
import com.buildnote.android.service.MeasurementService
import com.buildnote.android.service.ProjectService


@SuppressLint("NewApi")
class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    // Project selection
    private val context = getApplication<Application>().applicationContext
    private var projectService: ProjectService? = null
    private var materialService: MaterialService? = null

    private var measurementService: MeasurementService? = null

    private var _projects: List<Project> = emptyList() // veränderbar nur im ViewModel
    val projects: List<Project> = _projects // unveränderbar (read-only) für die UI.
    var selectedProject: Project? by mutableStateOf(null)
    var searchQuery by mutableStateOf("")
    var sortMode by mutableStateOf(ProjectSortMode.NEWEST_FIRST)


    init {
        initService(context)
        loadProjects()
    }

    fun initService(context: Context) {
        projectService = ProjectService.create(context)
        materialService = MaterialService.create(context)
        measurementService = MeasurementService.create(context)
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
            loadMaterialForProject(proj.id)
            loadMeasurementsForProject(proj.id)
        } else {
            Log.e("Error", "No id found for selected project")
        }
    }

    fun updateSearchQuery(q: String) {
        searchQuery = q
    }

    fun updateSortMode(updatedSortMode: ProjectSortMode) {
        sortMode = updatedSortMode
    }


    fun getCustomerForSelected(): Customer? =
        Customer(1, "Max Mustermann", "max@example.com", "+49 170 1234567")

    //---------------------------------      Material  ----------------------------------------------
    private var _materials: List<Material> = emptyList()
    val materials: List<Material> get() = _materials

    private val userAddedEntries = mutableListOf<Material>()

    fun loadMaterialForProject(projectId: Long) {
        materialService?.getMaterialForProject(
            projectId,
            onResult = { list -> _materials = list },
            onError = { e ->
                Log.e(
                    "Error on load materials for project ${projectId}",
                    e.message.orEmpty()
                )
            })
    }

    fun addMaterialEntry(materialEntry: Material) {
        materialService?.postMaterial(
            material = materialEntry,
            onResult = { saved -> _materials = _materials + saved },
            onError = { e -> Log.e("Error to create material", e.message.orEmpty()) }
        )
    }

    fun undoLastMaterialEntry() {
        selectedProject?.let { proj ->
            val last = userAddedEntries.lastOrNull { it.name == proj.name } ?: return
            userAddedEntries.remove(last)
        }
    }

    //---------------------------------      Aufmaß  ----------------------------------------------
    // Aufmaß Liste
    private var _measurements: MutableList<MeasurementRecord> = mutableListOf()
    val measurements: List<MeasurementRecord> get() = _measurements

    fun loadMeasurementsForProject(projectId: Long) {
        measurementService?.getMeasurementsForProject(
            projectId,
            onResult = { list -> _measurements = list },
            onError = { e ->
                Log.e(
                    "Error on load materials for project ${projectId}",
                    e.message.orEmpty()
                )
            })
    }


    // Aufmaß Detail
    var selectedMeasurement: MeasurementRecord? by mutableStateOf(null)

    fun addLengthEntry() {
        selectedMeasurement?.lengthEntries += LengthEntry(
            "",
            null,
            false,
            null
        )
    }

    fun addAreaEntry() {
        selectedMeasurement?.areaEntries += AreaEntry(
            "",
            null,
            null,
            false,
            null,
            null
        )
    }

    fun addRoomEntry() {
        selectedMeasurement?.roomEntries += RoomEntry(
            "",
            null,
            null,
            null,
            false,
            null,
            null,
            null
        )
    }


    // TODO: Verbessere total-calculation in eine methode
    fun totalLength(): Double =
        selectedMeasurement?.lengthEntries?.sumOf { e ->
            val len = e.length ?: 0.0
            val ded = if (e.includeDeduction) (e.deductionLength ?: 0.0) else 0.0
            (len - ded).coerceAtLeast(0.0)
        } ?: 0.0

    fun totalArea(): Double = selectedMeasurement?.areaEntries?.sumOf {
        val base = (it.length ?: 0.0) * (it.width ?: 0.0)
        val abz = if (it.includeDeduction) ((it.deductionLength ?: 0.0) * (it.deductionWidth
            ?: 0.0)) else 0.0
        base - abz
    } ?: 0.0

    fun totalRoom(): Double = selectedMeasurement?.roomEntries?.sumOf {
        val base = (it.length ?: 0.0) * (it.width ?: 0.0) * (it.height ?: 0.0)
        val abz =
            if (it.includeDeduction) ((it.deductionLength ?: 0.0) * (it.deductionWidth ?: 0.0) * (it.deductionHeight
                ?: 0.0)) else 0.0
        base - abz
    } ?: 0.0

    fun sendMeasurement() {
        _measurements.add(selectedMeasurement!!)
        /*
        TODO: post measuerement record
        val payload = MeasurementRecord(
            name = name,
            notes = notes,
            measurementType = measurementType,
            measurementUnit = measurementType.displayName,
            lengthUnit = lengthUnit,
            areaUnit = areaUnit,
            roomUnit = roomUnit,
            lengthEntries = lengthEntries.toList(),
            areaEntries = areaEntries.toList(),
            roomEntries = roomEntries.toList()
        )*/
    }



    //---------------------------------      Dokumente  ----------------------------------------------
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
}
