import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalendarScreen(
    onMenuItemClick: (String) -> Unit,
    onNavigationItemClick: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var currentViewType by remember { mutableStateOf(CalendarViewType.MONTH) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            NavigationDrawerContent(
                onItemClick = { item ->
                    onNavigationItemClick(item)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "我的日程",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "打开导航菜单",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // 菜单项
                        IconButton(onClick = { onMenuItemClick("search") }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜索",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { onMenuItemClick("more") }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "更多选项",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2196F3) // light_blue_primary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 视图切换控制区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD)) // light_blue_light
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalendarViewButton(
                        text = "年视图",
                        isSelected = currentViewType == CalendarViewType.YEAR,
                        onClick = { currentViewType = CalendarViewType.YEAR }
                    )
                    CalendarViewButton(
                        text = "月视图",
                        isSelected = currentViewType == CalendarViewType.MONTH,
                        onClick = { currentViewType = CalendarViewType.MONTH }
                    )
                    CalendarViewButton(
                        text = "日视图",
                        isSelected = currentViewType == CalendarViewType.DAY,
                        onClick = { currentViewType = CalendarViewType.DAY }
                    )
                }

                // 视图容器
                when (currentViewType) {
                    CalendarViewType.YEAR -> YearCalendarView()
                    CalendarViewType.MONTH -> MonthCalendarView()
                    CalendarViewType.DAY -> DayCalendarView()
                }
            }
        }
    }
}

@Composable
fun CalendarViewButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.3f)  // 每个按钮占30%宽度
            .padding(horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                Color(0xFF2196F3)
            } else {
                Color(0xFF64B5F6)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
fun NavigationDrawerContent(
    onItemClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
    ) {
        // 导航头部
        NavigationHeader()

        // 导航菜单项
        NavigationMenuItem(
            icon = Icons.Default.CalendarToday,
            label = "我的日程",
            onClick = { onItemClick("schedule") }
        )
        NavigationMenuItem(
            icon = Icons.Default.Event,
            label = "活动管理",
            onClick = { onItemClick("events") }
        )
        NavigationMenuItem(
            icon = Icons.Default.Settings,
            label = "设置",
            onClick = { onItemClick("settings") }
        )
        NavigationMenuItem(
            icon = Icons.Default.Help,
            label = "帮助与反馈",
            onClick = { onItemClick("help") }
        )
    }
}

@Composable
fun NavigationHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF2196F3)), // light_blue_primary
        contentAlignment = Alignment.BottomStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 用户头像
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "用户名",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "user@example.com",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun NavigationMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            color = Color(0xFF333333),
            fontSize = 16.sp
        )
    }
}

// 视图类型枚举
enum class CalendarViewType {
    YEAR, MONTH, DAY
}

// 占位符视图组件
@Composable
fun YearCalendarView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("年视图内容")
    }
}

@Composable
fun MonthCalendarView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("月视图内容")
    }
}

@Composable
fun DayCalendarView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("日视图内容")
    }
}

// 预览函数
@Preview(showBackground = true)
@Composable
fun MainCalendarScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainCalendarScreen(
                onMenuItemClick = { menuItem ->
                    println("菜单项点击: $menuItem")
                },
                onNavigationItemClick = { navItem ->
                    println("导航项点击: $navItem")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationDrawerPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavigationDrawerContent(
                onItemClick = { println("导航项: $it") },
                onClose = { }
            )
        }
    }
}