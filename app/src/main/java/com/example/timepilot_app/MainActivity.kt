package com.example.timepilot_app

import MainCalendarScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 这里调用你之前写的 Compose 主界面
                    MainCalendarScreen(
                        onMenuItemClick = { menuItem ->
                            // 处理菜单点击
                            when (menuItem) {
                                "search" -> { /* 搜索逻辑 */ }
                                "more" -> { /* 更多选项逻辑 */ }
                                "more" -> { /* 更多选项逻辑 */ }
                            }
                        },
                        onNavigationItemClick = { navItem ->
                            // 处理导航点击
                            when (navItem) {
                                "schedule" -> { /* 我的日程 */ }
                                "events" -> { /* 活动管理 */ }
                                "settings" -> { /* 设置 */ }
                                "help" -> { /* 帮助与反馈 */ }
                                "help" -> { /* 帮助与反馈 */ }
                            }
                        }
                    )
                }
            }
        }
    }
}