package com.example.timepilot_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ScheduleEvent(
    val title: String,
    val startHour: Int,
    val endHour: Int,
    val type: String // "daily" or "emergency"
)

@Composable
fun ScheduleBoardScreen() {
    var currentView by remember { mutableStateOf("today") }
    var showAddDialog by remember { mutableStateOf(false) }

    val events = remember { mutableStateListOf(
        ScheduleEvent("晨练", 6, 7, "daily"),
        ScheduleEvent("上班", 9, 12, "daily"),
        ScheduleEvent("午餐", 12, 13, "daily"),
        ScheduleEvent("学习 Kotlin", 14, 16, "daily"),
        ScheduleEvent("突发会议", 10, 11, "emergency"),
        ScheduleEvent("系统崩溃修复", 10, 12, "emergency"),
        ScheduleEvent("服务器异常排查", 10, 12, "emergency"),
        ScheduleEvent("晚餐", 18, 19, "daily")
    )}

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newEvent ->
                events.add(newEvent)
                showAddDialog = false
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9FAFB)) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 中间区域
            Column(modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
                // 顶部操作栏
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

                // 中间主体区域占剩余空间
                Box(modifier = Modifier.weight(1f)) {
                    when (currentView) {
                        "today" -> ScrollableEventSchedule(events = events)
                        "daily" -> EventListTitles(events = events)
                    }
                }
            }

            // 底部固定切换栏
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
                    Button(
                        onClick = { currentView = "today" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == "today") Color(0xFF2196F3) else Color.Gray
                        )
                    ) {
                        Text("当日计划", color = Color.White)
                    }
                    Button(
                        onClick = { currentView = "daily" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == "daily") Color(0xFF2196F3) else Color.Gray
                        )
                    ) {
                        Text("事件查看", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableEventSchedule(events: List<ScheduleEvent>) {
    // 找出最早和最晚的小时
    val minHour = events.minOfOrNull { it.startHour } ?: 0
    val maxHour = events.maxOfOrNull { it.endHour } ?: 24

    val hoursToShow = (minHour..maxHour).toList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        items(hoursToShow.size) { i ->
            val hour = hoursToShow[i]

            val dailyEvents = events.filter { it.type == "daily" && it.startHour <= hour && it.endHour > hour }
            val emergencyEvents = events.filter { it.type == "emergency" && it.startHour <= hour && it.endHour > hour }

            val blockHeight = 70.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 时间栏
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(blockHeight)
                        .border(0.5.dp, Color.Gray)
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${hour}:00", fontSize = 12.sp, color = Color.DarkGray)
                }

                // 日常事件栏
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(blockHeight)
                        .padding(horizontal = 2.dp)
                ) {
                    if (dailyEvents.isNotEmpty()) {
                        EventCard(
                            title = dailyEvents[0].title,
                            color = Color(0xFF90CAF9),
                            timeRange = "${dailyEvents[0].startHour}:00-${dailyEvents[0].endHour}:00"
                        )
                    }
                }

                // 突发事件栏
                val emergencyCount = emergencyEvents.size
                if (emergencyCount > 0) {
                    Row(
                        modifier = Modifier
                            .weight(2f)
                            .height(blockHeight)
                    ) {
                        emergencyEvents.forEach { ev ->
                            Box(
                                modifier = Modifier
                                    .weight(1f / emergencyCount.toFloat())
                                    .fillMaxHeight()
                                    .padding(horizontal = 2.dp)
                            ) {
                                EventCard(
                                    title = ev.title,
                                    color = Color(0xFFFF8A80),
                                    timeRange = "${ev.startHour}:00-${ev.endHour}:00"
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(2f))
                }
            }
        }
    }
}

@Composable
fun EventListTitles(events: List<ScheduleEvent>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(events.size) { i ->
            val ev = events[i]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = ev.title,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (ScheduleEvent) -> Unit) {
    var title by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("9") }
    var endHour by remember { mutableStateOf("10") }
    var type by remember { mutableStateOf("daily") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加事件") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("事件标题") }
                )
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { startHour = it },
                        label = { Text("开始小时") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { endHour = it },
                        label = { Text("结束小时") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Button(
                        onClick = { type = "daily" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "daily") Color(0xFF2196F3) else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("日常") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { type = "emergency" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "emergency") Color(0xFFFF5252) else Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("突发") }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newEvent = ScheduleEvent(
                    title,
                    startHour.toIntOrNull() ?: 9,
                    endHour.toIntOrNull() ?: 10,
                    type
                )
                onAdd(newEvent)
            }) {
                Text("添加")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun EventCard(title: String, color: Color, timeRange: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x33000000), RoundedCornerShape(10.dp))
            .padding(6.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = timeRange,
                color = Color(0xFFF5F5F5),
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleBoardPreview() {
    ScheduleBoardScreen()
}
