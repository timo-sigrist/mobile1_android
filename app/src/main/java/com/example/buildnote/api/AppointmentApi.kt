package com.example.buildnote.api

import com.example.buildnote.api.dto.AppointmentDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentApi {
    @GET("/getByDateAndEmployee")
    suspend fun getAppointmentsByDateAndEmployee(
        @Query("date") date: String,
        @Query("employeeId") employeeId: Long
    ): Response<List<AppointmentDTO>>
}
