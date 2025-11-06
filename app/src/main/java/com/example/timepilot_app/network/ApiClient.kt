package com.example.timepilot_app.network

import android.content.Context
import com.example.timepilot_app.util.DeviceIdManager
import com.example.timepilot_app.util.TokenStorage
import com.google.gson.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://117.72.94.236:8090/api"

    // Application 初始化时注入上下文
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // ✅ 自定义 Gson —— 支持多种时间格式
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, JsonDeserializer { json, _, _ ->
            val str = json.asString
            try {
                // 优先解析标准 ISO 格式
                Instant.parse(str)
            } catch (_: Exception) {
                try {
                    // 尝试解析常见的 yyyy-MM-dd HH:mm:ss 格式（无时区）
                    LocalDateTime.parse(
                        str,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    ).toInstant(ZoneOffset.UTC)
                } catch (e: Exception) {
                    println("⚠️ 时间解析失败: $str -> ${e.message}")
                    null
                }
            }
        })
        .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ ->
            JsonPrimitive(src.toString()) // 输出为 ISO 字符串
        })
        .setLenient()
        .create()

    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                println("🔗 发起请求: ${originalRequest.method} ${originalRequest.url}")

                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("X-Device-Id", DeviceIdManager.getDeviceId(appContext))

                // ✅ 加上 Authorization 头
                TokenStorage.getAccessToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    println("🔐 添加认证头: Bearer $token")
                } ?: run {
                    println("⚠️ 未找到认证token")
                }

                val request = requestBuilder.build()
                try {
                    val response = chain.proceed(request)
                    println("📡 响应状态: ${response.code} - ${response.message}")
                    response
                } catch (e: Exception) {
                    println("❌ 网络请求异常: ${e.message}")
                    throw e
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ✅ 使用自定义 Gson 构造 Retrofit
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}
