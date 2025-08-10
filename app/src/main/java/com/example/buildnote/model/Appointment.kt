package com.example.buildnote.model

data class Appointment(
    val date: String,
    val start: String,
    val end: String,
    val projectName: String,
    val notes: String = ""
)