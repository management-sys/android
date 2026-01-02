package com.example.attendancemanagementapp.ui.meeting.edit

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO.AttendeesInfo
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDatePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTimePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.meeting.add.AttendeeTypeDialog
import com.example.attendancemanagementapp.ui.meeting.add.EmployeeItem
import com.example.attendancemanagementapp.ui.meeting.add.ExpenseField
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddField
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.util.formatDateYY
import com.example.attendancemanagementapp.util.formatTime
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/* 회의록 수정 화면 */
@Composable
fun MeetingEditScreen(navController: NavController, meetingViewModel: MeetingViewModel) {
    val onEvent = meetingViewModel::onEditEvent
    val meetingEditState by meetingViewModel.meetingEditState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("회의록 정보", "참석자", "회의비")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "회의록 수정",
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
                                EditMeetingCard(
                                    meetingEditState = meetingEditState,
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
                                    meetingEditState = meetingEditState,
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
                                    expenses = meetingEditState.inputData.expenses,
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
                                        name = "수정",
                                        onClick = { onEvent(MeetingEditEvent.ClickedUpdate) }
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

/* 회의록 수정 카드 */
@Composable
private fun EditMeetingCard(
    meetingEditState: MeetingEditState,
    onEvent: (MeetingEditEvent) -> Unit
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
            TwoLineEditBar(
                name = "프로젝트명",
                value = meetingEditState.projectName,
                enabled = false,
                isRequired = true
            )

            TwoLineEditBar(
                name = "회의록 제목",
                value = meetingEditState.inputData.title,
                isRequired = true,
                onValueChange = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.TITLE, it)) }
            )

            StartEndDateTimeEditItem(
                startDateTime = meetingEditState.inputData.startDate,
                endDateTime = meetingEditState.inputData.endDate,
                onEvent = onEvent
            )

            TwoLineEditBar(
                name = "장소",
                value = meetingEditState.inputData.place,
                isRequired = true,
                onValueChange = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.PLACE, it)) }
            )

            TwoLineEditBar(
                name = "회의내용",
                value = meetingEditState.inputData.content ?: "",
                onValueChange = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.CONTENT, it)) }
            )

            TwoLineEditBar(
                name = "비고",
                value = meetingEditState.inputData.remark ?: "",
                onValueChange = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.REMARK, it)) }
            )
        }
    }
}

/* 일시 수정 아이템 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartEndDateTimeEditItem(
    startDateTime: String,
    endDateTime: String,
    onEvent: (MeetingEditEvent) -> Unit
) {
    var openStartDate by rememberSaveable { mutableStateOf(false) }
    var openEndDate by rememberSaveable { mutableStateOf(false) }
    var openStartTime by rememberSaveable { mutableStateOf(false) }
    var openEndTime by rememberSaveable { mutableStateOf(false) }

    if (openStartDate) {
        BasicDatePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartDate = false },
            onConfirm = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.START, it)) }
        )
    }

    if (openEndDate) {
        BasicDatePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndDate = false },
            onConfirm = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.END, it)) }
        )
    }

    if (openStartTime) {
        BasicTimePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartTime = false },
            onConfirm = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.START, it)) }
        )
    }

    if (openEndTime) {
        BasicTimePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndTime = false },
            onConfirm = { onEvent(MeetingEditEvent.ChangedValueWith(MeetingAddField.END, it)) }
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
    meetingEditState: MeetingEditState,
    onEvent: (MeetingEditEvent) -> Unit
) {
    var openSelectType by rememberSaveable { mutableStateOf(false) }
    var openSelectInternalAttendee by rememberSaveable { mutableStateOf(false) }
    var openSelectExternalAttendee by rememberSaveable { mutableStateOf(false) }

    if (openSelectType) {
        AttendeeTypeDialog(
            onDismiss = { openSelectType = false },
            onClickConfirm = {
                onEvent(MeetingEditEvent.ClickedAddInnerAttendee)
                openSelectInternalAttendee = true
            },
            onClickDismiss = { openSelectExternalAttendee = true }
        )
    }

    if (openSelectInternalAttendee) {
        EmployeeBottomSheet(
            meetingEditState = meetingEditState,
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

            meetingEditState.inputData.attendees.forEachIndexed { idx, attendee ->
                AttendeeItem(
                    attendee = attendee,
                    onClickDelete = { onEvent(MeetingEditEvent.ClickedDeleteAttendeeWith(idx)) }
                )
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

/* 참석자 목록 아이템 */
@Composable
private fun AttendeeItem(
    attendee: MeetingDTO.AttendeesInfo,
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
    expenses: List<MeetingDTO.ExpensesInfo>,
    onEvent: (MeetingEditEvent) -> Unit
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
                    onClick = { onEvent(MeetingEditEvent.ClickedAddExpense) }
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
                            onValueChange = { onEvent(MeetingEditEvent.ChangedExpenseWith(ExpenseField.TYPE, it, idx)) },
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
                                    onEvent(MeetingEditEvent.ChangedExpenseWith(ExpenseField.AMOUNT, newValue, idx))
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
                            onClick = { onEvent(MeetingEditEvent.ClickedDeleteExpenseWith(idx)) },
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

/* 직원 목록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeBottomSheet(
    meetingEditState: MeetingEditState,
    onEvent: (MeetingEditEvent) -> Unit,
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
            if (shouldLoad && !meetingEditState.employeeState.paginationState.isLoading && meetingEditState.employeeState.paginationState.currentPage < meetingEditState.employeeState.paginationState.totalPage) {
                onEvent(MeetingEditEvent.LoadNextPage)
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
                    value = meetingEditState.employeeState.searchText,
                    onValueChange = { onEvent(MeetingEditEvent.ChangedSearchValueWith(it)) },
                    onClickSearch = {
                        if (meetingEditState.employeeState.paginationState.currentPage <= meetingEditState.employeeState.paginationState.totalPage) {
                            onEvent(MeetingEditEvent.ClickedSearch) }
                    },
                    onClickInit = { onEvent(MeetingEditEvent.ClickedInitSearch) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(meetingEditState.employeeState.employees) { item ->
                    val isChecked = meetingEditState.inputData.attendees.any { it.userId == item.id }
                    EmployeeItem(
                        info = item,
                        isChecked = isChecked,
                        onChecked = { onEvent(MeetingEditEvent.CheckedAttendeeWith(it, item)) }
                    )
                }

                if (meetingEditState.employeeState.paginationState.isLoading) {
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
    onEvent: (MeetingEditEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var newAttendee by remember { mutableStateOf(AttendeesInfo(
        type = "O",
        name = "",
        grade = "",
        department = "",
        userId = null,
        id = null
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
                    onEvent(MeetingEditEvent.ClickedAddExternalAttendeeWith(newAttendee))
                    onDismiss()
                },
                enabled = newAttendee.name!!.isNotBlank() && newAttendee.department!!.isNotBlank() && newAttendee.grade!!.isNotBlank()
            )
        }
    }
}