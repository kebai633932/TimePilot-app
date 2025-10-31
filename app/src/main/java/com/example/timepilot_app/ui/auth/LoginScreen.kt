import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.timepilot_app.R   // ✅ 导入你项目的资源R文件


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val viewModel = remember { com.example.timepilot_app.viewmodel.LoginViewModel() }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        runCatching {
            Image(
                painter = painterResource(id = R.drawable.ic_app_login_register_background),
                contentDescription = "登录背景",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }.onFailure {
            // 预览模式或资源丢失时使用纯色背景
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD)) // 浅蓝色替代背景
            )
        }


        // 登录表单区域
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x99FFFFFF) // 半透明白色背景
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = "用户登录",
                    color = Color(0xFF333333),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 账号输入区域
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("请输入账号或邮箱", color = Color(0xFF999999))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFF757575),
                        cursorColor = Color(0xFF1976D2),
                        focusedTextColor = Color(0xFF333333),
                        unfocusedTextColor = Color(0xFF333333)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("账号") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 密码输入区域
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("请输入密码", color = Color(0xFF999999))
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码",
                                tint = Color(0xFF1976D2)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFF757575),
                        cursorColor = Color(0xFF1976D2),
                        focusedTextColor = Color(0xFF333333),
                        unfocusedTextColor = Color(0xFF333333)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("密码") }
                )
//todo 没做这方面的接口
//                // 忘记密码
//                Text(
//                    text = "忘记密码？",
//                    color = Color(0xFF1976D2),
//                    fontSize = 14.sp,
//                    modifier = Modifier
//                        .clickable { onForgotPasswordClick() }
//                        .padding(top = 12.dp)
//                        .align(Alignment.End)
//                )

                Spacer(modifier = Modifier.height(32.dp))

                // 登录按钮
                Button(
                    onClick = {
                        viewModel.login(username, password) { success, message ->
                            loginMessage = message
                            println(message)
                            if (success) {
                                // 登录成功跳转
                                onLoginSuccess(username, password) // AppNavHost 中的 lambda 会导航到 home
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "登录",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (loginMessage.isNotEmpty()) {
                    Text(
                        text = loginMessage,
                        color = if (loginMessage.contains("成功")) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 注册链接
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "还没有账号？",
                        color = Color(0xFF666666),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "立即注册",
                        color = Color(0xFF1976D2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRegisterClick() } // ✅ 调用 Lambda
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen(
                onLoginSuccess = { username, password ->
                    println("登录: $username, $password")
                },
                onRegisterClick = {
                    println("跳转到注册")
                },
                onForgotPasswordClick = {
                    println("忘记密码")
                }
            )
        }
    }
}
