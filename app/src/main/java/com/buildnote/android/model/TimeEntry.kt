package com.buildnote.android.model


data class TimeEntry(
    val projectName: String,
    val action: ActionType,
    val start: Long,
    val end: Long
)