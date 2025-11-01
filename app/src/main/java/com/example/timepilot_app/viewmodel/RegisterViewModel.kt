package com.example.timepilot_app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timepilot_app.model.RegisterRequest
import com.example.timepilot_app.network.ApiClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // 注册状态
    var registerMessage by mutableStateOf("")
        private set
    var isRegistering by mutableStateOf(false)
        private set

    // 验证码状态
    var codeMessage by mutableStateOf("")
        private set
    var isSendingCode by mutableStateOf(false)
        private set

    /**
     * 发送邮箱验证码
     */
    fun sendEmailCode(email: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (email.isBlank()) {
            codeMessage = "请输入邮箱"
            onResult(false, codeMessage)
            return
        }

        isSendingCode = true
        codeMessage = ""
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.sendEmailCode(email)
                if (response.data) {
                    codeMessage = "验证码发送成功"
                    onResult(true, codeMessage)
                } else {
                    codeMessage = response.info ?: "发送失败" // ← 这里可能为 null
                    onResult(false, codeMessage)
                }
            } catch (e: Exception) {
                codeMessage = "发送失败: ${e.message}"
                onResult(false, codeMessage)
            } finally {
                isSendingCode = false
            }
        }
    }

    /**
     * 注册
     */
    fun register(
        username: String,
        email: String,
        code: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        // 校验输入
        if (username.isBlank() || email.isBlank() || code.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            registerMessage = "请填写完整信息"
            return
        }

        if (password != confirmPassword) {
            registerMessage = "两次输入密码不一致"
            return
        }

        isRegistering = true
        registerMessage = ""

        val request = RegisterRequest(
            username = username,
            password = password,
            email = email,
            verificationCode = code
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.register(request)
                if (response.code == 200) {
                    registerMessage = "注册成功"
                    onSuccess()
                } else {
                    registerMessage = response.message ?: "注册失败" // ← 这里也可能为 null
                }
            } catch (e: Exception) {
                registerMessage = "注册失败: ${e.message}"
            } finally {
                isRegistering = false
            }
        }
    }
}
