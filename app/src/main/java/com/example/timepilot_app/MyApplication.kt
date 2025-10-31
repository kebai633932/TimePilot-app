package com.example.timepilot_app

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        // 提供全局 Context
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化全局 Context
        context = applicationContext
    }
}
