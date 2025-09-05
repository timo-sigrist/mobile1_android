package com.buildnote.android.model

data class LengthEntry(
    val description: String,
    val length: Double?,
    val includeDeduction: Boolean,
    val deductionLength: Double?
)