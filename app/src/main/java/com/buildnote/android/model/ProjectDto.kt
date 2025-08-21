package com.buildnote.android.model

data class ProjectDto(
    val id: Long,
    val name: String,
    val street: String,
    val cityZip: String,
    val additionalInfo: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val customerId: Int = 0
)