package com.example.timepilot_app.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.timepilot_app.model.RefreshTokenRequest
import com.example.timepilot_app.network.ApiClient
import com.example.timepilot_app.util.TokenStorage
//TODO 关键问题：RefreshTokenWorker（刷新令牌的工作器）执行失败
class RefreshTokenWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val refreshToken = TokenStorage.getRefreshToken() ?: return Result.failure()
        return try {
            // 调用 refresh token API
            val response = ApiClient.apiService.refreshToken(
                RefreshTokenRequest(refreshToken)
            )

            // 保存新的 token
            TokenStorage.saveTokens(response.access_token, response.refresh_token)
            Log.d("TokenWorker", "Token refreshed successfully")

            Result.success()
        } catch (e: Exception) {
            Log.e("TokenWorker", "Token refresh failed: ${e.message}")
            Result.retry()
        }
    }
}


