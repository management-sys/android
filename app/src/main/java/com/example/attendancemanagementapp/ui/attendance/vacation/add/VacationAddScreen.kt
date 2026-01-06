package com.example.attendancemanagementapp.ui.attendance.vacation.add

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDatePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTimePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.project.add.EmployeeItem
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDateYY
import com.example.attendancemanagementapp.util.formatTime
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 휴가 신청 화면 */
@Composable
fun VacationAddScreen(navController: NavController, vacationViewModel: VacationViewModel) {
    val onEvent = vacationViewModel::onAddEvent
    val vacationAddState by vacationViewModel.vacationAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    LaunchedEffect(Unit) {
        onEvent(VacationAddEvent.InitWith(vacationViewModel.userId))
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "휴가 신청",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AddVacationCard(
                vacationAddState = vacationAddState,
                onEvent = onEvent
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SubButton(
                    name = "이전 승인자 불러오기",
                    wrapContent = true,
                    onClick = { onEvent(VacationAddEvent.ClickedGetPrevApprover) }
                )

                BasicButton(
                    name = "신청",
                    onClick = { onEvent(VacationAddEvent.ClickedAdd) }
                )
            }
        }
    }
}

/* 휴가 신청 수정 카드 */
@Composable
private fun AddVacationCard(vacationAddState: VacationAddState, onEvent: (VacationAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 승인자 선택 바텀 시트

    if (openSheet) {
        ApproverBottomSheet(
            vacationAddState = vacationAddState,
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
            TwoLineDropdownEditBar(
                name = "유형",
                isRequired = true,
                options = listOf("연차", "반차"),
                selected = vacationAddState.inputData.type,
                onSelected = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.TYPE, it)) }
            )

            StartEndDateTimeEditItem(
                startDateTime = vacationAddState.inputData.startDate,
                endDateTime = vacationAddState.inputData.endDate,
                onEvent = onEvent
            )

            TwoLineSearchEditBar(
                name = "승인자",
                value = vacationAddState.employeeState.employees
                    .filter { it.userId in vacationAddState.inputData.approverIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = true },
                isRequired = true
            )

            TwoLineBigEditBar(
                name = "세부사항",
                value = vacationAddState.inputData.detail,
                onValueChange = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.DETAIL, it)) }
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
    onEvent: (VacationAddEvent) -> Unit
) {
    var openStartDate by rememberSaveable { mutableStateOf(false) }
    var openEndDate by rememberSaveable { mutableStateOf(false) }
    var openStartTime by rememberSaveable { mutableStateOf(false) }
    var openEndTime by rememberSaveable { mutableStateOf(false) }

    if (openStartDate) {
        BasicDatePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartDate = false },
            onConfirm = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.START, it)) }
        )
    }

    if (openEndDate) {
        BasicDatePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndDate = false },
            onConfirm = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.END, it)) }
        )
    }

    if (openStartTime) {
        BasicTimePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartTime = false },
            onConfirm = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.START, it)) }
        )
    }

    if (openEndTime) {
        BasicTimePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndTime = false },
            onConfirm = { onEvent(VacationAddEvent.ChangedValueWith(VacationAddField.END, it)) }
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

/* 승인자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproverBottomSheet(
    vacationAddState: VacationAddState,
    onEvent: (VacationAddEvent) -> Unit,
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
            if (shouldLoad && !vacationAddState.employeeState.paginationState.isLoading && vacationAddState.employeeState.paginationState.currentPage < vacationAddState.employeeState.paginationState.totalPage) {
                onEvent(VacationAddEvent.LoadNextPage)
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
                    value = vacationAddState.employeeState.searchText,
                    onValueChange = { onEvent(VacationAddEvent.ChangedSearchValueWith(it)) },
                    onClickSearch = {
                        if (vacationAddState.employeeState.paginationState.currentPage <= vacationAddState.employeeState.paginationState.totalPage) {
                            onEvent(VacationAddEvent.ClickedSearch)
                        }
                    },
                    onClickInit = { onEvent(VacationAddEvent.ClickedSearchInit) }
                ),
                hint = "직원명"
            )

            if (vacationAddState.employeeState.employees.isEmpty()) {
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
                    items(vacationAddState.employeeState.employees) { item ->
                        val isChecked =
                            vacationAddState.inputData.approverIds.any { it == item.userId }

                        EmployeeItem(
                            info = item,
                            isChecked = isChecked,
                            onChecked = {
                                onEvent(
                                    VacationAddEvent.SelectedApproverWith(
                                        it,
                                        item.userId
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
}