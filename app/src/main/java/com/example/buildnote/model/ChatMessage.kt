package com.example.buildnote.model

import android.net.Uri

data class ChatMessage(
    val projectName: String,
    val senderName: String,
    val text: String = "",
    val attachments: List<Uri> = emptyList(),
    val isMine: Boolean
)
