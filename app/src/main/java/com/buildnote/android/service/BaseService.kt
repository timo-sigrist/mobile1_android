package com.buildnote.android.service

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Json

abstract class BaseService protected constructor(
    protected val queue: RequestQueue
) {
    companion object {
        private const val DEFAULT_URL = "https://test.buildnote.de/api"

        /**
         * Hilfsmethode für Subklassen, um eine Instanz zu erzeugen.
         * Beispiel: MyService.create(context)
         */
        fun createQueue(context: Context): RequestQueue =
            Volley.newRequestQueue(context.applicationContext)

        fun defaultBaseUrl(): String = DEFAULT_URL.trimEnd('/')


    }
}