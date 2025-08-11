package com.buildnote.android.api.repository

import com.buildnote.android.api.ProjectApi
import com.buildnote.android.api.mapper.toDomain
import com.buildnote.android.model.Project
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val api: ProjectApi
) {
    suspend fun getProjects(): List<Project> =
        api.getProjects().map { it.toDomain() }
}