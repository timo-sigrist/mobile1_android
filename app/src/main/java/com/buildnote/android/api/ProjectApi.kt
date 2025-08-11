package com.buildnote.android.api

import com.buildnote.android.api.dto.ProjectDto
import retrofit2.http.GET

interface ProjectApi {
    @GET("project")
    suspend fun getProjects(): List<ProjectDto>
}