package com.example.timepilot_app.model

import java.time.Instant

// 🧩 通用封装
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)
// 🗓️ 统一事件抽象（通用视图层或后端聚合返回使用）
data class EventItem(
    val eventId: Long?,
    val title: String,
    val quadrant: Int,      // 四象限分类
    val startTime: Instant, // 开始时间（UTC）
    val endTime: Instant,   // 结束时间（UTC）
    val type: String        // "habitual" 或 "adHoc"
)
// 🕒 客户端展示模型 (ScheduleEvent)
data class ScheduleEvent(
    val title: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val type: String,    // "daily" or "emergency"
    val quadrant: Int    // 1=重要紧急, 2=重要不紧急, 3=紧急不重要, 4=不重要不紧急
)
interface EventCreateRequest {
    val type: String
}

data class AdHocEventCreateRequest(
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant,
) : EventCreateRequest {
    override val type: String = "adHoc"
}

data class HabitualEventCreateRequest(
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant,
) : EventCreateRequest {
    override val type: String = "habitual"
}
interface EventUpdateRequest {
    val type: String
}
data class AdHocEventUpdateRequest(
    val eventId: Long,
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant
) : EventUpdateRequest {
    override val type: String = "adHoc"
}
data class HabitualEventUpdateRequest(
    val eventId: Long,
    val title: String,
    val description: String? = null,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant
) : EventUpdateRequest {
    override val type: String = "habitual"
}
// 统一删除请求接口
interface EventDeleteRequest {
    val type: String
    val eventId: Long   // ← 提升到接口
}

// 日常事件删除请求
data class HabitualEventDeleteRequest(
    override val eventId: Long
) : EventDeleteRequest {
    override val type: String = "habitual"
}

// 突发事件删除请求
data class AdHocEventDeleteRequest(
    override val eventId: Long
) : EventDeleteRequest {
    override val type: String = "adHoc"
}

data class HabitualEventVO(
    val eventId: Long,
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant,
    val description: String? = null,
    val type: String
)

data class AdHocEventVO(
    val eventId: Long,
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant,
    val type: String
)