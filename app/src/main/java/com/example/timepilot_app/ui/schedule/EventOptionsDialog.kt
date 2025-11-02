package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.timepilot_app.model.ScheduleEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventOptionsDialog(
    event: ScheduleEvent, // 当前事件
    onUpdate: (ScheduleEvent) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    // 内部状态，用于编辑
    var title by remember { mutableStateOf(event.title) }
    var startHourText by remember { mutableStateOf(event.startHour.toString().padStart(2, '0')) }
    var startMinuteText by remember { mutableStateOf(event.startMinute.toString().padStart(2, '0')) }
    var endHourText by remember { mutableStateOf(event.endHour.toString().padStart(2, '0')) }
    var endMinuteText by remember { mutableStateOf(event.endMinute.toString().padStart(2, '0')) }
    var type by remember { mutableStateOf(event.type) }
    var quadrant by remember { mutableStateOf(event.quadrant) }

    var quadrantExpanded by remember { mutableStateOf(false) }

    fun parse2(s: String) = s.toIntOrNull()
    val sh = parse2(startHourText)
    val sm = parse2(startMinuteText)
    val eh = parse2(endHourText)
    val em = parse2(endMinuteText)

    val validHour: (Int?) -> Boolean = { it != null && it in 0..23 }
    val validMinute: (Int?) -> Boolean = { it != null && it in 0..59 }
    val startValid = validHour(sh) && validMinute(sm)
    val endValid = validHour(eh) && validMinute(em)
    val orderValid = if (startValid && endValid) (eh!! * 60 + em!!) > (sh!! * 60 + sm!!) else false
    val canSubmit = title.isNotBlank() && startValid && endValid && orderValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑事件") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("事件标题") }
                )
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = startHourText,
                        onValueChange = { v -> startHourText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("开始小时(0-23)") },
                        isError = !validHour(sh),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = startMinuteText,
                        onValueChange = { v -> startMinuteText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("开始分钟(0-59)") },
                        isError = !validMinute(sm),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = endHourText,
                        onValueChange = { v -> endHourText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("结束小时(0-23)") },
                        isError = !validHour(eh),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endMinuteText,
                        onValueChange = { v -> endMinuteText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("结束分钟(0-59)") },
                        isError = !validMinute(em),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (startValid && endValid && !orderValid) {
                    Text("结束时间必须晚于开始时间", color = Color.Red)
                }

                // 四象限选择
                ExposedDropdown(
                    label = "象限",
                    display = when (quadrant) { 1 -> "1-重要紧急"; 2 -> "2-重要不紧急"; 3 -> "3-紧急不重要"; 4 -> "4-不重要不紧急"; else -> quadrant.toString() },
                    expanded = quadrantExpanded,
                    onExpandedChange = { quadrantExpanded = it },
                    options = listOf("1-重要紧急", "2-重要不紧急", "3-紧急不重要", "4-不重要不紧急"),
                    onSelectIndex = { idx -> quadrant = idx + 1 },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(enabled = canSubmit, onClick = {
                onUpdate(
                    event.copy(
                        title = title,
                        startHour = sh!!,
                        startMinute = sm!!,
                        endHour = eh!!,
                        endMinute = em!!,
                        type = type,
                        quadrant = quadrant
                    )
                )
                onDismiss()
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onDelete) { Text("删除") }
        }
    )
}




