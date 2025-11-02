package com.example.timepilot_app.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

// 🧩 通用封装 - 修复字段名匹配问题
data class BaseResponse<T>(
    @SerializedName("code") val code: String,  // ✅ 改为 String 并添加序列化名称
    @SerializedName("info") val message: String,  // ✅ 使用 @SerializedName 匹配 "info" 字段
    @SerializedName("data") val data: T?
) {
    // ✅ 添加便利方法检查是否成功
    val isSuccess: Boolean
        get() = code == "200"
}

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
    val startTime: Instant,  // ✅ 修改为与Java DTO一致的startTime
    val endTime: Instant,    // ✅ 修改为与Java DTO一致的endTime
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
    val description: String? = null,  // ✅ 添加description字段以匹配Java DTO
    val quadrant: Int,
    val startTime: Instant,  // ✅ 修改为与Java DTO一致的startTime
    val endTime: Instant     // ✅ 修改为与Java DTO一致的endTime
) : EventUpdateRequest {
    override val type: String = "habitual"
}

// 统一删除请求接口
interface EventDeleteRequest {
    val type: String
    val eventId: Long
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
    val startTime: Instant,  // ✅ 修改为与Java DTO一致的startTime
    val endTime: Instant    // ✅ 修改为与Java DTO一致的endTime
)

data class AdHocEventVO(
    val eventId: Long,
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant
)

// AdHoc事件创建请求（带验证注解）
data class ValidatedAdHocEventCreateRequest(
    val title: String,
    val quadrant: Int,
    val plannedStartTime: Instant,
    val plannedEndTime: Instant
) : EventCreateRequest {
    override val type: String = "adHoc"
}

// Habitual事件创建请求（带验证注解）
data class ValidatedHabitualEventCreateRequest(
    val title: String,
    val quadrant: Int,
    val startTime: Instant,
    val endTime: Instant
) : EventCreateRequest {
    override val type: String = "habitual"
}

// 智能规划请求体
data class SmartDailyPlanGenerateRequest(
    val date: Instant
)

// 智能规划返回的事件对象
data class PlannedEventVO(
    val eventId: Long?,
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val type: String
)

// 智能规划请求（带验证注解）
data class ValidatedSmartDailyPlanGenerateRequest(
    val date: Instant
)