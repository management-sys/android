package com.example.attendancemanagementapp.ui.meeting.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO.AddAttendeesInfo
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDatePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTimePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.project.add.EmployeeItem
import com.example.attendancemanagementapp.ui.project.add.ProjectAddEvent
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDateYY
import com.example.attendancemanagementapp.util.formatTime
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/* 회의록 등록 화면 */
@Composable
fun MeetingAddScreen(navController: NavController, meetingViewModel: MeetingViewModel) {
    val onEvent = meetingViewModel::onAddEvent
    val meetingAddState by meetingViewModel.meetingAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("회의록 정보", "참석자", "회의비")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onEvent(MeetingAddEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "회의록 등록",
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
                            0 -> {  // 회의록 정보
                                AddMeetingCard(
                                    meetingAddState = meetingAddState,
                                    onEvent = onEvent
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
                            1 -> {  // 참석자
                                AttendeeEditCard(
                                    meetingAddState = meetingAddState,
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
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                                    )
                                }
                            }
                            2 -> {  // 회의비
                                ExpenseEditCard(
                                    expenses = meetingAddState.inputData.expenses,
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )

                                    BasicButton(
                                        name = "저장",
                                        onClick = { onEvent(MeetingAddEvent.ClickedAdd) }
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

    DisposableEffect(Unit) {
        onDispose {
            onEvent(MeetingAddEvent.InitLast)
        }
    }
}

/* 회의록 등록 카드 */
@Composable
private fun AddMeetingCard(
    meetingAddState: MeetingAddState,
    onEvent: (MeetingAddEvent) -> Unit
) {
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        ProjectBottomSheet(
            meetingAddState = meetingAddState,
            onEvent = onEvent,
            onDismiss = { openSheet = false }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (meetingAddState.fixedProject) {
                TwoLineEditBar(
                    name = "프로젝트명",
                    value = meetingAddState.projectName,
                    enabled = false,
                    isRequired = true
                )
            } else {
                TwoLineSearchEditBar(
                    name = "프로젝트명",
                    value = meetingAddState.projectName,
                    onClick = { openSheet = true },
                    isRequired = true,
                    enabled = false
                )
            }

            TwoLineEditBar(
                name = "회의록 제목",
                value = meetingAddState.inputData.title,
                isRequired = true,
                onValueChange = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.TITLE, it)) }
            )

            StartEndDateTimeEditItem(
                startDateTime = meetingAddState.inputData.startDate,
                endDateTime = meetingAddState.inputData.endDate,
                onEvent = onEvent
            )

            TwoLineEditBar(
                name = "장소",
                value = meetingAddState.inputData.place,
                isRequired = true,
                onValueChange = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.PLACE, it)) }
            )

            TwoLineEditBar(
                name = "회의내용",
                value = meetingAddState.inputData.content ?: "",
                onValueChange = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.CONTENT, it)) }
            )

            TwoLineEditBar(
                name = "비고",
                value = meetingAddState.inputData.remark ?: "",
                onValueChange = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.REMARK, it)) }
            )
        }
    }
}

/* 프로젝트 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectBottomSheet(
    meetingAddState: MeetingAddState,
    onEvent: (MeetingAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !meetingAddState.projectState.paginationState.isLoading && meetingAddState.projectState.paginationState.currentPage < meetingAddState.projectState.paginationState.totalPage) {
                onEvent(MeetingAddEvent.LoadNextEmployeePage)
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = meetingAddState.projectState.searchText,
                    onValueChange = { onEvent(MeetingAddEvent.ChangedProjectSearchValueWith(it)) },
                    onClickSearch = {
                        if (meetingAddState.projectState.paginationState.currentPage <= meetingAddState.projectState.paginationState.totalPage) {
                            onEvent(MeetingAddEvent.ClickedProjectSearch)
                        }
                    },
                    onClickInit = { onEvent(MeetingAddEvent.ClickedProjectInitSearch) }
                ),
                hint = "프로젝트명"
            )

            if (meetingAddState.projectState.projects.isEmpty()) {
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    items(meetingAddState.projectState.projects) { projectInfo ->
                        ProjectInfoItem(
                            projectInfo = projectInfo,
                            onClick = {
                                onEvent(MeetingAddEvent.SelectedProjectWith(projectInfo.projectId, projectInfo.projectName))
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
}

/* 프로젝트 목록 아이템 */
@Composable
private fun ProjectInfoItem(projectInfo: ProjectDTO.ProjectStatusInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(projectInfo.projectName, "")
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/* 일시 수정 아이템 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartEndDateTimeEditItem(
    startDateTime: String,
    endDateTime: String,
    onEvent: (MeetingAddEvent) -> Unit
) {
    var openStartDate by rememberSaveable { mutableStateOf(false) }
    var openEndDate by rememberSaveable { mutableStateOf(false) }
    var openStartTime by rememberSaveable { mutableStateOf(false) }
    var openEndTime by rememberSaveable { mutableStateOf(false) }

    if (openStartDate) {
        BasicDatePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartDate = false },
            onConfirm = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.START, it)) }
        )
    }

    if (openEndDate) {
        BasicDatePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndDate = false },
            onConfirm = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.END, it)) }
        )
    }

    if (openStartTime) {
        BasicTimePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartTime = false },
            onConfirm = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.START, it)) }
        )
    }

    if (openEndTime) {
        BasicTimePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndTime = false },
            onConfirm = { onEvent(MeetingAddEvent.ChangedValueWith(MeetingAddField.END, it)) }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append("일시 ")
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append("*")
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formatDateYY(startDateTime),
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(5.dp),
                    colors = BasicOutlinedTextFieldColors(),
                    placeholder = {
                        Text(
                            text = startDateTime.ifBlank { "연도-월-일" },
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "캘린더 열기",
                            modifier =
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { openStartDate = true }
                        )
                    }
                )

                OutlinedTextField(
                    value = formatTime(startDateTime),
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(5.dp),
                    colors = BasicOutlinedTextFieldColors(),
                    placeholder = {
                        Text(
                            text = startDateTime.ifBlank { "시간:분" },
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "시간 선택 팝업 열기",
                            modifier =
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { openStartTime = true }
                        )
                    }
                )
            }

            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "~",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = formatDateYY(endDateTime),
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(5.dp),
                    colors = BasicOutlinedTextFieldColors(),
                    placeholder = {
                        Text(
                            text = endDateTime.ifBlank { "연도-월-일" },
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "캘린더 열기",
                            modifier =
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { openEndDate = true }
                        )
                    }
                )

                OutlinedTextField(
                    value = formatTime(endDateTime),
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    shape = RoundedCornerShape(5.dp),
                    colors = BasicOutlinedTextFieldColors(),
                    placeholder = {
                        Text(
                            text = endDateTime.ifBlank { "시간:분" },
                            fontSize = 14.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "시간 선택 팝업 열기",
                            modifier =
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { openEndTime = true }
                        )
                    }
                )
            }
        }
    }
}

/* 참석자 정보 수정 카드 */
@Composable
private fun AttendeeEditCard(
    meetingAddState: MeetingAddState,
    onEvent: (MeetingAddEvent) -> Unit
) {
    var openSelectType by rememberSaveable { mutableStateOf(false) }
    var openSelectInternalAttendee by rememberSaveable { mutableStateOf(false) }
    var openSelectExternalAttendee by rememberSaveable { mutableStateOf(false) }

    if (openSelectType) {
        AttendeeTypeDialog(
            onDismiss = { openSelectType = false },
            onClickConfirm = { openSelectInternalAttendee = true },
            onClickDismiss = { openSelectExternalAttendee = true }
        )
    }

    if (openSelectInternalAttendee) {
        EmployeeBottomSheet(
            meetingAddState = meetingAddState,
            onEvent = onEvent,
            onDismiss = { openSelectInternalAttendee = false }
        )
    }

    if (openSelectExternalAttendee) {
        ExternalAttendeeBottomSheet(
            onEvent = onEvent,
            onDismiss = { openSelectExternalAttendee = false }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "참석자",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { openSelectType = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "참석자 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            meetingAddState.inputData.attendees.forEachIndexed { idx, attendee ->
                AttendeeItem(
                    attendee = attendee,
                    onClickDelete = { onEvent(MeetingAddEvent.ClickedDeleteAttendeeWith(idx)) }
                )
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

/* 참석자 구분 선택 디알로그 */
@Composable
fun AttendeeTypeDialog(
    onDismiss: () -> Unit,
    onClickConfirm: () -> Unit,
    onClickDismiss: () -> Unit = onDismiss
) {
    AlertDialog(
        title = {
            Text(
                text = "참석자 구분을 선택해주세요",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickConfirm()
                    onDismiss()
                }
            ) {
                Text(
                    text = "내부",
                    fontSize = 16.sp,
                    color = MainBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClickDismiss()
                    onDismiss()
                }
            ) {
                Text(
                    text = "외부",
                    fontSize = 16.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = Color.White
    )
}

/* 직원 목록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeBottomSheet(
    meetingAddState: MeetingAddState,
    onEvent: (MeetingAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !meetingAddState.employeeState.paginationState.isLoading && meetingAddState.employeeState.paginationState.currentPage < meetingAddState.employeeState.paginationState.totalPage) {
                onEvent(MeetingAddEvent.LoadNextEmployeePage)
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = meetingAddState.employeeState.searchText,
                    onValueChange = { onEvent(MeetingAddEvent.ChangedEmployeeSearchValueWith(it)) },
                    onClickSearch = {
                        if (meetingAddState.employeeState.paginationState.currentPage <= meetingAddState.employeeState.paginationState.totalPage) {
                            onEvent(MeetingAddEvent.ClickedEmployeeSearch)
                        }
                    },
                    onClickInit = { onEvent(MeetingAddEvent.ClickedEmployeeInitSearch) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(meetingAddState.employeeState.employees) { item ->
                    val isChecked = meetingAddState.inputData.attendees.any { it.userId == item.userId }
                    EmployeeItem(
                        info = item,
                        isChecked = isChecked,
                        onChecked = { onEvent(MeetingAddEvent.CheckedAttendeeWith(it, item)) }
                    )
                }

                if (meetingAddState.employeeState.paginationState.isLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

/* 외부 직원 등록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExternalAttendeeBottomSheet(
    onEvent: (MeetingAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var newAttendee by remember { mutableStateOf(AddAttendeesInfo(
        type = "O",
        name = "",
        grade = "",
        department = "",
        userId = null
    )) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "외부 참석자 정보를 입력해주세요.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            TwoLineEditBar(
                name = "이름",
                value = newAttendee.name ?: "",
                onValueChange = { newAttendee = newAttendee.copy(name = it) },
                isRequired = true
            )

            TwoLineEditBar(
                name = "소속",
                value = newAttendee.department ?: "",
                onValueChange = { newAttendee = newAttendee.copy(department = it) },
                isRequired = true
            )

            TwoLineEditBar(
                name = "직위",
                value = newAttendee.grade ?: "",
                onValueChange = { newAttendee = newAttendee.copy(grade = it) },
                isRequired = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            BasicLongButton(
                name = "추가",
                onClick = {
                    onEvent(MeetingAddEvent.ClickedAddExternalAttendeeWith(newAttendee))
                    onDismiss()
                },
                enabled = newAttendee.name!!.isNotBlank() && newAttendee.department!!.isNotBlank() && newAttendee.grade!!.isNotBlank()
            )
        }
    }
}

/* 참석자 목록 아이템 */
@Composable
private fun AttendeeItem(
    attendee: AddAttendeesInfo,
    onClickDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (attendee.type == "I") "내부" else if (attendee.type == "O") "외부" else "",
                    fontSize = 15.sp
                )

                IconButton(
                    onClick = { onClickDelete() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "참석자 아이템 삭제 아이콘",
                        modifier = Modifier.size(20.dp),
                        tint = ApprovalInfoItem_Red
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${attendee.department}/${attendee.grade}",
                    fontSize = 15.sp
                )

                Text(
                    text = attendee.name ?: "",
                    fontSize = 15.sp
                )
            }
        }
    }
}

/* 회의비 정보 수정 카드 */
@Composable
private fun ExpenseEditCard(
    expenses: List<MeetingDTO.AddExpensesInfo>,
    onEvent: (MeetingAddEvent) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "회의비",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onEvent(MeetingAddEvent.ClickedAddExpense) }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "회의비 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            expenses.forEachIndexed { idx, expense ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "•   ",
                        fontSize = 16.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(0.6f),
                            value = expense.type,
                            onValueChange = { onEvent(MeetingAddEvent.ChangedExpenseWith(ExpenseField.TYPE, it, idx)) },
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = DarkGray,
                                focusedBorderColor = DarkGray,
                                disabledContainerColor = DisableGray,
                                disabledBorderColor = DarkGray,
                                disabledTextColor = DarkGray
                            ),
                            placeholder = {
                                Text(
                                    text = "여비내역입력",
                                    fontSize = 16.sp
                                )
                            }
                        )

                        OutlinedTextField(
                            modifier = Modifier.weight(0.3f),
                            value = if (expense.amount == 0) "" else "${expense.amount}",
                            onValueChange = { newValue ->
                                if (newValue.length <= 9 && newValue.matches(Regex("^[0-9]*$"))) {
                                    onEvent(MeetingAddEvent.ChangedExpenseWith(ExpenseField.AMOUNT, newValue, idx))
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = DarkGray,
                                focusedBorderColor = DarkGray,
                                disabledContainerColor = DisableGray,
                                disabledBorderColor = DarkGray,
                                disabledTextColor = DarkGray
                            ),
                            placeholder = {
                                Text(
                                    text = "금액",
                                    fontSize = 16.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        IconButton(
                            onClick = { onEvent(MeetingAddEvent.ClickedDeleteExpenseWith(idx)) },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "회의비 아이템 삭제 버튼"
                            )
                        }
                    }
                }
            }
        }
    }
}