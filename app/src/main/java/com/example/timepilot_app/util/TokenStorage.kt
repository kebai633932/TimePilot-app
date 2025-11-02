package com.example.timepilot_app.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.timepilot_app.MyApplication
import androidx.core.content.edit

object TokenStorage {
    private const val PREF_NAME = "secure_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    // ✅ 使用安全的 Context 获取方式
    private fun getSafeContext(): Context {
        return try {
            MyApplication.instance.appContext
        } catch (e: IllegalStateException) {
            throw IllegalStateException("MyApplication 未初始化，请确保在 Application.onCreate 中初始化", e)
        }
    }

    private val sharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKeyAlias,
                getSafeContext(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ).also {
                println("✅ EncryptedSharedPreferences 创建成功")
            }
        } catch (e: Exception) {
            println("❌ EncryptedSharedPreferences 创建失败: ${e.message}")
            // 降级到普通 SharedPreferences
            getSafeContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).also {
                println("⚠️ 使用普通 SharedPreferences 作为备选")
            }
        }
    }

    fun saveTokens(access: String, refresh: String) {
        try {
            sharedPreferences.edit {
                putString(KEY_ACCESS_TOKEN, access)
                    .putString(KEY_REFRESH_TOKEN, refresh)
            }
            println("✅ Token 保存成功: access=${access.take(10)}..., refresh=${refresh.take(10)}...")
        } catch (e: Exception) {
            println("❌ Token 保存失败: ${e.message}")
        }
    }

    fun getAccessToken(): String? {
        return try {
            val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
            println("🔍 读取 Access Token: ${if (token != null) "存在 (${token.length} 字符)" else "不存在"}")
            token
        } catch (e: Exception) {
            println("❌ 读取 Access Token 失败: ${e.message}")
            null
        }
    }

    fun getRefreshToken(): String? {
        return try {
            val token = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
            println("🔍 读取 Refresh Token: ${if (token != null) "存在 (${token.length} 字符)" else "不存在"}")
            token
        } catch (e: Exception) {
            println("❌ 读取 Refresh Token 失败: ${e.message}")
            null
        }
    }

    fun clearTokens() {
        try {
            sharedPreferences.edit { clear() }
            println("🗑️ Token 已清除")
        } catch (e: Exception) {
            println("❌ 清除 Token 失败: ${e.message}")
        }
    }

    // ✅ 添加安全的调试方法
    fun debugTokenStatus() {
        println("=== TokenStorage 调试信息 ===")
        try {
            println("🔧 MyApplication 初始化: ${MyApplication::class.java.simpleName} 已加载")
            println("🔐 Access Token: ${if (getAccessToken() != null) "存在" else "不存在"}")
            println("🔄 Refresh Token: ${if (getRefreshToken() != null) "存在" else "不存在"}")
        } catch (e: IllegalStateException) {
            println("❌ MyApplication 未初始化: ${e.message}")
        } catch (e: Exception) {
            println("❌ 调试过程出错: ${e.message}")
        }
        println("============================")
    }
}