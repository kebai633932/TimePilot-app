package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    title: String,
    color: Color,
    timeRange: String,
    onClick: (() -> Unit)? = null
) {
    // 单层卡片：定位和尺寸直接作用在外层；根据卡片尺寸自适应布局。
    BoxWithConstraints(
        modifier = modifier
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .shadow(3.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        // 紧凑阈值：卡片过矮或过窄时，仅显示一行标题并减小字号。
        val isCompact = maxHeight < 44.dp || maxWidth < 120.dp

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 13.sp else 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = timeRange,
                color = Color(0xFFF5F5F5),
                fontSize = if (isCompact) 11.sp else 12.sp
            )
        }
    }
}