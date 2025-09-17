package com.buildnote.android.service

import android.annotation.SuppressLint
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.AreaUnit
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.LengthUnit
import com.buildnote.android.model.Material
import com.buildnote.android.model.MeasurementRecord
import com.buildnote.android.model.MeasurementType
import com.buildnote.android.model.Project
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.model.RoomUnit
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId

object JsonBuildnoteParser : BuildnoteParser {
    private fun JSONObject.flexInt(key: String, default: Int = 0): Int =
        when (val v = this.opt(key)) {
            is Number -> v.toInt()
            is String -> v.toIntOrNull() ?: default
            else -> default
        }

    private fun JSONObject.flexLong(key: String, default: Long = 0L): Long =
        when (val v = this.opt(key)) {
            is Number -> v.toLong()
            is String -> v.toLongOrNull() ?: default
            else -> default
        }

    private fun JSONObject.flexDouble(name: String): Double? {
        val v = this.opt(name)
        return when (v) {
            is Number -> v.toDouble()
            is String -> v.trim().replace(',', '.').toDoubleOrNull()
            else -> null
        }
    }

    @SuppressLint("NewApi")
    override fun parseProjects(raw: String): List<Project> {
        val text = raw.trim()
        if (text.isEmpty()) return emptyList()

        val zone = ZoneId.systemDefault()

        fun parseDateToMillis(d: String?): Long {
            if (d.isNullOrBlank()) return System.currentTimeMillis()
            val ld = LocalDate.parse(d) // erwartet ISO yyyy-MM-dd
            return ld.atStartOfDay(zone).toInstant().toEpochMilli()
        }

        fun map(o: JSONObject) = Project(
            id = o.flexLong("id", 0L),
            name = o.optString("name"),
            street = o.optString("street"),
            cityZip = "${
                o.flexInt("zip", 0).takeIf { it != 0 } ?: ""
            } ${o.optString("city")}".trim(),
            additionalInfo = o.optString("additionalInfo"),
            description = o.optString("description"),
            createdAt = parseDateToMillis(o.optString("createdDate")),
            customerId = 0 // oder o.flexLong("customerId", 0L) falls vorhanden
        )

        return try {
            if (text.startsWith("[")) {
                val arr = JSONArray(text)
                buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        add(map(o))
                    }
                }
            } else {
                listOf(map(JSONObject(text)))
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }


    private fun String.norm(): String =
        lowercase()
            .replace(" ", "")
            .replace("²", "2")
            .replace("³", "3")
            .replace("^2", "2")
            .replace("^3", "3")

    private fun parseMeasurementType(
        o: JSONObject,
        lengthEntries: List<LengthEntry>,
        areaEntries: List<AreaEntry>,
        roomEntries: List<RoomEntry>
    ): MeasurementType {
        val raw = o.optString("measurementType", "").trim()
        if (raw.isNotEmpty()) {
            when (raw.lowercase()) {
                "length", "länge", "laenge", "len", "l" -> return MeasurementType.LENGTH
                "area", "fläche", "flaeche", "a", "fl" -> return MeasurementType.AREA
                "room", "raum", "r" -> return MeasurementType.ROOM
            }
        }
        return when {
            lengthEntries.isNotEmpty() -> MeasurementType.LENGTH
            areaEntries.isNotEmpty() -> MeasurementType.AREA
            roomEntries.isNotEmpty() -> MeasurementType.ROOM
            else -> MeasurementType.LENGTH
        }
    }

    private fun parseLengthUnit(raw: String): LengthUnit {
        return when (raw.norm()) {
            "m", "meter", "meters" -> LengthUnit.M
            "cm" -> LengthUnit.CM
            "mm" -> LengthUnit.MM
            "km" -> LengthUnit.KM
            else -> LengthUnit.M
        }
    }

    private fun parseAreaUnit(raw: String): AreaUnit {
        return when (raw.norm()) {
            "m2", "m^2", "qm", "sqm", "squaremeter", "squaremeters" -> AreaUnit.M
            "cm2", "cm^2" -> AreaUnit.CM
            "mm2", "mm^2" -> AreaUnit.MM
            "km2", "km^2" -> AreaUnit.KM
            "m" /* Server liefert manchmal "m" */ -> AreaUnit.M
            "cm" -> AreaUnit.CM
            "mm" -> AreaUnit.MM
            "km" -> AreaUnit.KM
            else -> AreaUnit.M
        }
    }

    private fun parseRoomUnit(raw: String): RoomUnit {
        return when (raw.norm()) {
            "m3", "m^3", "cbm", "cubicmeter", "cubicmeters" -> RoomUnit.M
            "cm3", "cm^3" -> RoomUnit.CM
            "mm3", "mm^3" -> RoomUnit.MM
            "km3", "km^3" -> RoomUnit.KM
            "m" /* falls der Server "m" schickt */ -> RoomUnit.M
            "cm" -> RoomUnit.CM
            "mm" -> RoomUnit.MM
            "km" -> RoomUnit.KM
            else -> RoomUnit.M
        }
    }

    override fun parseMaterials(raw: String): List<Material> {
        val text = raw.trim()
        if (text.isEmpty()) return emptyList()

        fun map(o: JSONObject) = Material(
            name = o.optString("name"),
            number = o.flexInt("number", 0),
            unit = o.optString("unit"),
            projectId = o.flexLong("projectId", 0L)
        )

        return try {
            if (text.startsWith("[")) {
                val arr = JSONArray(text)
                buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        add(map(o))
                    }
                }
            } else {
                listOf(map(JSONObject(text)))
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun parseMeasurements(raw: String, projectId: Long): List<MeasurementRecord> {
        val text = raw.trim()
        if (text.isEmpty()) return emptyList()

        fun map(o: JSONObject): MeasurementRecord? {
            val lengthEntries = parseLengthEntries(o.optJSONArray("lengthEntries"))
            val areaEntries = parseAreaEntries(o.optJSONArray("areaEntries"))
            val roomEntries = parseRoomEntries(o.optJSONArray("roomEntries"))

            val type = parseMeasurementType(o, lengthEntries, areaEntries, roomEntries)

            return MeasurementRecord(
                name = o.optString("name"),
                description = o.optString("description"),
                notes = o.optString("notes"),
                total = o.flexDouble("total") ?: 0.0,
                measurementType = type,
                lengthUnit = parseLengthUnit(o.optString("lengthUnit")),
                areaUnit = parseAreaUnit(o.optString("areaUnit")),
                roomUnit = parseRoomUnit(o.optString("roomUnit")),
                lengthEntries = lengthEntries,
                areaEntries = areaEntries,
                roomEntries = roomEntries,
                projectId = projectId,
                userId = 1
            )
        }

        return try {
            if (text.startsWith("[")) {
                val arr = JSONArray(text)
                buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        map(o)?.let(::add)
                    }
                }
            } else {
                map(JSONObject(text))?.let(::listOf) ?: emptyList()
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    private fun parseLengthEntries(arr: JSONArray?): MutableList<LengthEntry> {
        if (arr == null) return mutableListOf()
        val out = mutableListOf<LengthEntry>()
        for (i in 0 until arr.length()) {
            val e = arr.optJSONObject(i) ?: continue
            val deduction = e.flexDouble("deductionLength")
            val include = e.optBoolean("includeDeduction", (deduction ?: 0.0) > 0.0)
            out += LengthEntry(
                description = e.optString("description"),
                length = e.flexDouble("length"),
                includeDeduction = include,
                deductionLength = deduction
            )
        }
        return out
    }

    private fun parseAreaEntries(arr: JSONArray?): MutableList<AreaEntry> {
        if (arr == null) return mutableListOf()
        val out = mutableListOf<AreaEntry>()
        for (i in 0 until arr.length()) {
            val e = arr.optJSONObject(i) ?: continue
            val dL = e.flexDouble("deductionLength")
            val dW = e.flexDouble("deductionWidth")
            val include = e.optBoolean("includeDeduction", (dL ?: 0.0) > 0.0 || (dW ?: 0.0) > 0.0)
            out += AreaEntry(
                description = e.optString("description"),
                length = e.flexDouble("length"),
                width = e.flexDouble("width"),
                includeDeduction = include,
                deductionLength = dL,
                deductionWidth = dW
            )
        }
        return out
    }

    private fun parseRoomEntries(arr: JSONArray?): MutableList<RoomEntry> {
        if (arr == null) return mutableListOf()
        val out = mutableListOf<RoomEntry>()
        for (i in 0 until arr.length()) {
            val e = arr.optJSONObject(i) ?: continue

            fun JSONObject.firstString(vararg keys: String): String =
                keys.firstNotNullOfOrNull { k -> optString(k).takeIf { it.isNotEmpty() } } ?: ""

            fun JSONObject.firstDouble(vararg keys: String): Double? =
                keys.asSequence().mapNotNull { k -> flexDouble(k) }.firstOrNull()

            val abzugL = e.firstDouble("abzugLaenge", "deductionLength")
            val abzugB = e.firstDouble("abzugBreite", "deductionWidth")
            val abzugH = e.firstDouble("abzugHoehe", "deductionHeight")
            val include = e.optBoolean(
                "includeAbzug",
                (abzugL ?: 0.0) > 0.0 || (abzugB ?: 0.0) > 0.0 || (abzugH ?: 0.0) > 0.0
            )

            out += RoomEntry(
                description = e.firstString("raumbezeichnung", "roomName", "description"),
                length = e.firstDouble("laenge", "length"),
                width = e.firstDouble("breite", "width"),
                height = e.firstDouble("hoehe", "height"),
                includeDeduction = include,
                deductionLength = abzugL,
                deductionWidth = abzugB,
                deductionHeight = abzugH
            )
        }
        return out
    }
}