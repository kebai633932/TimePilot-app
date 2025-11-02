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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.timepilot_app.navigation.AppNavHost
import com.example.timepilot_app.network.ApiClient
import com.example.timepilot_app.util.TokenStorage
import com.example.timepilot_app.worker.RefreshTokenWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 调试：检查 Application 状态
        println("🔧 MainActivity onCreate - Application: ${application}")

        // ✅ 调试 TokenStorage 状态
//        TokenStorage.debugTokenStatus()

        // ✅ 初始化 Retrofit / OkHttp 客户端
        ApiClient.init(this)

        // ✅ 启动定时刷新 token 任务
        startTokenRefreshWorker()

        // ✅ 设置 Compose 界面
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    private fun startTokenRefreshWorker() {
        try {
            // 立即执行一次（用于调试和立即刷新）
//            val immediateRequest = OneTimeWorkRequestBuilder<RefreshTokenWorker>()
//                .setInitialDelay(0, TimeUnit.SECONDS)
//                .addTag("immediate_refresh")
//                .build()

            // 定期执行（每90分钟）
            val periodicRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(
                90, TimeUnit.MINUTES
            ).addTag("periodic_refresh").build()

            val workManager = WorkManager.getInstance(applicationContext)

//            // 先执行立即任务
//            workManager.enqueue(immediateRequest)

            // 然后安排定期任务
            workManager.enqueueUniquePeriodicWork(
                "refresh_token_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )

            println("✅ Token 刷新 Worker 已启动")
        } catch (e: Exception) {
            println("❌ 启动 Token 刷新 Worker 失败: ${e.message}")
        }
    }
}