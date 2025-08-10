package com.example.buildnote.api.dto

data class AppointmentDTO(
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