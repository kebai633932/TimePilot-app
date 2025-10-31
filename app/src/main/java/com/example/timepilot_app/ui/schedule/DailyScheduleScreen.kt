package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ScheduleEvent(
    val title: String,
    val startHour: Int,
    val startMinute: Int,

    val endHour: Int,
    val endMinute: Int,
    val type: String // "daily" or "emergency"
)

@Composable
fun ScheduleBoardScreen() {
    var currentView by remember { mutableStateOf("today") }
    var showAddDialog by remember { mutableStateOf(false) }

    val events = remember { mutableStateListOf(
        ScheduleEvent("晨练", 6, 0, 7, 0, "daily"),
        ScheduleEvent("上班", 9, 0, 12, 0, "daily"),
        ScheduleEvent("午餐", 12, 0, 13, 0, "daily"),
        ScheduleEvent("学习 Kotlin", 14, 15, 16, 45, "daily"),
        ScheduleEvent("突发会议", 10, 20, 11, 10, "emergency"),
        ScheduleEvent("系统崩溃修复", 10, 0, 12, 0, "emergency"),
        ScheduleEvent("服务器异常排查", 10, 30, 12, 0, "emergency"),
        ScheduleEvent("晚餐", 18, 0, 19, 0, "daily")
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
                    Button(
                        onClick = { currentView = "daily" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentView == "daily") Color(0xFF2196F3) else Color.Gray
                        )
                    ) {
                        Text("个人界面", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableEventSchedule(events: List<ScheduleEvent>) {
    // 仅显示事件的最早-最晚时间段，按分钟精度布局；重叠事件分栏并列显示
    if (events.isEmpty()) return

    fun startInMinutes(e: ScheduleEvent): Int = e.startHour * 60 + e.startMinute
    fun endInMinutes(e: ScheduleEvent): Int = e.endHour * 60 + e.endMinute

    val rangeStart = events.minOf { startInMinutes(it) }
    val rangeEnd = events.maxOf { endInMinutes(it) }
    val totalMinutes = (rangeEnd - rangeStart).coerceAtLeast(1)

    // 构建重叠簇（interval clustering）
    data class EventNode(val event: ScheduleEvent, val start: Int, val end: Int, var column: Int = 0)
    data class Cluster(val nodes: MutableList<EventNode>)

    val sorted = events.map { EventNode(it, startInMinutes(it), endInMinutes(it)) }.sortedBy { it.start }
    val clusters = mutableListOf<Cluster>()
    var current = mutableListOf<EventNode>()
    var currentMaxEnd = -1
    for (n in sorted) {
        if (current.isEmpty()) {
            current.add(n)
            currentMaxEnd = n.end
        } else if (n.start < currentMaxEnd) { // overlap with cluster
            current.add(n)
            if (n.end > currentMaxEnd) currentMaxEnd = n.end
        } else {
            clusters.add(Cluster(current))
            current = mutableListOf(n)
            currentMaxEnd = n.end
        }
    }
    if (current.isNotEmpty()) clusters.add(Cluster(current))

    // 对每个簇做贪心着色，分配列
    data class Positioned(val node: EventNode, val totalColumns: Int)
    val positioned = mutableListOf<Positioned>()
    clusters.forEach { cluster ->
        // 列的结束时间，用于放置下一个可用列
        val columnsEnd = mutableListOf<Int>()
        cluster.nodes.forEach { node ->
            var placed = false
            for (i in columnsEnd.indices) {
                if (node.start >= columnsEnd[i]) { // 可复用该列
                    node.column = i
                    columnsEnd[i] = node.end
                    placed = true
                    break
                }
            }
            if (!placed) {
                node.column = columnsEnd.size
                columnsEnd.add(node.end)
            }
        }
        val totalCols = columnsEnd.size.coerceAtLeast(1)
        cluster.nodes.forEach { positioned.add(Positioned(it, totalCols)) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        val availableHeightDp = maxHeight
        val fullWidth = maxWidth
        val minuteHeight = if (availableHeightDp.value > 0f) availableHeightDp / totalMinutes else 0.8.dp
        val hourHeight = minuteHeight * 60
        val totalHeight = minuteHeight * totalMinutes

        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧整点刻度（限定在范围内）——文字放在横线位置
            val firstHour = kotlin.math.floor(rangeStart / 60.0).toInt()
            val lastHour = kotlin.math.ceil(rangeEnd / 60.0).toInt()
                Box(
                    modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .clipToBounds()
                    .background(Color(0xFFF7F7F7))
            ) {
                for (h in firstHour..lastHour) {
                    val minutesFromStart = (h * 60 - rangeStart).coerceAtLeast(0)
                    if (minutesFromStart in 0..totalMinutes) {
                        // 将文本中心对齐到线：向上偏移约半个文本高度
                        Text(
                            text = String.format("%02d:00", h),
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = minuteHeight * minutesFromStart - 8.dp)
                        )
                    }
                }
            }

            // 右侧事件时间轴
            val scrollState = rememberScrollState()
            Box(
                        modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .clipToBounds()
            ) {
                // 以右侧容器的宽度作为事件布局的可用宽度
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val contentWidth = maxWidth
                    Box(modifier = Modifier.height(totalHeight).fillMaxWidth().clipToBounds()) {
                    // 小时网格线
                    for (h in firstHour..lastHour) {
                        val minutesFromStart = (h * 60 - rangeStart).coerceAtLeast(0)
                        if (minutesFromStart in 0..totalMinutes) {
                            Box(
                                modifier = Modifier
                                    .offset(y = minuteHeight * minutesFromStart)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0x22000000))
                            )
                        }
                    }

                        // 放置事件：按列分宽度并右移到对应列
                        positioned.forEach { p ->
                            val node = p.node
                            val rawTop = minuteHeight * (node.start - rangeStart)
                            val rawHeight = minuteHeight * (node.end - node.start)
                            val columnFraction = 1f / p.totalColumns
                            val columnWidth = contentWidth * columnFraction
                            val gap = 6.dp

                            // 垂直边界裁剪与最小高度
                            val top = rawTop.coerceIn(0.dp, totalHeight)
                            val maxAvailableHeight = (totalHeight - top).coerceAtLeast(0.dp)
                            val height = rawHeight
                                .coerceAtLeast(24.dp)
                                .coerceAtMost(maxAvailableHeight)

                            // 水平边界保护（相对于右侧容器宽度）
                            val width = (columnWidth - gap).coerceAtLeast(12.dp).coerceAtMost(contentWidth)
                            val maxX = (contentWidth - width).coerceAtLeast(0.dp)
                            val xOffset = (columnWidth * node.column).coerceAtMost(maxX)

                            if (height > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .offset(x = xOffset, y = top)
                                        .requiredWidth(width)
                                        .height(height)
                                ) {
                                    val color = if (node.event.type == "emergency") Color(0xFFFF8A80) else Color(0xFF90CAF9)
                                EventCard(
                                        title = node.event.title,
                                        color = color,
                                        timeRange = String.format(
                                            "%02d:%02d-%02d:%02d",
                                            node.event.startHour, node.event.startMinute, node.event.endHour, node.event.endMinute
                                        )
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
    var startMinute by remember { mutableStateOf("0") }
    var endHour by remember { mutableStateOf("10") }
    var endMinute by remember { mutableStateOf("0") }
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
                        value = startMinute,
                        onValueChange = { startMinute = it },
                        label = { Text("开始分钟") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { endHour = it },
                        label = { Text("结束小时") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endMinute,
                        onValueChange = { endMinute = it },
                        label = { Text("结束分钟") },
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
                    startMinute.toIntOrNull() ?: 0,
                    endHour.toIntOrNull() ?: 10,
                    endMinute.toIntOrNull() ?: 0,
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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x33000000), RoundedCornerShape(10.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        val h = maxHeight
        val w = maxWidth

        // 自适应排版规则：
        // - 极小：仅显示时间（高度<28dp 或 宽度<80dp）
        // - 中等：显示标题(最多1-2行) + 时间
        // - 足够：显示标题(2行) + 时间
        val isTiny = h < 28.dp || w < 80.dp
        val isCompact = h < 44.dp || w < 120.dp

        val titleFont = when {
            isTiny -> 11.sp
            isCompact -> 13.sp
            else -> 14.sp
        }
        val timeFont = when {
            isTiny -> 10.sp
            isCompact -> 11.sp
            else -> 12.sp
        }
        val titleMaxLines = when {
            isTiny -> 1
            isCompact -> 1
            else -> 2
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isTiny) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                    fontSize = titleFont,
                    maxLines = titleMaxLines,
                    overflow = TextOverflow.Ellipsis
            )
            }
            Text(
                text = if (isTiny) title else timeRange,
                color = Color(0xFFF5F5F5),
                fontSize = timeFont,
                maxLines = if (isTiny) 1 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleBoardPreview() {
    ScheduleBoardScreen()
}
