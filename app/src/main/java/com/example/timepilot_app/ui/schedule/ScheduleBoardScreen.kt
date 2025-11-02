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
import com.example.timepilot_app.model.*
import com.example.timepilot_app.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F4F6),
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBarItem(
                    icon = {
                        Text("当日", color = if (currentView == "today") Color.White else Color.Gray)
                    },
                    label = { Text("当日计划") },
                    selected = currentView == "today",
                    onClick = {
                        currentView = "today"
//                        println("DEBUG: 切换到今日视图")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2196F3),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = {
                        Text("列表", color = if (currentView == "list") Color.White else Color.Gray)
                    },
                    label = { Text("事件列表") },
                    selected = currentView == "list",
                    onClick = {
                        currentView = "list"
//                        println("DEBUG: 切换到列表视图")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2196F3),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = {
                        Text("个人", color = if (currentView == "profile") Color.White else Color.Gray)
                    },
                    label = { Text("个人界面") },
                    selected = currentView == "profile",
                    onClick = {
                        currentView = "profile"
//                        println("DEBUG: 切换到个人视图")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF2196F3),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
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
                                        println("错误: $it")
                                    }
                                }
                            }
                        }
                    )
                }

                // ======= 主界面内容 =======
                Column(modifier = Modifier.fillMaxSize()) {

                    // 顶部按钮栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { showAddDialog = true }) { Text("添加事件") }
                        Button(onClick = {
                            val today = java.time.LocalDate.now()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()

                            viewModel.generateSmartDailyPlan(
                                date = today,
                                strategy = "default",
                                onComplete = { success, message, plannedEvents ->
                                    if (success) {
                                        println("✅ 智能规划成功，生成事件数：${plannedEvents?.size ?: 0}")
                                        plannedEvents?.let { viewModel.updateEvents(it) }

                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("智能规划成功！")
                                        }
                                    } else {
                                        println("❌ 智能规划失败: $message")
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message ?: "规划失败")
                                        }
                                    }
                                }
                            )
                        }) {
                            Text("规划")
                        }
                    }

                    Divider(color = Color(0xFFBDBDBD), thickness = 1.dp)

                    // ✅ 修复：重构条件逻辑，确保 ProfileScreen 能正确显示
                    Box(modifier = Modifier.weight(1f)) {
                        // 调试信息
                        Text(
                            text = "当前视图: $currentView, 事件数量: ${events.size}",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )

                        when {
                            isLoading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                            currentView == "profile" -> {
                                // ✅ 关键修复：当切换到个人界面时，直接显示 ProfileScreen，不检查事件列表
                                ProfileScreen()
                            }
                            events.isEmpty() -> {
                                Text(
                                    text = "暂无事件",
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            else -> {
                                when (currentView) {
                                    "today" -> ScrollableEventSchedule(
                                        events = events.map { it.toScheduleEvent() },
                                        onEditEvent = { updatedScheduleEvent ->
                                            val eventItem = events.find {
                                                it.toScheduleEvent().title == updatedScheduleEvent.title // 或其他唯一标识
                                            }
                                            eventItem?.let { item ->
                                                // 将编辑后的时间转换为 Instant
                                                val today = java.time.LocalDate.now()
                                                val newStartTime = today
                                                    .atTime(updatedScheduleEvent.startHour, updatedScheduleEvent.startMinute)
                                                    .atZone(java.time.ZoneId.systemDefault())
                                                    .toInstant()
                                                val newEndTime = today
                                                    .atTime(updatedScheduleEvent.endHour, updatedScheduleEvent.endMinute)
                                                    .atZone(java.time.ZoneId.systemDefault())
                                                    .toInstant()

                                                val updateRequest = when (item.type) {
                                                    "adHoc" -> AdHocEventUpdateRequest(
                                                        eventId = item.eventId!!,
                                                        title = updatedScheduleEvent.title,
                                                        quadrant = updatedScheduleEvent.quadrant,
                                                        plannedStartTime = newStartTime,  // 使用编辑后的新时间
                                                        plannedEndTime = newEndTime       // 使用编辑后的新时间
                                                    )
                                                    "habitual" -> HabitualEventUpdateRequest(
                                                        eventId = item.eventId!!,
                                                        title = updatedScheduleEvent.title,
                                                        quadrant = updatedScheduleEvent.quadrant,
                                                        startTime = newStartTime,         // 使用编辑后的新时间
                                                        endTime = newEndTime              // 使用编辑后的新时间
                                                    )
                                                    else -> null
                                                }

                                                updateRequest?.let { req ->
                                                    viewModel.editEvent(req) { success, message ->
                                                        if (success) {
                                                            println("✅ 事件编辑成功")
                                                            // 可以在这里刷新事件列表
                                                            viewModel.loadEvents()
                                                        } else {
                                                            println("❌ 编辑失败: $message")
                                                            // 显示错误提示
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("编辑失败: $message")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        onDeleteEvent = { scheduleEvent ->
                                            val eventItem = events.find { it.toScheduleEvent() == scheduleEvent }
                                            eventItem?.let { item ->
                                                val deleteRequest = when (item.type) {
                                                    "adHoc" -> AdHocEventDeleteRequest(eventId = item.eventId!!)
                                                    "habitual" -> HabitualEventDeleteRequest(eventId = item.eventId!!)
                                                    else -> null
                                                }
                                                deleteRequest?.let { req ->
                                                    viewModel.deleteEvent(req) { success, message ->
                                                        if (!success) {
                                                            println("删除失败: $message")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    "list" -> EventListTitles(events = events.map { it.toScheduleEvent() })
                                    else -> {
                                        // 默认情况
                                        Text(
                                            text = "未知视图",
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
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

@Preview(showBackground = true)
@Composable
fun ScheduleBoardPreview() {
    ScheduleBoardScreen()
}