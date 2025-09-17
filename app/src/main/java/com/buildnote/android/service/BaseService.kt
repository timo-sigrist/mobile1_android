package com.buildnote.android.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

abstract class BaseService protected constructor(
    protected val queue: RequestQueue
) {
    companion object {
        //private const val DEFAULT_URL = "https://test.buildnote.de/api"
        private const val DEFAULT_URL = "http://10.0.2.2:8443/api/"

        /**
         * Hilfsmethode für Subklassen, um eine Instanz zu erzeugen.
         * Beispiel: MyService.create(context)
         */
        fun createQueue(context: Context): RequestQueue =
            Volley.newRequestQueue(context.applicationContext)

        fun defaultBaseUrl(): String = DEFAULT_URL.trimEnd('/')


    }

    protected fun url(endpoint: String): String =
        defaultBaseUrl() + "/" + endpoint.trimStart('/')

    /**
     * Generischer POST ohne externe JSON-Libs:
     * - bodyJson: bereits als JSONObject aufgebaut (oder via Map/List -> JSONObject, siehe Helpers)
     * - parse: Lambda, um die JSON-Response in T zu mappen
     */
    protected inline fun <reified T> post(
        endpoint: String,
        bodyJson: JSONObject,
        crossinline parse: (JSONObject) -> T,
        crossinline onResult: (T) -> Unit,
        crossinline onError: (Throwable) -> Unit
    ) {
        val requestUrl = url(endpoint)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            requestUrl,
            bodyJson,
            { response ->
                try {
                    val parsed = parse(response)
                    onResult(parsed)
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                val detail = error.networkResponse?.data?.let { String(it) } ?: ""
                Log.e("BaseService", "POST $requestUrl failed: $error $detail")
                onError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf("Content-Type" to "application/json")
        }

        queue.add(request)
    }
}