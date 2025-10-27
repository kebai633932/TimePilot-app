package com.example.timepilot_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timepilot_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onGetCodeClick: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    val isInPreview = LocalInspectionMode.current

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图（预览模式使用浅色背景）
        if (!isInPreview) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_login_register_background),
                contentDescription = "注册背景",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD))
            )
        }

        // 注册表单 Card
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = Color(0x99FFFFFF)),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "用户注册",
                    fontSize = 24.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 用户名
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    placeholder = { Text("请输入用户名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 邮箱
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("邮箱") },
                    placeholder = { Text("请输入邮箱地址") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 验证码 + 获取按钮
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("验证码") },
                        placeholder = { Text("请输入验证码") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onGetCodeClick(email) },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("获取验证码", color = Color.White, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 密码
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    placeholder = { Text("请输入密码（至少6位）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "切换密码可见性",
                                tint = Color(0xFF1976D2)
                            )
                        }
                    },
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 确认密码
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认密码") },
                    placeholder = { Text("请再次输入密码") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "切换确认密码可见性",
                                tint = Color(0xFF1976D2)
                            )
                        }
                    },
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 协议勾选
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1976D2))
                    )
                    Text(
                        text = "我已阅读并同意 用户协议 和 隐私政策",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 注册按钮
                Button(
                    onClick = {
                        if (agreeToTerms) {
                            onRegisterClick(username, email, code, password, confirmPassword)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = agreeToTerms,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (agreeToTerms) Color(0xFF1976D2) else Color(0xFFB0BEC5)
                    )
                ) {
                    Text("注册", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 登录提示
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "已有账号？", color = Color(0xFF666666), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "立即登录",
                        color = Color(0xFF1976D2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

// 统一输入框样式
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF1976D2),
    unfocusedBorderColor = Color(0xFF757575),
    cursorColor = Color(0xFF1976D2),
    focusedLabelColor = Color(0xFF1976D2),
    unfocusedLabelColor = Color(0xFF757575),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)

// 预览函数
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _, _ -> },
                onLoginClick = {},
                onGetCodeClick = {}
            )
        }
    }
}
