package com.example.buildnote.model

import android.net.Uri

data class DocumentEntry(
    val projectName: String,
    val name: String,
    val uri: Uri
)