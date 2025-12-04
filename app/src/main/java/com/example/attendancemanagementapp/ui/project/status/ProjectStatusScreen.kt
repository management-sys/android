package com.example.attendancemanagementapp.ui.project.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.home.calendar.AttendanceInfoItem
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageEvent
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Blue
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import java.util.Locale

/* 프로젝트 현황 화면 */
@Composable
fun ProjectStatusScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onStatusEvent
    val projectStatusState by projectViewModel.projectStatusState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(ProjectStatusEvent.Init)
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "프로젝트 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            ProjectStatusInfo()
            ProjectList(projectStatusState = projectStatusState, onEvent = onEvent, navController = navController)
        }
    }
}

/* 프로젝트 진행 현황 */
@Composable
private fun ProjectStatusInfo() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AttendanceInfoItem(count = 1, name = "전체", backColor = AttendanceInfoItem_Gray)
            AttendanceInfoItem(count = 1, name = "진행중", backColor = MainBlue)
            AttendanceInfoItem(count = 0, name = "미진행", backColor = ApprovalInfoItem_Red)
            AttendanceInfoItem(count = 0, name = "완료", backColor = AttendanceInfoItem_Blue)
        }
    }
}

/* 프로젝트 목록 */
@Composable
private fun ProjectList(projectStatusState: ProjectStatusState, onEvent: (ProjectStatusEvent) -> Unit, navController: NavController) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        ProjectSearchBottomSheet(
            projectStatusState = projectStatusState,
            onEvent = onEvent,
            onDismiss = { openSheet = false }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchBar(
                    modifier = Modifier.weight(0.85f),
                    searchState = SearchState(
                        value = projectStatusState.searchText,
                        onValueChange = { onEvent(ProjectStatusEvent.ChangedSearchTextWith(it)) },
                        onClickSearch = {
                            // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
//                            if (employeeManageState.paginationState.currentPage <= employeeManageState.paginationState.totalPage) {
//                                onEvent(EmployeeManageEvent.ClickedSearch)
//                                keyboardController?.hide()
//                                focusManager.clearFocus(force = true)
//                            }
                        },
                        onClickInit = { onEvent(ProjectStatusEvent.ClickedInitSearchText) }
                    ),
                    hint = "검색어를 입력하세요"
                )

                IconButton(
                    modifier = Modifier.weight(0.15f),
                    onClick = { openSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "검색 필터",
                        tint = DarkGray
                    )
                }
            }

            // 프로젝트 목록 TODO: 목록 조회 기능 생기면 구현, 임시로 1번 아이디 프로젝트만 생성해 상세 화면 이동하게 함
//            if (employeeEditState.dropDownMenu.departmentMenu.isEmpty()) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "조회된 결과가 없습니다",
//                        color = TextGray,
//                        fontSize = 15.sp
//                    )
//                }
//            }
//            else {
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        ProjectListItem(
                            projectInfo = ProjectDTO.GetProjectResponse(
                                projectName = "차세대 모니터링 시스템 구축",
                                departmentName = "개발팀",
                                managerName = "관리자",
                                businessExpense = 75000000,
                                businessStartDate = "2025-08-01",
                                businessEndDate = "2026-01-31",
                                status = "진행",
                                type = "내부"
                            ),
                            onClick = {
                                onEvent(ProjectStatusEvent.SelectedProjectWith("PRJT_000000000000001"))
                                navController.navigate("projectDetail")
                            }
                        )
                    }

                    item {
                        ProjectListItem(
                            projectInfo = ProjectDTO.GetProjectResponse(
                                projectName = "AI 기반 데이터 분석 고도화 과제",
                                departmentName = "기획팀",
                                managerName = "홍길둥",
                                businessExpense = 300000000,
                                businessStartDate = "2026-01-01",
                                businessEndDate = "2027-12-31",
                                status = "진행",
                                type = "국가과제"
                            ),
                            onClick = {
                                onEvent(ProjectStatusEvent.SelectedProjectWith("PRJT_000000000000002ㅁ"))
                                navController.navigate("projectDetail")
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(15.dp))
                    }
                }
//            }
        }
    }
}

/* 프로젝트 목록 아이템 */
@Composable
private fun ProjectListItem(projectInfo: ProjectDTO.GetProjectResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        ProjectTypeStatusBar(projectInfo.type, projectInfo.status)
        TwoInfoBar(projectInfo.projectName, "")
        TwoInfoBar(projectInfo.departmentName, projectInfo.managerName, fontSize = 15.sp)
        TwoInfoBar("${"%,d".format(projectInfo.businessExpense ?: 0)}원", "", fontSize = 14.sp)
        TwoInfoBar("${projectInfo.businessStartDate} ~ ${projectInfo.businessEndDate}", "", fontSize = 14.sp, color = TextGray)
        Spacer(modifier = Modifier.height(14.dp))
    }
}

/* 프로젝트 구분, 상태 출력 바 */
@Composable
private fun ProjectTypeStatusBar(type: String, status: String) {
    val statusColor = when (status) {
        "진행" -> MainBlue
        "미진행" -> ApprovalInfoItem_Red
        "완료" -> ApprovalInfoItem_Green
        else -> Color.Black
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = type,
            modifier = Modifier
                .background(color = MiddleBlue.copy(alpha = 0.3f), shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 15.dp, vertical = 3.dp),
            fontSize = 14.sp
        )

        Text(
            text = status,
            color = statusColor,
            modifier = Modifier
                .border(color = statusColor, width = 0.5.dp, shape = RoundedCornerShape(90.dp))
                .padding(horizontal = 15.dp, vertical = 3.dp),
            fontSize = 14.sp
        )
    }
}

/* 프로젝트 검색 필터 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectSearchBottomSheet(projectStatusState: ProjectStatusState, onEvent: (ProjectStatusEvent) -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "연도",
                options = listOf("전체", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016"),
                selected = projectStatusState.selectedYear,
                onSelected = { onEvent(ProjectStatusEvent.SelectedSearchFilterWith(ProjectStatusField.YEAR, it)) }
            )

            TwoLineDropdownEditBar(
                name = "월",
                options = listOf("전체", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"),
                selected = projectStatusState.selectedMonth,
                onSelected = { onEvent(ProjectStatusEvent.SelectedSearchFilterWith(ProjectStatusField.MONTH, it)) }
            )

            TwoLineDropdownEditBar(
                name = "부서",
                options = projectStatusState.departments.map { it.name },
                selected = projectStatusState.selectedDepartment,
                onSelected = { onEvent(ProjectStatusEvent.SelectedSearchFilterWith(ProjectStatusField.DEPARTMENT, it)) }
            )

            TwoLineDropdownEditBar(
                name = "검색 키워드",
                options = listOf("전체", "프로젝트명", "PM"),
                selected = projectStatusState.selectedKeyword,
                onSelected = { onEvent(ProjectStatusEvent.SelectedSearchFilterWith(ProjectStatusField.KEYWORD, it)) }
            )
        }
    }
}