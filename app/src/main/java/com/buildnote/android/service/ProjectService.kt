package com.buildnote.android.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.buildnote.android.service.Utf8StringRequest
import com.buildnote.android.model.ProjectDto
import org.json.JSONArray
import java.time.LocalDate
import java.time.ZoneId


class ProjectService private constructor(
    private val baseUrl: String,
    private val queue: RequestQueue
) {

    companion object {

        private val url = "https://test.buildnote.de/api/"
        /**
         * Erzeuge eine Instanz mit Application-Context und Basis-URL.
         * Beispiel baseUrl: "https://example.com"  (ohne Slash am Ende)
         */
        fun create(context: Context): ProjectService {
            return ProjectService(
                baseUrl = url.trimEnd('/'),
                queue = Volley.newRequestQueue(context.applicationContext)
            )
        }
    }

    /**
     * Lädt die Projekte von /api/projects (oder /project, wenn du es umstellst).
     */
    fun getProjects(
        onResult: (List<ProjectDto>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val endpoint = "$baseUrl/project"

        val request = Utf8StringRequest(
            Request.Method.GET,
            endpoint,
            { response ->
                Log.d("ProjectService", "response: $response")
                try {
                    val projects = parseProjects(response)
                    onResult(projects)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                Log.e("ProjectService", "Error loading data: $error")
                onError(error)
            }
        )

        queue.add(request)
    }

    @SuppressLint("NewApi")
    private fun parseProjects(raw: String): List<ProjectDto> {
        val arr = JSONArray(raw)
        val zone = ZoneId.systemDefault()

        fun parseDateToMillis(d: String?): Long {
            if (d.isNullOrBlank()) return System.currentTimeMillis()
            val ld = LocalDate.parse(d)
            return ld.atStartOfDay(zone).toInstant().toEpochMilli()
        }

        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)

                val id = o.optLong("id")
                val name = o.optString("name")
                val street = o.optString("street")
                val city = o.optString("city")
                val zip = o.optInt("zip")
                val additionalInfo = o.optString("additionalInfo")
                val description = o.optString("description")
                val createdDate = o.optString("createdDate")

                add(
                    ProjectDto(
                        id = id,
                        name = name,
                        street = street,
                        cityZip = "${zip.takeIf { it != 0 } ?: ""} $city".trim(),
                        additionalInfo = additionalInfo,
                        description = description,
                        createdAt = parseDateToMillis(createdDate),
                        customerId = 0
                    )
                )
            }
        }
    }
}

