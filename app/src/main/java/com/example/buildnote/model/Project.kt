package com.example.buildnote.model

data class Project(
    val projectName: String,
    val street: String,
    val cityZip: String,
    val additionalInfo: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val customerId: Int
)