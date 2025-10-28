package com.example.timepilot_app.data.repository

import com.example.timepilot_app.data.local.ScheduleDao
import com.example.timepilot_app.data.model.ScheduleEvent
import com.example.timepilot_app.data.remote.ScheduleApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository(
    private val dao: ScheduleDao,
    private val api: ScheduleApi
) {


}
