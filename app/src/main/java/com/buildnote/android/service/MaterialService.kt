package com.buildnote.android.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.buildnote.android.model.Material
import com.buildnote.android.service.JsonBuildnoteParser.parseMaterials


class MaterialService private constructor(
    queue: RequestQueue
) : BaseService(queue) {
    companion object {
        private val endpoint = "${defaultBaseUrl()}/material"

        fun create(context: Context): MaterialService {
            return MaterialService(
                queue = createQueue(context)
            )
        }
    }

    fun getMaterialForProject(
        projectId: Long,
        onResult: (List<Material>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val requestUrl = "$endpoint/project/${projectId}"
        val request = Utf8StringRequest(
            Request.Method.GET,
            requestUrl,
            { response ->
                Log.d("ProjectService", "response: $response")
                try {
                    onResult(parseMaterials(response))
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

    fun postMaterial(
        material: Material,
        onResult: (Material) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val body = Json.obj(
            "name" to material.name,
            "number" to material.number,
            "unit" to material.unit,
            "projectId" to material.projectId
        )

        post(
            endpoint = "material",
            bodyJson = body,
            parse = { json ->
                Material(
                    name = json.optString("name"),
                    number = json.optInt("number"),
                    unit = json.optString("unit"),
                    projectId = json.optLong("projectId")
                )
            },
            onResult = onResult,
            onError = onError
        )
    }


    /*
    fun postMaterial(
        material: Material,
        onResult: (Material) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val requestUrl = endpoint // POST direkt an /material

        val body = JSONObject().apply {
            put("name", material.name)
            put("number", material.number)
            put("unit", material.unit)
            put("projectId", material.projectId)
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            requestUrl,
            body,
            { response ->
                try {
                    // Response zurück in Material mappen
                    val saved = Material(
                        name = response.optString("name"),
                        number = response.optInt("number"),
                        unit = response.optString("unit"),
                        projectId = response.optLong("projectId")
                    )
                    onResult(saved)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                Log.e("MaterialService", "Error posting material: $error")
                onError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf(
                    "Content-Type" to "application/json"
                    // hier ggf. noch Auth-Header etc.
                )
            }
        }

        queue.add(request)
    }*/


}

