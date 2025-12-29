package com.example.attendancemanagementapp.ui.project.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.MeetingsInfo
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Blue
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.launch

/* 프로젝트 상세 화면 */
@Composable
fun ProjectDetailScreen(navController: NavController, projectViewModel: ProjectViewModel, meetingViewModel: MeetingViewModel) {
    val onEvent = projectViewModel::onDetailEvent
    val projectDetailState by projectViewModel.projectDetailState.collectAsState()

    val tabs = listOf("프로젝트 정보", "회의록 정보")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var openDelete by remember { mutableStateOf(false) }
    var openStop by remember { mutableStateOf(false) }
    
    if (openDelete) {
        BasicDialog(
            title = "프로젝트를 삭제하시겠습니까?",
            onDismiss = { openDelete = false },
            onClickConfirm = {
                onEvent(ProjectDetailEvent.ClickedDelete)
                openDelete = false
            }
        )
    }

    if (openStop) {
        BasicDialog(
            title = "프로젝트를 중단하시겠습니까?",
            onDismiss = { openStop = false },
            onClickConfirm = {
                onEvent(ProjectDetailEvent.ClickedStop)
                openStop = false
            }
        )
    }
    
    Scaffold(
        topBar = {
            BasicTopBar(
                title = "프로젝트 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDelete = true }
            )
        },
        floatingActionButton = {
            if (pagerState.currentPage == 1) {
                BasicFloatingButton(
                    onClick = {
                        meetingViewModel.onAddEvent(MeetingAddEvent.InitWith(projectDetailState.projectInfo.projectId, projectDetailState.projectInfo.projectName, true))
                        navController.navigate("meetingAdd")
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MainBlue
                    )
                }
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = pagerState.currentPage == idx,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                        },
                        text = { Text(title) },
                        selectedContentColor = MainBlue,
                        unselectedContentColor = Color.Black
                    )
                }
            }

            Box(Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 프로젝트 정보
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    ProjectInfoCard(
                                        projectInfo = projectDetailState.projectInfo
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        SubButton(
                                            name = "중단",
                                            onClick = { openStop = true }
                                        )

                                        BasicButton(
                                            name = "수정",
                                            onClick = { navController.navigate("projectEdit") }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }

                            1 -> {  // 회의록 정보
                                MeetingExpenseInfoCard(
                                    total = projectDetailState.projectInfo.meetingExpense ?: 0,
                                    used = projectDetailState.projectInfo.totalMeetingExpense,
                                    unused = if (projectDetailState.projectInfo.meetingExpense == null) 0 else projectDetailState.projectInfo.meetingExpense!! - projectDetailState.projectInfo.totalMeetingExpense
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                MeetingMinutesInfoCard(
                                    meetingMinutes = projectDetailState.projectInfo.meetings,
                                    onEvent = onEvent,
                                    navController = navController,
                                    meetingViewModel = meetingViewModel
                                )

                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 프로젝트 정보 출력 카드 */
@Composable
private fun ProjectInfoCard(projectInfo: ProjectDTO.GetProjectResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TowLineInfoBar(name = "구분", value = projectInfo.type)
            TowLineInfoBar(name = "제목", value = projectInfo.projectName)
            TowLineInfoBar(name = "주관기관", value = projectInfo.companyName ?: "")
            TowLineInfoBar(name = "담당부서", value = projectInfo.departmentName)
            TowLineInfoBar(name = "프로젝트 책임자", value = projectInfo.managerName)
            TowLineInfoBar(name = "사업비", value = "%,d".format(projectInfo.businessExpense))
            TowLineInfoBar(name = "사업기간", value = "${projectInfo.businessStartDate} - ${projectInfo.businessEndDate}")
            TowLineInfoBar(name = "계획기간", value = if (projectInfo.planStartDate.isNullOrBlank() || projectInfo.planEndDate.isNullOrBlank()) "" else "${projectInfo.planStartDate} - ${projectInfo.planEndDate}")
            TowLineInfoBar(name = "실제기간", value = if (projectInfo.realStartDate.isNullOrBlank() || projectInfo.realEndDate.isNullOrBlank()) "" else "${projectInfo.realStartDate} - ${projectInfo.realEndDate}")
            TowLineInfoBar(name = "진행상태", value = projectInfo.status)
            TowLineInfoBar(name = "투입인력", value = projectInfo.assignedPersonnels?.joinToString(", ") { "${it.name}(${it.type})" } ?: "")
            TowLineInfoBar(name = "비고", value = projectInfo.remark ?: "비고 없음")
        }
    }
}

/* 회의록 정보 출력 카드 */
@Composable
private fun MeetingMinutesInfoCard(meetingMinutes: List<MeetingsInfo>, onEvent: (ProjectDetailEvent) -> Unit, navController: NavController, meetingViewModel: MeetingViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (meetingMinutes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "조회된 결과가 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(meetingMinutes) { meetingMinute ->
                        MeetingMinuteItem(
                            meetingMinuteInfo = meetingMinute,
                            onClick = {
                                meetingViewModel.getMeeting(meetingMinute.id)
                                navController.navigate("meetingDetail")
                            }
                        )
                    }
                }
            }
        }
    }
}

/* 회의록 목록 아이템 */
@Composable
private fun MeetingMinuteItem(meetingMinuteInfo: MeetingsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(meetingMinuteInfo.title, "", fontSize = 15.sp)
        TwoInfoBar(meetingMinuteInfo.attendee, "${"%,d".format(meetingMinuteInfo.expense)}원", fontSize = 14.sp)
        TwoInfoBar("${meetingMinuteInfo.startDate} ~ ${meetingMinuteInfo.endDate}", "", color = TextGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
    }
}

/* 회의비 사용내역 출력 카드 */
@Composable
private fun MeetingExpenseInfoCard(total: Int, used: Int, unused: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "회의비 사용내역",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MeetingExpenseInfoItem("전체", total)
                MeetingExpenseInfoItem("사용", used)
                MeetingExpenseInfoItem("미사용", unused)
            }
        }
    }
}

/* 회의비 사용내역 출력 아이템 */
@Composable
private fun MeetingExpenseInfoItem(name: String, value: Int) {
    val color = when (name) {
        "전체" -> ApprovalInfoItem_Green
        "사용" -> AttendanceInfoItem_Blue
        "미사용" -> ApprovalInfoItem_Red
        else -> Color.Black
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${name}  ",
            fontSize = 14.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "${"%,d".format(value)}원",
            fontSize = 14.sp
        )
    }
}