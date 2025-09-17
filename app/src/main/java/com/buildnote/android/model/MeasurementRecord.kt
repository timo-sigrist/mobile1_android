package com.buildnote.android.model

enum class MeasurementType(val displayName: String) {
    LENGTH("Länge"),
    AREA("Fläche"),
    ROOM("Raum")

}

enum class LengthUnit(val displayName: String) {
    M("m"),
    CM("cm"),
    MM("mm"),
    KM("km");

    companion object {
        fun fromString(value: String?): LengthUnit {
            if (value.isNullOrBlank()) return M
            values().find { it.name.equals(value, ignoreCase = true) }?.let { return it }
            values().find { it.displayName.equals(value, ignoreCase = true) }?.let { return it }
            return M
        }
    }
}

enum class AreaUnit(val displayName: String) {
    M("m²"),
    CM("cm²"),
    MM("mm²"),
    KM("km²");

    companion object {
        fun fromString(value: String?): AreaUnit {
            if (value.isNullOrBlank()) return M
            values().find { it.name.equals(value, ignoreCase = true) }?.let { return it }
            values().find { it.displayName.equals(value, ignoreCase = true) }?.let { return it }
            return M
        }
    }
}

enum class RoomUnit(val displayName: String) {
    M("m³"),
    CM("cm³"),
    MM("mm³"),
    KM("km³");

    companion object {
        fun fromString(value: String?): RoomUnit {
            if (value.isNullOrBlank()) return M
            values().find { it.name.equals(value, ignoreCase = true) }?.let { return it }
            values().find { it.displayName.equals(value, ignoreCase = true) }?.let { return it }
            return M
        }
    }
}

data class MeasurementRecord(
    var name: String,
    var description: String,
    var notes: String,
    var total: Double,
    var measurementType: MeasurementType,
    var lengthUnit: LengthUnit,
    var areaUnit: AreaUnit,
    var roomUnit: RoomUnit,
    var lengthEntries: MutableList<LengthEntry>,
    var areaEntries: MutableList<AreaEntry>,
    var roomEntries: MutableList<RoomEntry>,
    var projectId: Long,
    var userId: Number = 1 // TODO: Real UserID here
)