package com.buildnote.android.model

data class LengthEntry(
    val laengenbezeichnung: String,
    val laenge: Double?,
    val includeAbzug: Boolean,
    val abzug: Double?
)