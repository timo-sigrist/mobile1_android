package com.buildnote.android.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.buildnote.android.model.Project
import com.buildnote.android.service.JsonBuildnoteParser.parseProjects



class ProjectService private constructor(
    queue: RequestQueue,
): BaseService(queue) {

    companion object {
        private val endpoint = "${defaultBaseUrl()}/project"
        fun create(context: Context): ProjectService {
            return ProjectService(
                queue = createQueue(context)
            )
        }
    }

    fun getProjects(
        onResult: (List<Project>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
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
}

