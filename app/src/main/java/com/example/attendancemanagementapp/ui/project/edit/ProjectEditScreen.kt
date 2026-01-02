package com.example.attendancemanagementapp.ui.project.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
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
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeInfoItem
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.project.add.BottomSheetType
import com.example.attendancemanagementapp.ui.project.add.EmployeeItem
import com.example.attendancemanagementapp.ui.project.add.ProjectAddField
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/* 프로젝트 수정 화면 */
@Composable
fun ProjectEditScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onEditEvent
    val projectEditState by projectViewModel.projectEditState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("프로젝트 정보", "투입인력")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var bottomSheetType by remember { mutableStateOf(BottomSheetType.NONE) }   // 바텀 시트 상태 (담당부서, 프로젝트 책임자, 투입인력)

    if (bottomSheetType != BottomSheetType.NONE) {
        ProjectBottomSheet(
            bottomSheetType = bottomSheetType,
            projectEditState = projectEditState,
            onEvent = onEvent,
            onDismiss = { bottomSheetType = BottomSheetType.NONE }
        )
    }

    LaunchedEffect(Unit) {
        onEvent(ProjectEditEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "프로젝트 수정",
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
                                ProjectInfoEditCard(
                                    projectEditState = projectEditState,
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

                            1 -> {  // 회의록 정보
                                PersonnelEditCard(
                                    projectEditState = projectEditState,
                                    onOpenPersonnel = { bottomSheetType = BottomSheetType.ASSIGNED_PERSONNEL },
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
                                        name = "수정",
                                        onClick = { onEvent(ProjectEditEvent.ClickedUpdate) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 프로젝트 정보 수정 카드 */
@Composable
private fun ProjectInfoEditCard(
    projectEditState: ProjectEditState,
    onEvent: (ProjectEditEvent) -> Unit,
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
                options = projectEditState.projectTypeOptions,
                selected = projectEditState.inputData.type,
                onSelected = { onEvent(ProjectEditEvent.SelectedTypeWith(it)) }
            )

            TwoLineEditBar(
                name = "프로젝트명",
                isRequired = true,
                value = projectEditState.inputData.projectName,
                onValueChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.PROJECT_NAME, it)) }
            )

            TwoLineEditBar(
                name = "주관기관",
                value = projectEditState.inputData.companyName ?: "",
                onValueChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.COMPANY_NAME, it)) }
            )

            TwoLineSearchEditBar(
                name = "담당부서",
                isRequired = true,
                value = projectEditState.departmentName,
                onClick = { onOpenDepartment() }
            )

            TwoLineSearchEditBar(
                name = "프로젝트 책임자",
                isRequired = true,
                value = projectEditState.managerName,
                onClick = { onOpenManager() }
            )

            TwoLineEditBar(
                name = "사업비",
                value = projectEditState.inputData.businessExpense.toString(),
                onValueChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.BUSINESS_EXPENSE, it)) },
                onlyNumber = true
            )

            TwoLineEditBar(
                name = "회의비",
                value = projectEditState.inputData.meetingExpense.toString(),
                onValueChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.MEETING_EXPENSE, it)) },
                onlyNumber = true
            )

            StartEndDateEditBar(
                name = "사업기간",
                startDate = projectEditState.inputData.businessStartDate,
                endDate = projectEditState.inputData.businessEndDate,
                onStartChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.BUSINESS_START, it)) },
                onEndChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.BUSINESS_END, it)) },
                isRequired = true
            )

            StartEndDateEditBar(
                name = "계획기간",
                startDate = projectEditState.inputData.planStartDate ?: "",
                endDate = projectEditState.inputData.planEndDate ?: "",
                onStartChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.PLAN_START, it)) },
                onEndChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.PLAN_END, it)) }
            )

            StartEndDateEditBar(
                name = "실제기간",
                startDate = projectEditState.inputData.realStartDate ?: "",
                endDate = projectEditState.inputData.realEndDate ?: "",
                onStartChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.REAL_START, it)) },
                onEndChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.REAL_END, it)) }
            )

            TwoLineBigEditBar(
                name = "비고",
                value = projectEditState.inputData.remark ?: "",
                onValueChange = { onEvent(ProjectEditEvent.ChangedValueWith(ProjectAddField.REMARK, it)) }
            )
        }
    }
}

/* 투입인력 등록 카드 */
@Composable
private fun PersonnelEditCard(
    projectEditState: ProjectEditState,
    onOpenPersonnel: () -> Unit,
    onEvent: (ProjectEditEvent) -> Unit
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

            if (projectEditState.inputData.assignedPersonnels.isEmpty()) {
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
                projectEditState.inputData.assignedPersonnels.forEach { personnel ->
                    PersonnelItem(
                        personnelInfo = personnel,
                        name = personnel.name,
//                        name = projectEditState.employeeState.employees.find { it.userId == personnel.managerId }?.name ?: "",
                        typeOptions = projectEditState.personnelTypeOptions,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

/* 투입인력 목록 아이템 */
@Composable
private fun PersonnelItem(
    personnelInfo: ProjectDTO.AssignedPersonnelInfo,
    name: String,
    typeOptions: List<String>,
    onEvent: (ProjectEditEvent) -> Unit
) {
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
                onSelected = { onEvent(ProjectEditEvent.SelectedPersonnelTypeWith(personnelInfo.managerId, it)) }
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
    projectEditState: ProjectEditState,
    onEvent: (ProjectEditEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

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
                    projectEditState = projectEditState,
                    onEvent = onEvent,
                    onDismiss = {
                        onDismiss()
                        onEvent(ProjectEditEvent.ChangedSearchValueWith(ProjectAddSearchField.DEPARTMENT, ""))
                    }
                )
            }
            BottomSheetType.MANAGER -> {
                ManagerBottomSheetContent(
                    projectEditState = projectEditState,
                    onEvent = onEvent,
                    onDismiss = {
                        onDismiss()
                        onEvent(ProjectEditEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, ""))
                    }
                )
            }
            BottomSheetType.ASSIGNED_PERSONNEL -> {
                AssignedPersonnelBottomSheetContent(
                    projectEditState = projectEditState,
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
    projectEditState: ProjectEditState,
    onEvent: (ProjectEditEvent) -> Unit,
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
            if (shouldLoad && !projectEditState.departmentState.paginationState.isLoading && projectEditState.departmentState.paginationState.currentPage < projectEditState.departmentState.paginationState.totalPage) {
                onEvent(ProjectEditEvent.LoadNextPage(ProjectAddSearchField.DEPARTMENT))
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
                value = projectEditState.departmentState.searchText,
                onValueChange = { onEvent(ProjectEditEvent.ChangedSearchValueWith(ProjectAddSearchField.DEPARTMENT, it)) },
                onClickSearch = {
                    if (projectEditState.departmentState.paginationState.currentPage <= projectEditState.departmentState.paginationState.totalPage) {
                        onEvent(ProjectEditEvent.ClickedSearchWith(ProjectAddSearchField.DEPARTMENT)) }
                },
                onClickInit = { onEvent(ProjectEditEvent.ClickedSearchInitWith(ProjectAddSearchField.DEPARTMENT)) }
            ),
            hint = "부서명"
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = listState
        ) {
            if (projectEditState.departmentState.departments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "조회된 결과가 없습니다",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(projectEditState.departmentState.departments) { item ->
                    DepartmentInfoItem(
                        name = item.name,
                        head = item.headName ?: "",
                        onClick = {
                            onEvent(ProjectEditEvent.SelectedDepartmentWith(item))
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
    projectEditState: ProjectEditState,
    onEvent: (ProjectEditEvent) -> Unit,
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
            if (shouldLoad && !projectEditState.employeeState.paginationState.isLoading && projectEditState.employeeState.paginationState.currentPage < projectEditState.employeeState.paginationState.totalPage) {
                onEvent(ProjectEditEvent.LoadNextPage(ProjectAddSearchField.EMPLOYEE))
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
                value = projectEditState.employeeState.searchText,
                onValueChange = { onEvent(ProjectEditEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, it)) },
                onClickSearch = {
                    if (projectEditState.employeeState.paginationState.currentPage <= projectEditState.employeeState.paginationState.totalPage) {
                        onEvent(ProjectEditEvent.ClickedSearchWith(ProjectAddSearchField.EMPLOYEE)) }
                },
                onClickInit = { onEvent(ProjectEditEvent.ClickedSearchInitWith(ProjectAddSearchField.EMPLOYEE)) }
            ),
            hint = "직원명"
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = listState
        ) {
            if (projectEditState.employeeState.employees.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "조회된 결과가 없습니다",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(projectEditState.employeeState.employees) { item ->
                    EmployeeInfoItem(
                        name = item.name,
                        deptGradeTitle = formatDeptGradeTitle(
                            item.department,
                            item.grade,
                            item.title
                        ),
                        onClick = {
                            onEvent(ProjectEditEvent.SelectedManagerWith(item))
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
    projectEditState: ProjectEditState,
    onEvent: (ProjectEditEvent) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectEditState.employeeState.paginationState.isLoading && projectEditState.employeeState.paginationState.currentPage < projectEditState.employeeState.paginationState.totalPage) {
                onEvent(ProjectEditEvent.LoadNextPage(ProjectAddSearchField.EMPLOYEE))
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
                value = projectEditState.employeeState.searchText,
                onValueChange = { onEvent(ProjectEditEvent.ChangedSearchValueWith(ProjectAddSearchField.EMPLOYEE, it)) },
                onClickSearch = {
                    if (projectEditState.employeeState.paginationState.currentPage <= projectEditState.employeeState.paginationState.totalPage) {
                        onEvent(ProjectEditEvent.ClickedSearchWith(ProjectAddSearchField.EMPLOYEE)) }
                },
                onClickInit = { onEvent(ProjectEditEvent.ClickedSearchInitWith(ProjectAddSearchField.EMPLOYEE)) }
            ),
            hint = "직원명"
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = listState
        ) {
            if (projectEditState.employeeState.employees.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "조회된 결과가 없습니다",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(projectEditState.employeeState.employees) { item ->
                    val isChecked =
                        projectEditState.inputData.assignedPersonnels.any { it.managerId == item.userId }
                    EmployeeItem(
                        info = item,
                        isChecked = isChecked,
                        onChecked = {
                            onEvent(
                                ProjectEditEvent.CheckedAssignedPersonnelWith(
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