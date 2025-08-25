package com.buildnote.android.service

import android.annotation.SuppressLint
import com.buildnote.android.service.BuildnoteParser
import com.buildnote.android.model.Material
import com.buildnote.android.model.Project
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
            cityZip = "${o.flexInt("zip", 0).takeIf { it != 0 } ?: ""} ${o.optString("city")}".trim(),
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
}