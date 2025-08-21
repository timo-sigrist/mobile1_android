package com.buildnote.android.service

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest

class Utf8StringRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : StringRequest(method, url, listener, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        return try {
            val parsed = String(response.data, charset("UTF-8")) // erzwinge UTF-8
            Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }
}