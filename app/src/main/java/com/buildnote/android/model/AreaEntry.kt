package com.buildnote.android.model

data class AreaEntry(
    val flaechenbezeichnung: String,
    val laenge: Double?,
    val breite: Double?,
    val includeAbzug: Boolean,
    val abzugLaenge: Double?,
    val abzugBreite: Double?
)