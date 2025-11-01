package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.timepilot_app.model.ScheduleEvent

@Composable
fun EventListTitles(events: List<ScheduleEvent>) {
    // 事件列表：按象限从上到下排序（1→4），并用颜色区分象限。
    // 同象限内按开始时间排序。
    val sorted = remember(events) {
        events.sortedWith(
            compareBy<ScheduleEvent> { it.quadrant }
                .thenBy { it.startHour }
                .thenBy { it.startMinute }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(sorted.size) { i ->
            val ev = sorted[i]
            val color = when (ev.quadrant) {
                1 -> Color(0xFFE57373)
                2 -> Color(0xFF64B5F6)
                3 -> Color(0xFFFFB74D)
                4 -> Color(0xFFBDBDBD)
                else -> Color(0xFF90CAF9)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp, 10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ev.title, fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("%02d:%02d - %02d:%02d  |  象限 %d",
                                ev.startHour, ev.startMinute, ev.endHour, ev.endMinute, ev.quadrant
                            ),
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}