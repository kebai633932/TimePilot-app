package com.example.timepilot_app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.model.LoginRequest
import com.example.timepilot_app.model.LoginResponse
import com.example.timepilot_app.network.ApiClient
import com.example.timepilot_app.util.TokenStorage
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                println("🔄 开始登录请求...")
                var loginResponse = ApiClient.apiService.login(LoginRequest(username, password)) // ✅ 修复：使用 val

                println("📡 登录响应: code=${loginResponse.code}, message=${loginResponse.message}")

                if (loginResponse.code == 200) {
                    TokenStorage.saveTokens(loginResponse.access_token, loginResponse.refresh_token)
                    println("✅ Token 保存成功")

                    // ✅ 立即验证 token 是否保存成功
                    TokenStorage.debugTokenStatus()

                    onResult(true, loginResponse.message)
                } else {
                    onResult(false, loginResponse.message)
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "网络异常，请检查连接"
                println("❌ 登录异常: $errorMsg")
                onResult(false, errorMsg)
            }
        }
    }
}