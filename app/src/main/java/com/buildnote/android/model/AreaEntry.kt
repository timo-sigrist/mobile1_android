package com.buildnote.android.model

data class AreaEntry(
    val description: String,
    val length: Double?,
    val width: Double?,
    val includeDeduction: Boolean,
    val deductionLength: Double?,
    val deductionWidth: Double?
)