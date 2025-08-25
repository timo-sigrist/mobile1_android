package com.buildnote.android.model

data class Material(
    val name: String,
    val number: Int,
    val unit: String,
    val projectId: Long,
    val netPurchasePrice: Double = 0.0,
    val calculatedSellPrice: Double = 0.0,
    val sectionId: Long = 0
)
