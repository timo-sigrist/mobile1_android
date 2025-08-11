package com.buildnote.android.api.mapper

import com.buildnote.android.api.dto.ProjectDto
import com.buildnote.android.model.Project
import kotlin.String

fun ProjectDto.toDomain(): Project =
    Project(
        id = id,
        projectName = name,
        street = street,
        cityZip = cityZip,
        additionalInfo = additionalInfo,
        description = description,
        createdAt = createdAt,
        customerId = customerId
    )