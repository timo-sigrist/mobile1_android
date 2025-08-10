package com.example.buildnote.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Calendar
import com.example.buildnote.model.ActionType
import com.example.buildnote.model.Appointment
import com.example.buildnote.model.AreaEntry
import com.example.buildnote.model.Customer
import com.example.buildnote.model.LengthEntry
import com.example.buildnote.model.MaterialEntry
import com.example.buildnote.model.NewMeasurement
import com.example.buildnote.model.Project
import com.example.buildnote.model.RoomEntry
import com.example.buildnote.model.TimeEntry



class MaterialViewModel : ViewModel() {

    // Test‑Kunden
    private val customers = listOf(
        Customer(1, "Max Mustermann",   "max@example.com",  "+49 170 1234567"),
        Customer(2, "Erika Musterfrau", "erika@example.com", "+49 170 9876543"),
        Customer(3, "Hans Meier",       "hans@example.com",  "+49 170 5555555")
    )

    // Test‑Projekte mit Kunden‑Verknüpfung
    private val projects = listOf(
        Project("Projekt Alpha",   "Musterstraße 12",   "12345 Musterstadt", "Ebene 3",    "Alpha‑Projektbeschreibung",   1713552000000, customerId = 1),
        Project("Projekt Beta",    "Wolfsgartenstraße 27","63329 Egelsbach",   "Räume A/B",  "Beta‑Projektbeschreibung",    1713110400000, customerId = 2),
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
        MaterialEntry("Projekt Gamma", "Dämmung",20,  "m")
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
            notizen            = notizen,
            artAufmass         = artAufmass,
            lengthUnit         = lengthUnit,
            areaUnit           = areaUnit,
            roomUnit           = roomUnit,
            lengthEntries      = lengthEntries.toList(),
            areaEntries        = areaEntries.toList(),
            roomEntries        = roomEntries.toList()
        )
        // TODO: HTTP call
    }

    // ————————————
    // **Zeiterfassung mit Safe‑Entry & Mindestdauer**
    // ————————————
    var timeTrackingProject: Project? by mutableStateOf(null); private set
    var timeTrackingAction: ActionType by mutableStateOf(ActionType.ARBEIT); private set
    var timerRunning: Boolean by mutableStateOf(false); private set
    private var timerStartTimestamp: Long by mutableStateOf(0L)
    private val _timeEntries = mutableStateListOf<TimeEntry>()
    val timeEntries: List<TimeEntry> get() = _timeEntries

    // Mindesteintrag: 1 Minute (60.000 ms)
    private val MIN_DURATION_MS = 60_000L

    fun selectTimeProject(proj: Project) {
        if (timerRunning && timeTrackingProject != null) {
            val now = System.currentTimeMillis()
            if (now - timerStartTimestamp >= MIN_DURATION_MS) {
                _timeEntries.add(
                    TimeEntry(
                        projectName = timeTrackingProject!!.projectName,
                        action = timeTrackingAction,
                        start = timerStartTimestamp,
                        end = now
                    )
                )
            }
            timerStartTimestamp = now
        }
        timeTrackingProject = proj
    }

    fun selectTimeAction(action: ActionType) {
        if (timerRunning && timeTrackingProject != null) {
            val now = System.currentTimeMillis()
            if (now - timerStartTimestamp >= MIN_DURATION_MS) {
                _timeEntries.add(
                    TimeEntry(
                        projectName = timeTrackingProject!!.projectName,
                        action = timeTrackingAction,
                        start = timerStartTimestamp,
                        end = now
                    )
                )
            }
            timerStartTimestamp = now
        }
        timeTrackingAction = action
    }

    fun toggleTimer() {
        val now = System.currentTimeMillis()
        if (timerRunning && timeTrackingProject != null) {
            if (now - timerStartTimestamp >= MIN_DURATION_MS) {
                _timeEntries.add(
                    TimeEntry(
                        projectName = timeTrackingProject!!.projectName,
                        action = timeTrackingAction,
                        start = timerStartTimestamp,
                        end = now
                    )
                )
            }
            timerRunning = false
        } else {
            timerStartTimestamp = now
            timerRunning = true
        }
    }

    /**
     * Löscht einen zuvor erfassten Eintrag.
     */
    fun deleteTimeEntry(entry: TimeEntry) {
        _timeEntries.remove(entry)
    }

    /**
     * Fügt einen manuell nachgetragenen Eintrag hinzu.
     */
    fun addManualEntry(entry: TimeEntry) {
        _timeEntries.add(entry)
    }

    fun getTodayEntries(): List<TimeEntry> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        return _timeEntries.filter { it.start >= startOfDay }
    }

    // ————————————
    // **Tagesnavigation** (neu)
    // ————————————
    private val DAY_MS = 24 * 60 * 60 * 1000L

    private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayStart: Long
        get() = startOfDay(System.currentTimeMillis())

    var selectedDayStart: Long by mutableStateOf(todayStart)
        private set

}
