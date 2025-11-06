package com.example.timepilot_app.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timepilot_app.model.ScheduleEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(onDismiss: () -> Unit, onAdd: (ScheduleEvent) -> Unit) {
    var title by remember { mutableStateOf("") }
    var startHourText by remember { mutableStateOf("09") }
    var startMinuteText by remember { mutableStateOf("00") }
    var endHourText by remember { mutableStateOf("10") }
    var endMinuteText by remember { mutableStateOf("00") }
    var type by remember { mutableStateOf("daily") }
    var quadrant by remember { mutableStateOf(1) }

    var quadrantExpanded by remember { mutableStateOf(false) }

    fun parse2(s: String): Int? = s.toIntOrNull()
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
        title = { Text("添加事件") },
        text = {
            val formScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = 440.dp)
                    .verticalScroll(formScroll)
            ) {
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
                        supportingText = { if (!validHour(sh)) Text("0-23") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = startMinuteText,
                        onValueChange = { v -> startMinuteText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("开始分钟(0-59)") },
                        isError = !validMinute(sm),
                        supportingText = { if (!validMinute(sm)) Text("0-59") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = endHourText,
                        onValueChange = { v -> endHourText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("结束小时(0-23)") },
                        isError = !validHour(eh),
                        supportingText = { if (!validHour(eh)) Text("0-23") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endMinuteText,
                        onValueChange = { v -> endMinuteText = v.filter { it.isDigit() }.take(2) },
                        label = { Text("结束分钟(0-59)") },
                        isError = !validMinute(em),
                        supportingText = { if (!validMinute(em)) Text("0-59") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (startValid && endValid && !orderValid) {
                    Text("结束时间必须晚于开始时间", color = Color.Red, modifier = Modifier.padding(top = 4.dp))
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
                val newEvent = ScheduleEvent(
                    title,
                    sh!!,
                    sm!!,
                    eh!!,
                    em!!,
                    type,
                    quadrant
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

@Preview(showBackground = true)
@Composable
private fun AddEventDialogPreview() {
    // 在预览中展示对话框，不做任何状态修改
    AddEventDialog(onDismiss = {}, onAdd = { _ -> })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    label: String,
    display: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            // 使用 Column 替代 LazyColumn
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectIndex(index)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}