package com.example.timepilot_app.network

// ApiService.kt
import com.example.timepilot_app.model.*
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
//    ==================== 用户认证模块 ====================
    @POST("/api/user/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("/api/user/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
    @POST("/api/user/auth/sendEmailCode")
    @FormUrlEncoded
    suspend fun sendEmailCode(
        @Field("email") email: String
    ): EmailCodeResponse

    //Refresh Token 接口
    @POST("/api/user/auth/refreshToken")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

    // ==================== 日常事件模块 (HabitualEventController) ====================
    /** 创建日常事件 */
    @POST("/api/habitual-event/create")
    suspend fun createHabitualEvent(@Body request: HabitualEventCreateRequest): BaseResponse<Long>

    /** 更新日常事件 */
    @POST("/api/habitual-event/update")
    suspend fun updateHabitualEvent(@Body request: HabitualEventUpdateRequest): BaseResponse<Boolean>

    /** 删除日常事件 */
    @POST("/api/habitual-event/delete")
    suspend fun deleteHabitualEvent(@Body request: HabitualEventDeleteRequest): BaseResponse<Boolean>

    /** 获取日常事件列表 */
    @POST("/api/habitual-event/list")
    suspend fun listHabitualEvents(): BaseResponse<List<HabitualEventVO>>

    /** 获取突发事件列表 */
    @POST("/api/ad-hoc-event/list")
    suspend fun listAdHocEvents(): BaseResponse<List<AdHocEventVO>>

    /** 创建突发事件 */
    @POST("/api/ad-hoc-event/create")
    suspend fun createAdHocEvent(@Body request: AdHocEventCreateRequest): BaseResponse<Long>

    /** 更新突发事件 */
    @POST("/api/ad-hoc-event/update")
    suspend fun updateAdHocEvent(@Body request: AdHocEventUpdateRequest): BaseResponse<Boolean>

    /** 删除突发事件 */
    @POST("/api/ad-hoc-event/delete")
    suspend fun deleteAdHocEvent(@Body request: AdHocEventDeleteRequest): BaseResponse<Boolean>


    // ==================== 智能规划模块 (TimePlanController) ====================

    /** 智能生成每日计划 */
    @POST("/api/time-plan/smart-daily-plan")
    suspend fun generateSmartDailyPlan(
        @Body request: SmartDailyPlanGenerateRequest
    ): BaseResponse<List<PlannedEventVO>>

}