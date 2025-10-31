package com.example.timepilot_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.timepilot_app.navigation.AppNavHost // ✅ 导入导航文件
import com.example.timepilot_app.network.ApiClient
import com.example.timepilot_app.worker.RefreshTokenWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ 初始化 Retrofit / OkHttp 客户端
        ApiClient.init(this)

        // ✅ 启动定时刷新 token 任务（每 30 分钟执行一次）
        startTokenRefreshWorker()

        // ✅ 设置 Compose 界面
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ✅ 创建 NavController
                    val navController = rememberNavController()

                    // ✅ 使用 AppNavHost 管理导航
                    AppNavHost(navController = navController)
                }
            }
        }
    }
    private fun startTokenRefreshWorker() {
        // 每 30 分钟执行一次 RefreshTokenWorker
        val workRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(
            30, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "refresh_token_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
