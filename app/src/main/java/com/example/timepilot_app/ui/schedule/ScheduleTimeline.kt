package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timepilot_app.model.ScheduleEvent

@Composable
fun ScrollableEventSchedule(events: List<ScheduleEvent>) {
    // 仅渲染 [最早开始, 最晚结束] 时间窗口；垂直按分钟映射像素，水平用列并排解决冲突。
    if (events.isEmpty()) return

    fun startInMinutes(e: ScheduleEvent): Int = e.startHour * 60 + e.startMinute
    fun endInMinutes(e: ScheduleEvent): Int = e.endHour * 60 + e.endMinute

    val rangeStart = events.minOf { startInMinutes(it) }
    val rangeEnd = events.maxOf { endInMinutes(it) }
    val totalMinutes = (rangeEnd - rangeStart).coerceAtLeast(1)

    // 构建重叠簇（interval clustering）：把时间上互相重叠的事件划为同一簇。
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

    // 对簇内事件做贪心“列分配”：优先复用结束早的列，否则开新列。
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

    // 用容器的可用高度推导“每分钟的像素高度”。
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        val availableHeightDp = maxHeight
        val fullWidth = maxWidth
        val minuteHeight = if (availableHeightDp.value > 0f) availableHeightDp / totalMinutes else 0.8.dp
        val hourHeight = minuteHeight * 60
        val totalHeight = minuteHeight * totalMinutes

        Row(modifier = Modifier.fillMaxSize()) {
            // 左侧整点刻度：将标签放在横线处，直观对齐网格线。
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
                // 事件布局基于右侧容器的 maxWidth，避免越过屏幕右边界。
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

                        // 放置事件：计算顶部/高度（分钟->像素），并按列计算宽度与横向偏移。
                        positioned.forEach { p ->
                            val node = p.node
                            val rawTop = minuteHeight * (node.start - rangeStart)
                            val rawHeight = minuteHeight * (node.end - node.start)
                            val columnFraction = 1f / p.totalColumns
                            val columnWidth = contentWidth * columnFraction
                            val gap = 6.dp

                            // 垂直裁剪并设最小高度，避免超出容器且保证可点击性。
                            val top = rawTop.coerceIn(0.dp, totalHeight)
                            val maxAvailableHeight = (totalHeight - top).coerceAtLeast(0.dp)
                            val height = rawHeight
                                .coerceAtLeast(24.dp)
                                .coerceAtMost(maxAvailableHeight)

                            // 水平边界保护：宽度与偏移都以右侧容器宽度为上限。
                            val width = (columnWidth - gap).coerceAtLeast(12.dp).coerceAtMost(contentWidth)
                            val maxX = (contentWidth - width).coerceAtLeast(0.dp)
                            val xOffset = (columnWidth * node.column).coerceAtMost(maxX)

                            if (height > 0.dp) {
                                val color = when (node.event.quadrant) {
                                    1 -> Color(0xFFE57373) // 重要紧急
                                    2 -> Color(0xFF64B5F6) // 重要不紧急
                                    3 -> Color(0xFFFFB74D) // 紧急不重要
                                    4 -> Color(0xFFBDBDBD) // 不重要不紧急
                                    else -> if (node.event.type == "emergency") Color(0xFFFF8A80) else Color(0xFF90CAF9)
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