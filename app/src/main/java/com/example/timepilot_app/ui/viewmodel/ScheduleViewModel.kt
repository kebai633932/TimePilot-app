package com.example.timepilot_app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.data.model.ScheduleEvent
import com.example.timepilot_app.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    var events by mutableStateOf<List<ScheduleEvent>>(emptyList())
        private set

    fun loadEvents() {

    }

    fun addEvent(event: ScheduleEvent) {

    }
}
