package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timepilot_app.model.ScheduleEvent
import kotlinx.coroutines.launch

@Composable
fun ScrollableEventSchedule(
    events: List<ScheduleEvent>,
    onDeleteEvent: suspend (ScheduleEvent) -> Unit, // 删除事件接口
    onEditEvent: suspend (ScheduleEvent) -> Unit    // 修改事件接口
) {
    if (events.isEmpty()) return

    // --- 内部状态 ---
    var selectedEvent by remember { mutableStateOf<ScheduleEvent?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun startInMinutes(e: ScheduleEvent): Int = e.startHour * 60 + e.startMinute
    fun endInMinutes(e: ScheduleEvent): Int = e.endHour * 60 + e.endMinute

    val rangeStart = events.minOf { startInMinutes(it) }
    val rangeEnd = events.maxOf { endInMinutes(it) }
    val totalMinutes = (rangeEnd - rangeStart).coerceAtLeast(1)

    // -------- 构建重叠簇 --------
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
        } else if (n.start < currentMaxEnd) {
            current.add(n)
            if (n.end > currentMaxEnd) currentMaxEnd = n.end
        } else {
            clusters.add(Cluster(current))
            current = mutableListOf(n)
            currentMaxEnd = n.end
        }
    }
    if (current.isNotEmpty()) clusters.add(Cluster(current))

    // -------- 分配列 --------
    data class Positioned(val node: EventNode, val totalColumns: Int)
    val positioned = mutableListOf<Positioned>()
    clusters.forEach { cluster ->
        val columnsEnd = mutableListOf<Int>()
        cluster.nodes.forEach { node ->
            var placed = false
            for (i in columnsEnd.indices) {
                if (node.start >= columnsEnd[i]) {
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

    // -------- 渲染布局 --------
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        val availableHeightDp = maxHeight
        val minuteHeight = if (availableHeightDp.value > 0f) availableHeightDp / totalMinutes else 0.8.dp
        val totalHeight = minuteHeight * totalMinutes

        Row(modifier = Modifier.fillMaxSize()) {
            val firstHour = kotlin.math.floor(rangeStart / 60.0).toInt()
            val lastHour = kotlin.math.ceil(rangeEnd / 60.0).toInt()

            // --- 左侧时间刻度 ---
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

            // --- 右侧可滚动区域 ---
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .clipToBounds()
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val contentWidth = maxWidth
                    Box(modifier = Modifier.height(totalHeight).fillMaxWidth().clipToBounds()) {
                        // 网格线
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

                        // --- 放置事件卡片 ---
                        positioned.forEach { p ->
                            val node = p.node
                            val rawTop = minuteHeight * (node.start - rangeStart)
                            val rawHeight = minuteHeight * (node.end - node.start)
                            val columnFraction = 1f / p.totalColumns
                            val columnWidth = contentWidth * columnFraction
                            val gap = 6.dp

                            val top = rawTop.coerceIn(0.dp, totalHeight)
                            val maxAvailableHeight = (totalHeight - top).coerceAtLeast(0.dp)
                            val height = rawHeight.coerceAtLeast(24.dp).coerceAtMost(maxAvailableHeight)

                            val width = (columnWidth - gap).coerceAtLeast(12.dp).coerceAtMost(contentWidth)
                            val maxX = (contentWidth - width).coerceAtLeast(0.dp)
                            val xOffset = (columnWidth * node.column).coerceAtMost(maxX)

                            if (height > 0.dp) {
                                val color = when (node.event.quadrant) {
                                    1 -> Color(0xFFE57373)
                                    2 -> Color(0xFF64B5F6)
                                    3 -> Color(0xFFFFB74D)
                                    4 -> Color(0xFFBDBDBD)
                                    else -> Color(0xFF90CAF9)
                                }

                                EventCard(
                                    modifier = Modifier
                                        .offset(x = xOffset, y = top)
                                        .requiredWidth(width)
                                        .height(height),
                                    title = node.event.title,
                                    color = color,
                                    timeRange = String.format(
                                        "%02d:%02d-%02d:%02d",
                                        node.event.startHour, node.event.startMinute,
                                        node.event.endHour, node.event.endMinute
                                    ),
                                    onClick = {
                                        selectedEvent = node.event
                                        showDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ScrollableEventSchedule 中弹窗逻辑
    if (showDialog && selectedEvent != null) {
        val event = selectedEvent!!

        // 使用可编辑表单的弹窗
        EventOptionsDialog(
            event = event,
            onUpdate = { updatedEvent ->
                coroutineScope.launch {
                    onEditEvent(updatedEvent) // 调用后端接口 / ViewModel
                }
                showDialog = false
            },
            onDelete = {
                coroutineScope.launch {
                    onDeleteEvent(event) // 调用后端接口 / ViewModel
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

}
