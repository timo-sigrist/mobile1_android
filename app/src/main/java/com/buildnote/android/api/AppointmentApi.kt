package com.buildnote.android.api

import com.buildnote.android.api.dto.AppointmentDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentApi {
    @GET("/getByDateAndEmployee")
    suspend fun getAppointmentsByDateAndEmployee(
        @Query("date") date: String,
        @Query("employeeId") employeeId: Long
    ): Response<List<AppointmentDto>>
}
