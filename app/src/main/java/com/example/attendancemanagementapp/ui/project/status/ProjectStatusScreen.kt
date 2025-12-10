package com.example.attendancemanagementapp.ui.project.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.param.ProjectStatusSearchType
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.home.calendar.AttendanceInfoItem
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Blue
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 프로젝트 현황 화면 */
@Composable
fun ProjectStatusScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onStatusEvent
    val projectStatusState by projectViewModel.projectStatusState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(ProjectStatusEvent.InitFirst)
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
            ProjectStatusInfo(
                projectStatusState = projectStatusState
            )

            ProjectList(
                projectStatusState = projectStatusState,
                onEvent = onEvent,
                navController = navController
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(ProjectStatusEvent.InitLast)
        }
    }
}

/* 프로젝트 진행 현황 */
@Composable
private fun ProjectStatusInfo(projectStatusState: ProjectStatusState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AttendanceInfoItem(count = projectStatusState.cntStatus.total, name = "전체", backColor = AttendanceInfoItem_Gray)
            AttendanceInfoItem(count = projectStatusState.cntStatus.inProgress, name = "진행중", backColor = MainBlue)
            AttendanceInfoItem(count = projectStatusState.cntStatus.notStarted, name = "미진행", backColor = ApprovalInfoItem_Red)
            AttendanceInfoItem(count = projectStatusState.cntStatus.completed, name = "완료", backColor = AttendanceInfoItem_Blue)
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

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectStatusState.paginationState.isLoading && projectStatusState.paginationState.currentPage < projectStatusState.paginationState.totalPage) {
                onEvent(ProjectStatusEvent.LoadNextPage)
            }
        }
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
                        value = projectStatusState.filter.searchText,
                        onValueChange = { onEvent(ProjectStatusEvent.ChangedSearchTextWith(it)) },
                        onClickSearch = {
                            // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                            if (projectStatusState.paginationState.currentPage <= projectStatusState.paginationState.totalPage) {
                                onEvent(ProjectStatusEvent.ClickedSearch)
                                keyboardController?.hide()
                                focusManager.clearFocus(force = true)
                            }
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
                        tint = if (projectStatusState.filter.year != 0 || projectStatusState.filter.month != 0 || projectStatusState.filter.departmentId.isNotBlank() || projectStatusState.filter.searchType.label != "전체") MainBlue else DarkGray
                    )
                }
            }

            if (projectStatusState.departments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    items(projectStatusState.projects) { projectInfo ->
                        ProjectListItem(
                            projectInfo = projectInfo,
                            onClick = {
                                onEvent(ProjectStatusEvent.ClickedProjectWith(projectInfo.projectId))
                                navController.navigate("projectDetail")
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

/* 프로젝트 목록 아이템 */
@Composable
private fun ProjectListItem(projectInfo: ProjectDTO.ProjectStatusInfo, onClick: () -> Unit) {
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
    var filter by remember { mutableStateOf(projectStatusState.filter) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        val yearList = listOf("전체") + (2016..2025).map { it.toString() }
        val monthList = listOf("전체") + (1..12).map { "${it}월" }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "연도",
                options = yearList,
                selected = if (filter.year == 0) "전체" else filter.year.toString(),
                onSelected = { filter = filter.copy(year = it.toInt()) }
            )

            TwoLineDropdownEditBar(
                name = "월",
                options = monthList,
                selected = if (filter.month == 0) "전체" else "${filter.month}월",
                onSelected = { filter = filter.copy(month = it.replace("월", "").toInt()) }
            )

            TwoLineDropdownEditBar(
                name = "부서",
                options = projectStatusState.departments.map { it.name },
                selected = if (filter.departmentId == "") "전체" else projectStatusState.departments.find { it.id == filter.departmentId }?.name ?: "",
                onSelected = { filter = filter.copy(departmentId = projectStatusState.departments.find { department -> department.name == it }?.id ?: "") }
            )

            TwoLineDropdownEditBar(
                name = "검색 키워드",
                options = ProjectStatusSearchType.entries.map { it.label },
                selected = filter.searchType.label,
                onSelected = { filter = filter.copy(searchType = ProjectStatusSearchType.fromLabel(it)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubButton(
                    name = "초기화",
                    onClick = {
                        onEvent(ProjectStatusEvent.ClickedInitFilter)
                        onDismiss()
                    }
                )

                BasicButton(
                    name = "적용",
                    onClick = {
                        onEvent(ProjectStatusEvent.ClickedUseFilter(filter = filter))
                        onDismiss()
                    }
                )
            }
        }
    }
}
