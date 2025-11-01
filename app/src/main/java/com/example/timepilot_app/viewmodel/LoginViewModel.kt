package com.example.timepilot_app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.model.LoginRequest
import com.example.timepilot_app.model.LoginResponse
import com.example.timepilot_app.network.ApiClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var loginResponse: LoginResponse? = null
    var loginError: String? = null

    // 登录逻辑
    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch { // viewModelScope 是协程作用域
            try {
                val response = ApiClient.apiService.login(LoginRequest(username, password))
                Log.e("Network", response.message)
                if (response.code == 200) {
                    onResult(true, response.message) // ✅ 登录成功
                } else {
                    onResult(false, response.message) // ❌ 登录失败（后端返回错误）
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "网络异常，请检查连接")
                println(e.message ?: "网络异常，请检查连接")
            }
        }
    }
}
