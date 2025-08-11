package com.buildnote.android.model

data class NewMeasurement(
    val aufmassBezeichnung: String,
    val notizen: String,
    val artAufmass: String,
    val lengthUnit: String,
    val areaUnit: String,
    val roomUnit: String,
    val lengthEntries: List<LengthEntry>,
    val areaEntries: List<AreaEntry>,
    val roomEntries: List<RoomEntry>
)