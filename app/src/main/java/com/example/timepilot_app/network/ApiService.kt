package com.example.timepilot_app.network

import com.example.timepilot_app.model.*
import retrofit2.http.*

interface ApiService {

    // =========================
    // 用户认证模块
    // =========================

    /** 登录 */
    @POST("/user/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /** 注册 */
    @POST("/user/auth/register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse<String>
    /** 发送邮箱验证码（改为 POST + form-data） */
    @FormUrlEncoded
    @POST("/email/sendEmailCode")
    suspend fun sendEmailCode(
        @Field("email") email: String
    ): BaseResponse<Boolean>

    // =========================
    // 临时事件（AdHoc Event）模块
    // =========================

    /** 创建临时事件 */
    @POST("/event/ad-hoc-event/create")
    suspend fun createAdHocEvent(@Body request: AdHocEventCreateRequest): BaseResponse<Long>

    /** 更新临时事件 */
    @POST("/event/ad-hoc-event/update")
    suspend fun updateAdHocEvent(@Body request: AdHocEventUpdateRequest): BaseResponse<Boolean>

    /** 删除临时事件 */
    @POST("/event/ad-hoc-event/delete")
    suspend fun deleteAdHocEvent(@Body request: AdHocEventDeleteRequest): BaseResponse<Boolean>

    /** 获取临时事件列表 */
    @POST("/event/ad-hoc-event/list")
    suspend fun listAdHocEvents(): BaseResponse<List<AdHocEventVO>>


    // =========================
    // 日常事件（Habitual Event）模块
    // =========================

    /** 创建日常事件 */
    @POST("/event//habitual-event/create")
    suspend fun createHabitualEvent(@Body request: HabitualEventCreateRequest): BaseResponse<Long>

    /** 更新日常事件 */
    @POST("/event/habitual-event/update")
    suspend fun updateHabitualEvent(@Body request: HabitualEventUpdateRequest): BaseResponse<Boolean>

    /** 删除日常事件 */
    @POST("/event/habitual-event/delete")
    suspend fun deleteHabitualEvent(@Body request: HabitualEventDeleteRequest): BaseResponse<Boolean>

    /** 获取日常事件列表 */
    @GET("/event/habitual-event/list")
    suspend fun listHabitualEvents(): BaseResponse<List<HabitualEventVO>>


    // =========================
    // 智能规划模块
    // =========================

    /** 智能生成每日计划 */
    @POST("/smartPlan/generateDaily")
    suspend fun generateSmartDailyPlan(@Body request: SmartDailyPlanGenerateRequest): BaseResponse<List<PlannedEventVO>>
}
