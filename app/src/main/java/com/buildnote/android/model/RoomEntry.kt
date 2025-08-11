package com.buildnote.android.model

data class RoomEntry(
    val raumbezeichnung: String,
    val laenge: Double?,
    val breite: Double?,
    val hoehe: Double?,
    val includeAbzug: Boolean,
    val abzugLaenge: Double?,
    val abzugBreite: Double?,
    val abzugHoehe: Double?
)