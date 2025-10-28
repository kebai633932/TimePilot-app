package com.example.timepilot_app.data.remote

import com.example.timepilot_app.data.model.ScheduleEvent
import retrofit2.http.*

interface ScheduleApi {

    @GET("events")
    suspend fun getEvents(): List<ScheduleEvent>

    @POST("events")
    suspend fun addEvent(@Body event: ScheduleEvent): ScheduleEvent

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Body event: ScheduleEvent
    ): ScheduleEvent

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int)
}
