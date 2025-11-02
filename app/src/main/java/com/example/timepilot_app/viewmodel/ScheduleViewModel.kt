package com.example.timepilot_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.model.*
import com.example.timepilot_app.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class ScheduleViewModel : ViewModel() {

    // ========== 事件状态 ==========
    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val events: StateFlow<List<EventItem>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // ========== 弹窗状态 ==========
    private val _selectedEvent = MutableStateFlow<EventItem?>(null)
    val selectedEvent: StateFlow<EventItem?> = _selectedEvent

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible

    // ========================================================
    // 加载事件
    // ========================================================
    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val adHocResponse = ApiClient.apiService.listAdHocEvents()
                val habitualResponse = ApiClient.apiService.listHabitualEvents()

                if (adHocResponse.code == "200" && habitualResponse.code == "200") {
                    val adHocEvents = adHocResponse.data?.map { vo ->
                        EventItem(
                            eventId = vo.eventId,
                            title = vo.title,
                            quadrant = vo.quadrant,
                            startTime = vo.plannedStartTime,
                            endTime = vo.plannedEndTime,
                            type = "adHoc"
                        )
                    } ?: emptyList()

                    val habitualEvents = habitualResponse.data?.map { vo ->
                        EventItem(
                            eventId = vo.eventId,
                            title = vo.title,
                            quadrant = vo.quadrant,
                            startTime = vo.startTime,
                            endTime = vo.endTime,
                            type = "habitual"
                        )
                    } ?: emptyList()

                    _events.value = adHocEvents + habitualEvents
                } else {
                    _errorMessage.value =
                        "获取事件失败: ${adHocResponse.message} / ${habitualResponse.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络异常: ${e.message}"
                println("网络异常:"+e.message)
            }
            _isLoading.value = false
        }
    }

    // ========================================================
    // 新增事件
    // ========================================================
    fun addEvent(
        request: EventCreateRequest,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                when (request.type) {
                    "adHoc" -> {
                        val req = request as AdHocEventCreateRequest
                        val response = ApiClient.apiService.createAdHocEvent(req)
                        if (response.code == "200") {
                            val newId = response.data!!
                            _events.value += EventItem(
                                eventId = newId,
                                title = req.title,
                                quadrant = req.quadrant,
                                startTime = req.plannedStartTime,
                                endTime = req.plannedEndTime,
                                type = "adHoc"
                            )
                            onComplete(true, null)
                        } else onComplete(false, response.message)
                    }

                    "habitual" -> {
                        val req = request as HabitualEventCreateRequest
                        val response = ApiClient.apiService.createHabitualEvent(req)
                        if (response.code == "200") {
                            val newId = response.data!!
                            _events.value += EventItem(
                                eventId = newId,
                                title = req.title,
                                quadrant = req.quadrant,
                                startTime = req.startTime,
                                endTime = req.endTime,
                                type = "habitual"
                            )
                            onComplete(true, null)
                        } else onComplete(false, response.message)
                    }
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "网络异常")
            }
        }
    }

    // ========================================================
    // 编辑事件
    // ========================================================
    fun editEvent(
        request: EventUpdateRequest,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                when (request.type) {
                    "adHoc" -> {
                        val req = request as AdHocEventUpdateRequest
                        val response = ApiClient.apiService.updateAdHocEvent(req)
                        if (response.code == "200") {
                            _events.value = _events.value.map {
                                if (it.eventId == req.eventId && it.type == "adHoc") {
                                    it.copy(
                                        title = req.title,
                                        quadrant = req.quadrant,
                                        startTime = req.plannedStartTime,
                                        endTime = req.plannedEndTime
                                    )
                                } else it
                            }
                            onComplete(true, null)
                        } else onComplete(false, response.message)
                    }

                    "habitual" -> {
                        val req = request as HabitualEventUpdateRequest
                        val response = ApiClient.apiService.updateHabitualEvent(req)
                        if (response.code == "200") {
                            _events.value = _events.value.map {
                                if (it.eventId == req.eventId && it.type == "habitual") {
                                    it.copy(
                                        title = req.title,
                                        quadrant = req.quadrant,
                                        startTime = req.startTime,
                                        endTime = req.endTime
                                    )
                                } else it
                            }
                            onComplete(true, null)
                        } else onComplete(false, response.message)
                    }
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "网络异常")
                println(e.message)
            }
        }
    }

    // ========================================================
    // 删除事件
    // ========================================================
    fun deleteEvent(
        request: EventDeleteRequest,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = when (request) {
                    is AdHocEventDeleteRequest -> ApiClient.apiService.deleteAdHocEvent(request)
                    is HabitualEventDeleteRequest -> ApiClient.apiService.deleteHabitualEvent(request)
                    else -> null
                }

                if (response == null) {
                    onComplete(false, "未知事件类型")
                    return@launch
                }

                if (response.code == "200" && response.data == true) {
                    // 删除本地缓存的事件
                    _events.value = _events.value.filterNot {
                        it.eventId == request.eventId && it.type == request.type
                    }
                    onComplete(true, null)
                } else {
                    onComplete(false, response.message ?: "删除失败")
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "网络异常")
            }
        }
    }
    // ========================================================
    // 智能规划：生成当日时间安排
    // ========================================================
    fun generateSmartDailyPlan(
        date: Instant,
        strategy: String? = null,
        onComplete: (Boolean, String?, List<PlannedEventVO>?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = SmartDailyPlanGenerateRequest(date)
                val response = ApiClient.apiService.generateSmartDailyPlan(request)

                if (response.code == "200" && response.data != null) {
                    // 返回规划结果
                    onComplete(true, null, response.data)
                } else {
                    onComplete(false, response.message ?: "生成失败", null)
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "网络异常", null)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateEvents(plannedEvents: List<PlannedEventVO>) {
        _events.value = plannedEvents.map {
            EventItem(
                eventId = it.eventId,
                title = it.title,
                startTime = it.startTime,
                endTime = it.endTime,
                quadrant = 4, // 如果后端没有返回象限，你可以自己补充
                type = it.type
            )
        }
    }
    // ========================================================
    // 弹窗控制
    // ========================================================
    fun onEventClick(event: EventItem) {
        _selectedEvent.value = event
        _isDialogVisible.value = true
    }

    fun closeDialog() {
        _isDialogVisible.value = false
        _selectedEvent.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
