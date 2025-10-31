package com.example.timepilot_app.ui.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.timepilot_app.R
import com.example.timepilot_app.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = remember { RegisterViewModel() },
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图
        Image(
            painter = painterResource(id = R.drawable.ic_app_login_register_background),
            contentDescription = "注册背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 注册表单卡片
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = Color(0x99FFFFFF)),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(28.dp)) {

                Text(
                    "用户注册",
                    fontSize = 24.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 用户名
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    placeholder = { Text("请输入用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 邮箱
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("邮箱") },
                    placeholder = { Text("请输入邮箱") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 验证码输入 + 获取按钮
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
                        onClick = { viewModel.sendEmailCode(email) },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        enabled = !viewModel.isSendingCode
                    ) {
                        Text(
                            text = if (viewModel.isSendingCode) "发送中..." else "获取验证码",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                if (viewModel.codeMessage.isNotEmpty()) {
                    Text(
                        viewModel.codeMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 密码
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    placeholder = { Text("请输入密码") },
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

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                // 协议
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1976D2))
                    )
                    Text("我已阅读并同意 用户协议 和 隐私政策", fontSize = 14.sp, color = Color(0xFF333333))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 注册按钮
                Button(
                    onClick = {
                        viewModel.register(
                            username = username,
                            email = email,
                            code = code,
                            password = password,
                            confirmPassword = confirmPassword,
                            onSuccess = onRegisterSuccess
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = agreeToTerms && !viewModel.isRegistering,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (agreeToTerms && !viewModel.isRegistering)
                            Color(0xFF1976D2) else Color(0xFFB0BEC5)
                    )
                ) {
                    Text("注册", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (viewModel.registerMessage.isNotEmpty()) {
                    Text(viewModel.registerMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("已有账号？")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "立即登录",
                        color = Color(0xFF1976D2),
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

// 输入框统一样式
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
//    val navController = rememberNavController()
    MaterialTheme {
        RegisterScreen(
            onRegisterSuccess = {},
            onLoginClick = {}
        )
    }
}
