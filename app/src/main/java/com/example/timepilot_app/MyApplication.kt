package com.example.timepilot_app

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        // ✅ 使用单例模式，但不使用 lateinit var
        private var _instance: MyApplication? = null
        val instance: MyApplication
            get() = _instance ?: throw IllegalStateException("MyApplication 未初始化")
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        println("✅ MyApplication 初始化完成")
    }

    // ✅ 提供 applicationContext
    val appContext: Context
        get() = applicationContext
}