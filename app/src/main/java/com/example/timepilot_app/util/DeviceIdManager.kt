package com.example.timepilot_app.util

import android.content.Context
import java.util.*

object DeviceIdManager {
    private const val PREFS_NAME = "app_prefs"
    private const val DEVICE_ID_KEY = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(DEVICE_ID_KEY, null)

        if (deviceId == null) {
            // ✅ 第一次生成 UUID
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(DEVICE_ID_KEY, deviceId).apply()
        }

        return deviceId
    }
}
