package com.example.attendancemanagementapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkGray

private enum class DrawerSection {
    APPROVAL, ATTENDANCE, WORK, FINANCE, ASSET, HR, SYSTEM
}

@Composable
fun BasicDrawer(drawerState: DrawerState, onItemClick: (String) -> Unit = {}, onLogoutClick: () -> Unit, content: @Composable () -> Unit) {
    var expandedSection by rememberSaveable { mutableStateOf<DrawerSection?>(null) }

    fun toggle(section: DrawerSection) {
        expandedSection = if (expandedSection == section) null else section
    }

    LaunchedEffect(drawerState.isClosed) {  // 드로어 닫히면 열린 섹션 닫기
        expandedSection = null
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "로그아웃",
                            fontSize = 14.sp,
                            color = DarkGray,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(onClick = { onLogoutClick() })
                        )
                    }

                    Spacer(Modifier.height(60.dp))

                    DrawerSection(
                        title = "결재관리",
                        expanded = expandedSection == DrawerSection.APPROVAL,
                        onHeaderClick = { toggle(DrawerSection.APPROVAL) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("결재 요청") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("승인 대기 목록") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "근태관리",
                        expanded = expandedSection == DrawerSection.ATTENDANCE,
                        onHeaderClick = { toggle(DrawerSection.ATTENDANCE) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("나의 근태현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("부서별 근태현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("휴가 현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("휴가 신청") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("출장 현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("출장 신청") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "업무관리",
                        expanded = expandedSection == DrawerSection.WORK,
                        onHeaderClick = { toggle(DrawerSection.WORK) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("프로젝트 현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("프로젝트 등록") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("투입현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "재무관리",
                        expanded = expandedSection == DrawerSection.FINANCE,
                        onHeaderClick = { toggle(DrawerSection.FINANCE) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("급여명세서") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("직원별 급여명세서") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("급여명세서 등록") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("출장비 현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("회의비 지출현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "자산관리",
                        expanded = expandedSection == DrawerSection.ASSET,
                        onHeaderClick = { toggle(DrawerSection.ASSET) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("법인차량 사용현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("차량정보 관리") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("법인카드 사용현황") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("카드정보 관리") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "인사관리",
                        expanded = expandedSection == DrawerSection.HR,
                        onHeaderClick = { toggle(DrawerSection.HR) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("직원검색") },
                            selected = false,
                            onClick = { onItemClick("employeeSearch") }
                        )
                        NavigationDrawerItem(
                            label = { Text("직원관리") },
                            selected = false,
                            onClick = { onItemClick("employeeManage") }
                        )
                        NavigationDrawerItem(
                            label = { Text("부서관리") },
                            selected = false,
                            onClick = { onItemClick("departmentManage") }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DrawerSection(
                        title = "시스템관리",
                        expanded = expandedSection == DrawerSection.SYSTEM,
                        onHeaderClick = { toggle(DrawerSection.SYSTEM) }
                    ) {
                        NavigationDrawerItem(
                            label = { Text("사용자권한관리") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("접근제어관리") },
                            selected = false,
                            onClick = { /* Handle click */ }
                        )
                        NavigationDrawerItem(
                            label = { Text("공통코드관리") },
                            selected = false,
                            onClick = { onItemClick("codeManage") }
                        )
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    ) {
        content()
    }
}

@Composable
fun UpperDrawerItem(name: String, expanded: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "드로어 메뉴 열림/닫힘 아이콘",
            modifier = Modifier.rotate(-90f).rotate(rotation).size(20.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DrawerSection(
    title: String,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.animateContentSize(animationSpec = spring())) {
        UpperDrawerItem(
            name = title,
            expanded = expanded,
            onClick = onHeaderClick
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit  = shrinkVertically() + fadeOut()
        ) {
            Column {
                content()
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}