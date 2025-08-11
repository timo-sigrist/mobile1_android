package com.buildnote.android.api.dto

data class AppointmentDto(
    val id: Long,
    val name: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val notes: String?,
    val color: String?,
    val projectId: Long,
    val employeeIds: List<Long>
)