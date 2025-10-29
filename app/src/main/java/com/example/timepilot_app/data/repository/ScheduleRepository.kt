package com.example.timepilot_app.data.repository

import com.example.timepilot_app.data.local.ScheduleDao
import com.example.timepilot_app.data.remote.ScheduleApi

class ScheduleRepository(
    private val dao: ScheduleDao,
    private val api: ScheduleApi
) {


}
