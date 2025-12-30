package com.example.attendancemanagementapp.ui.project.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicDatePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.StartEndDateEditBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddEvent
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeInfoItem
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class BottomSheetType { NONE, DEPARTMENT, MANAGER, ASSIGNED_PERSONNEL }

/* 프로젝트 등록 화면 */
@Composable
fun ProjectAddScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onAddEvent
    val projectAddState by projectViewModel.projectAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("프로젝트 정보", "투입인력")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var bottomSheetType by remember { mutableStateOf(BottomSheetType.NONE) }   // 바텀 시트 상태 (담당부서, 프로젝트 책임자, 투입인력)

    if (bottomSheetType != BottomSheetType.NONE) {
        ProjectBottomSheet(
            bottomSheetType = bottomSheetType,
            projectAddState = projectAddState,
            onEvent = onEvent,
            onDismiss = { bottomSheetType = BottomSheetType.NONE }
        )
    }

    LaunchedEffect(Unit) {
        onEvent(ProjectAddEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "프로젝트 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
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
                            .padding(horizontal = 26.dp, vertical = 10.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 프로젝트 정보
                                ProjectAddCard(
                                    projectAddState = projectAddState,
                                    onEvent = onEvent,
                                    onOpenDepartment = { bottomSheetType = BottomSheetType.DEPARTMENT },
                                    onOpenManager = { bottomSheetType = BottomSheetType.MANAGER }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )
                                }
                            }
                            1 -> {  // 투입 현황
                                PersonnelAddCard(
                                    projectAddState = projectAddState,
                                    onOpenPersonnel = {
                                        bottomSheetType = BottomSheetType.ASSIGNED_PERSONNEL
                                    },
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
                                    )

                                    BasicButton(
                                        name = "저장",
                                        onClick = { onEvent(ProjectAddEvent.ClickedAdd) }
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.height(40.dp)
                        )
                    }
                }
            }
        }
    }
}

/* 프로젝트 등록 카드 */
@Composable
private fun ProjectAddCard(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onOpenDepartment: () -> Unit,
    onOpenManager: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "구분",
                isRequired = true,
                options = projectAddState.projectTypeOptions,
                selected = projectAddState.inputData.type,
                onSelected = { onEvent(ProjectAddEvent.SelectedTypeWith(it)) }
            )

            TwoLineEditBar(
                name = "프로젝트명",
                isRequired = true,
                value = projectAddState.inputData.projectName,
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PROJECT_NAME, it)) }
            )

            TwoLineEditBar(
                name = "주관기관",
                value = projectAddState.inputData.companyName ?: "",
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.COMPANY_NAME, it)) }
            )

            TwoLineSearchEditBar(
                name = "담당부서",
                isRequired = true,
                value = projectAddState.departmentName,
                onClick = { onOpenDepartment() }
            )

            TwoLineSearchEditBar(
                name = "프로젝트 책임자",
                isRequired = true,
                value = projectAddState.managerName,
                onClick = { onOpenManager() }
            )

            TwoLineEditBar(
                name = "사업비",
                value = projectAddState.inputData.businessExpense.toString(),
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_EXPENSE, it)) },
                onlyNumber = true
            )

            TwoLineEditBar(
                name = "회의비",
                value = projectAddState.inputData.meetingExpense.toString(),
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.MEETING_EXPENSE, it)) },
                onlyNumber = true
            )

            StartEndDateEditBar(
                name = "사업기간",
                startDate = projectAddState.inputData.businessStartDate,
                endDate = projectAddState.inputData.businessEndDate,
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_END, it)) },
                isRequired = true
            )

            StartEndDateEditBar(
                name = "계획기간",
                startDate = projectAddState.inputData.planStartDate ?: "",
                endDate = projectAddState.inputData.planEndDate ?: "",
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PLAN_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PLAN_END, it)) }
            )

            StartEndDateEditBar(
                name = "실제기간",
                startDate = projectAddState.inputData.realStartDate ?: "",
                endDate = projectAddState.inputData.realEndDate ?: "",
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REAL_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REAL_END, it)) }
            )

            TwoLineBigEditBar(
                name = "비고",
                value = projectAddState.inputData.remark ?: "",
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REMARK, it)) }
            )
        }
    }
}

/* 투입인력 등록 카드 */
@Composable
private fun PersonnelAddCard(
    projectAddState: ProjectAddState,
    onOpenPersonnel: () -> Unit,
    onEvent: (ProjectAddEvent) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("투입인력 ")
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append("*")
                        }
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onOpenPersonnel() }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "투입인력 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            if (projectAddState.inputData.assignedPersonnels.isEmpty()) {
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
                projectAddState.inputData.assignedPersonnels.forEach { personnel ->
                    PersonnelItem(
                        personnelInfo = personnel,
                        name = projectAddState.employeeState.employees.find { it.userId == personnel.chargerId }?.name ?: "",
                        typeOptions = projectAddState.personnelTypeOptions,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

/* 투입인력 목록 아이템 */
@Composable
private fun PersonnelItem(personnelInfo: ProjectDTO.AssignedPersonnelRequestInfo, name: String, typeOptions: List<String>, onEvent: (ProjectAddEvent) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropDownField(
                modifier = Modifier.weight(0.5f),
                options = typeOptions,
                selected = personnelInfo.type,
                onSelected = { onEvent(ProjectAddEvent.SelectedPersonnelTypeWith(personnelInfo.chargerId, it)) }
            )

            Divider(modifier = Modifier.fillMaxHeight().padding(horizontal = 10.dp).width(1.dp))

            BasicOutlinedTextField(
                value = name,
                enabled = false,
                onValueChange = {},
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}

/* 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectBottomSheet(
    bottomSheetType: BottomSheetType,
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        when (bottomSheetType) {
            BottomSheetType.DEPARTMENT -> {
                DepartmentBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent,
                    onDismiss = {
                        onDismiss()
                        onEvent(ProjectAddEvent.ChangedSearchValueWith(ProjectAddSearchField.DEPARTMENT, ""))
                    }
                )
            }
            BottomSheetType.MANAGER -> {
                ManagerBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent,
                    onDismiss = {
                        onDismiss()
                        onEvent(ProjectAddEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, ""))
                    }
                )
            }
            BottomSheetType.ASSIGNED_PERSONNEL -> {
                AssignedPersonnelBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent
                )
            }
            else -> {}
        }
    }
}

/* 담당부서 선택 바텀 시트 내용 */
@Composable
private fun DepartmentBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectAddState.departmentState.paginationState.isLoading && projectAddState.departmentState.paginationState.currentPage < projectAddState.departmentState.paginationState.totalPage) {
                onEvent(ProjectAddEvent.LoadNextPage(ProjectAddSearchField.DEPARTMENT))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchState = SearchState(
                value = projectAddState.departmentState.searchText,
                onValueChange = { onEvent(ProjectAddEvent.ChangedSearchValueWith(ProjectAddSearchField.DEPARTMENT, it)) },
                onClickSearch = {
                    if (projectAddState.departmentState.paginationState.currentPage <= projectAddState.departmentState.paginationState.totalPage) {
                        onEvent(ProjectAddEvent.ClickedSearchWith(ProjectAddSearchField.DEPARTMENT)) }
                    },
                onClickInit = { onEvent(ProjectAddEvent.ClickedSearchInitWith(ProjectAddSearchField.DEPARTMENT)) }
            ),
            hint = "부서명"
        )

        if (projectAddState.departmentState.departments.isEmpty()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(projectAddState.departmentState.departments) { item ->
                    DepartmentInfoItem(
                        name = item.name,
                        head = item.headName ?: "",
                        onClick = {
                            onEvent(ProjectAddEvent.SelectedDepartmentWith(item))
                            onDismiss()
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

/* 프로젝트 책임자 선택 바텀 시트 내용 */
@Composable
private fun ManagerBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectAddState.employeeState.paginationState.isLoading && projectAddState.employeeState.paginationState.currentPage < projectAddState.employeeState.paginationState.totalPage) {
                onEvent(ProjectAddEvent.LoadNextPage(ProjectAddSearchField.EMPLOYEE))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchState = SearchState(
                value = projectAddState.employeeState.searchText,
                onValueChange = { onEvent(ProjectAddEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, it)) },
                onClickSearch = {
                    if (projectAddState.employeeState.paginationState.currentPage <= projectAddState.employeeState.paginationState.totalPage) {
                        onEvent(ProjectAddEvent.ClickedSearchWith(ProjectAddSearchField.EMPLOYEE)) }
                },
                onClickInit = { onEvent(ProjectAddEvent.ClickedSearchInitWith(ProjectAddSearchField.EMPLOYEE)) }
            ),
            hint = "직원명"
        )

        if (projectAddState.employeeState.employees.isEmpty()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(projectAddState.employeeState.employees) { item ->
                    EmployeeInfoItem(
                        name = item.name,
                        deptGradeTitle = formatDeptGradeTitle(
                            item.department,
                            item.grade,
                            item.title
                        ),
                        onClick = {
                            onEvent(ProjectAddEvent.SelectedManagerWith(item))
                            onDismiss()
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

/* 투입 인력 선택 바텀 시트 내용 */
@Composable
private fun AssignedPersonnelBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectAddState.employeeState.paginationState.isLoading && projectAddState.employeeState.paginationState.currentPage < projectAddState.employeeState.paginationState.totalPage) {
                onEvent(ProjectAddEvent.LoadNextPage(ProjectAddSearchField.EMPLOYEE))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchState = SearchState(
                value = projectAddState.employeeState.searchText,
                onValueChange = { onEvent(ProjectAddEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, it)) },
                onClickSearch = {
                    if (projectAddState.employeeState.paginationState.currentPage <= projectAddState.employeeState.paginationState.totalPage) {
                        onEvent(ProjectAddEvent.ClickedSearchWith(ProjectAddSearchField.EMPLOYEE)) }
                },
                onClickInit = { onEvent(ProjectAddEvent.ClickedSearchInitWith(ProjectAddSearchField.EMPLOYEE)) }
            ),
            hint = "직원명"
        )

        if (projectAddState.employeeState.employees.isEmpty()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(projectAddState.employeeState.employees) { item ->
                    val isChecked =
                        projectAddState.inputData.assignedPersonnels.any { it.chargerId == item.userId }
                    EmployeeItem(
                        info = item,
                        isChecked = isChecked,
                        onChecked = {
                            onEvent(
                                ProjectAddEvent.CheckedAssignedPersonnelWith(
                                    it,
                                    item
                                )
                            )
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

/* 직원 목록 아이템 */
@Composable
fun EmployeeItem(
    info: EmployeeDTO.ManageEmployeesInfo,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicCheckbox(
                isChecked = isChecked,
                onChecked = { onChecked(it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = info.name,
                    fontSize = 15.sp
                )

                Text(
                    text = formatDeptGradeTitle(info.department, info.grade, info.title),
                    fontSize = 15.sp
                )
            }
        }
    }
}