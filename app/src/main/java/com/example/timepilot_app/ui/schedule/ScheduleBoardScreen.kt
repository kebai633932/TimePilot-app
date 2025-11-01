// ScheduleBoardScreen.kt
package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timepilot_app.model.AdHocEventCreateRequest
import com.example.timepilot_app.model.EventItem
import com.example.timepilot_app.model.ScheduleEvent
import com.example.timepilot_app.viewmodel.ScheduleViewModel
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun ScheduleBoardScreen(viewModel: ScheduleViewModel = remember { ScheduleViewModel() }) {

    var currentView by remember { mutableStateOf("today") }
    var showAddDialog by remember { mutableStateOf(false) }

    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar host 用于显示错误提示
    val snackbarHostState = remember { SnackbarHostState() }

    // 首次加载事件
    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    // 监听错误变化并显示 Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFF9FAFB)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // ======= 弹窗新增事件 =======
                if (showAddDialog) {
                    AddEventDialog(
                        onDismiss = { showAddDialog = false },
                        onAdd = { scheduleEvent: ScheduleEvent ->

                            // 假设使用今天的日期，如果有选择日期可以替换 LocalDate.now()
                            val today = java.time.LocalDate.now()

                            val plannedStartTime = today
                                .atTime(scheduleEvent.startHour, scheduleEvent.startMinute)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()

                            val plannedEndTime = today
                                .atTime(scheduleEvent.endHour, scheduleEvent.endMinute)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toInstant()

                            val request = AdHocEventCreateRequest(
                                title = scheduleEvent.title,
                                quadrant = scheduleEvent.quadrant,
                                plannedStartTime = plannedStartTime,
                                plannedEndTime = plannedEndTime
                            )

                            viewModel.addEvent(request) { success, errorMsg ->
                                if (success) {
                                    showAddDialog = false
                                } else {
                                    errorMsg?.let {
                                        // 可以弹 Snackbar 或日志
                                        println("错误: $it")
                                    }
                                }
                            }
                        }
                    )
                }


                // ======= 主界面内容 =======
                Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {

                    // 顶部按钮栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { showAddDialog = true }) { Text("添加事件") }
                        Button(onClick = { /* TODO: AI规划 */ }) { Text("自动规划") }
                    }

                    Divider(color = Color(0xFFBDBDBD), thickness = 1.dp)

                    // 主体内容
                    Box(modifier = Modifier.weight(1f)) {
                        when {
                            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            events.isEmpty() -> Text(
                                text = "暂无事件",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            else -> when (currentView) {
                                "today" -> ScrollableEventSchedule(events = events.map { it.toScheduleEvent() })
                                "list" -> EventListTitles(events = events.map { it.toScheduleEvent() })
                                "profile" -> ProfileScreen()
                            }
                        }
                    }
                }

                // ======= 底部导航栏 =======
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    Divider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NavButton("today", currentView) { currentView = "today" }
                        NavButton("list", currentView) { currentView = "list" }
                        NavButton("profile", currentView) { currentView = "profile" }
                    }
                }
            }
        }
    }
}
// ===================== 扩展函数：将 EventItem 转换为 ScheduleEvent =====================
fun EventItem.toScheduleEvent(): ScheduleEvent {
    val startZdt: ZonedDateTime = this.startTime.atZone(ZoneId.systemDefault())
    val endZdt: ZonedDateTime = this.endTime.atZone(ZoneId.systemDefault())
    return ScheduleEvent(
        title = this.title,
        startHour = startZdt.hour,
        startMinute = startZdt.minute,
        endHour = endZdt.hour,
        endMinute = endZdt.minute,
        quadrant = this.quadrant,
        type = this.type
    )
}

@Composable
private fun NavButton(label: String, currentView: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentView == label) Color(0xFF2196F3) else Color.Gray
        )
    ) {
        val text = when (label) {
            "today" -> "当日计划"
            "list" -> "事件列表"
            "profile" -> "个人界面"
            else -> label
        }
        Text(text, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleBoardPreview() {
    ScheduleBoardScreen()
}
