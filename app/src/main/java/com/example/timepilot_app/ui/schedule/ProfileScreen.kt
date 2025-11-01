package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF6F7FB))
    ) {
        // Header with gradient and avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder avatar
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("TimePilot 用户", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("高效时间管理", color = Color(0xCCFFFFFF), fontSize = 13.sp)
                }
                IconButton(onClick = { /* TODO: edit profile */ }) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            StatCard(title = "今日计划", value = "8", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            StatCard(title = "完成", value = "5", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            StatCard(title = "效率(%)", value = "78", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick actions
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            ListItem(
                headlineContent = { Text("提醒与通知") },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF2196F3)) },
                trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color(0xFFB0BEC5)) },
                modifier = Modifier.clickable { /* TODO */ }
            )
            Divider()
            ListItem(
                headlineContent = { Text("通用设置") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF2196F3)) },
                trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color(0xFFB0BEC5)) },
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // More sections
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(2.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
        ) {
            ListItem(
                headlineContent = { Text("关于 TimePilot") },
                supportingContent = { Text("版本 1.0.0") },
                trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color(0xFFB0BEC5)) },
                modifier = Modifier.clickable { /* TODO */ }
            )
            Divider()
            ListItem(
                headlineContent = { Text("退出登录") },
                leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFE57373)) },
                modifier = Modifier.clickable { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color(0xFF263238))
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color(0xFF607D8B))
        }
    }
}
