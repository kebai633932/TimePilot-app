package com.example.timepilot_app.network

// ApiClient.kt
import android.content.Context
import com.example.timepilot_app.util.DeviceIdManager
import com.example.timepilot_app.util.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8090/"

    // 需要在 Application 初始化时先注入
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("X-Device-Id", DeviceIdManager.getDeviceId(appContext))

                // ✅ 如果 access token 存在，则添加 Authorization
                TokenStorage.getAccessToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }
}
