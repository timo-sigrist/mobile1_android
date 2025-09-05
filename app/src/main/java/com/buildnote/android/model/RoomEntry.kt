package com.buildnote.android.model

data class RoomEntry(
    val description: String,
    val length: Double?,
    val width: Double?,
    val height: Double?,
    val includeDeduction: Boolean,
    val deductionLength: Double?,
    val deductionWidth: Double?,
    val deductionHeight: Double?
)