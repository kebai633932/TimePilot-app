// ScheduleViewModel.kt
package com.example.timepilot_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.model.AdHocEventCreateRequest
import com.example.timepilot_app.model.AdHocEventVO
import com.example.timepilot_app.model.EventCreateRequest
import com.example.timepilot_app.model.EventItem
import com.example.timepilot_app.model.HabitualEventCreateRequest
import com.example.timepilot_app.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val events: StateFlow<List<EventItem>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** 加载全部事件（含日常 + 突发） */
    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 并行请求两种类型的事件
                val adHocResponse = ApiClient.apiService.listAdHocEvents()
                val habitualResponse = ApiClient.apiService.listHabitualEvents()

                if (adHocResponse.code == 200 && habitualResponse.code == 200) {
                    val adHocEvents = adHocResponse.data?.map { vo ->
                        EventItem(
                            eventId = vo.eventId,
                            title = vo.title,
                            quadrant = vo.quadrant,
                            startTime = vo.plannedStartTime,
                            endTime = vo.plannedEndTime,
                            type = vo.type
                        )
                    } ?: emptyList()

                    val habitualEvents = habitualResponse.data?.map { vo ->
                        EventItem(
                            eventId = vo.eventId,
                            title = vo.title,
                            quadrant = vo.quadrant,
                            startTime = vo.plannedStartTime,
                            endTime = vo.plannedEndTime,
                            type = vo.type
                        )
                    } ?: emptyList()

                    // 合并两类事件
                    _events.value = adHocEvents + habitualEvents
                } else {
                    _errorMessage.value =
                        "获取事件失败: ${adHocResponse.message} / ${habitualResponse.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络异常: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    /** 新增事件，区分类型 */
    fun addEvent(
        request: EventCreateRequest, // 可以传 AdHocEventCreateRequest 或 HabitualEventCreateRequest
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                when (request.type) {
                    "adHoc" -> {
                        val req = request as AdHocEventCreateRequest
                        val response = ApiClient.apiService.createAdHocEvent(req)
                        if (response.code == 200) {
                            val newId = response.data!!
                            _events.value = _events.value + EventItem(
                                eventId = newId,
                                title = req.title,
                                quadrant = req.quadrant,
                                startTime = req.plannedStartTime,
                                endTime = req.plannedEndTime,
                                type = "adHoc"
                            )
                            onComplete(true, null)
                        } else {
                            onComplete(false, response.message)
                        }
                    }
                    "habitual" -> {
                        val req = request as HabitualEventCreateRequest
                        val response = ApiClient.apiService.createHabitualEvent(req)
                        if (response.code == 200) {
                            val newId = response.data!!
                            _events.value = _events.value + EventItem(
                                eventId = newId,
                                title = req.title,
                                quadrant = req.quadrant,
                                startTime = req.plannedStartTime,
                                endTime = req.plannedEndTime,
                                type = "habitual"
                            )
                            onComplete(true, null)
                        } else {
                            onComplete(false, response.message)
                        }
                    }
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "网络异常")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
