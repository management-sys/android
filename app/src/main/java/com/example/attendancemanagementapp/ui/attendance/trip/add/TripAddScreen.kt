package com.example.attendancemanagementapp.ui.attendance.trip.add

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.asset.car.edit.ManagerInfoItem
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicDatePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTimePickerDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.project.add.EmployeeItem
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDateYY
import com.example.attendancemanagementapp.util.formatTime
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/* 출장 신청 화면 */
@Composable
fun TripAddScreen(navController: NavController,  tripViewModel: TripViewModel) {
    val onEvent = tripViewModel::onAddEvent
    val tripAddState by tripViewModel.tripAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("기본정보", "카드정보", "차량정보")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onEvent(TripAddEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장 신청",
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
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 기본 정보
                                AddTripCard(
                                    tripAddState = tripAddState,
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전 승인자 불러오기",
                                        wrapContent = true,
                                        onClick = { onEvent(TripAddEvent.ClickedGetPrevApprover) }
                                    )

                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )
                                }
                            }
                            1 -> {  // 카드 정보
                                AddCardCard(
                                    tripAddState = tripAddState,
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
                            2 -> {  // 차량 정보
                                AddCarCard(
                                    tripAddState = tripAddState,
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
                                        onClick = { onEvent(TripAddEvent.ClickedAdd) }
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

/* 출장 신청 수정 카드 */
@Composable
private fun AddTripCard(tripAddState: TripAddState, onEvent: (TripAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf("") } // 바텀 시트

    if (openSheet.isNotBlank()) {
        when (openSheet) {
            "Attendee" -> {
                AttendeeApproverBottomSheet(
                    tripAddState = tripAddState,
                    field = TripSearchField.ATTENDEE,
                    onEvent = onEvent,
                    onDismiss = {
                        onEvent(TripAddEvent.ClickedSearchInitWith(TripSearchField.ATTENDEE))
                        openSheet = ""
                    }
                )
            }
            "Approver" -> {
                AttendeeApproverBottomSheet(
                    tripAddState = tripAddState,
                    field = TripSearchField.APPROVER,
                    onEvent = onEvent,
                    onDismiss = {
                        onEvent(TripAddEvent.ClickedSearchInitWith(TripSearchField.APPROVER))
                        openSheet = ""
                    }
                )
            }
        }
    }

    val dates = if (tripAddState.inputData.startDate == "" || tripAddState.inputData.endDate == "") "0" else {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val startDate = LocalDateTime.parse(tripAddState.inputData.startDate, formatter).toLocalDate()
        val endDate = LocalDateTime.parse(tripAddState.inputData.endDate, formatter).toLocalDate()

        (ChronoUnit.DAYS.between(startDate, endDate) + 1).toString()
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
                name = "출장구분",
                isRequired = true,
                options = tripAddState.tripTypeNames,
                selected = tripAddState.inputData.type,
                onSelected = { onEvent(TripAddEvent.SelectedTypeWith(it)) }
            )

            TwoLineEditBar(
                name = "출장지",
                isRequired = true,
                value = tripAddState.inputData.place,
                onValueChange = { onEvent(TripAddEvent.ChangedValueWith(TripAddField.PLACE, it)) }
            )

            TwoLineEditBar(
                name = "출장목적",
                isRequired = true,
                value = tripAddState.inputData.purpose,
                onValueChange = { onEvent(TripAddEvent.ChangedValueWith(TripAddField.PURPOSE, it)) }
            )

            StartEndDateTimeEditItem(
                startDateTime = tripAddState.inputData.startDate,
                endDateTime = tripAddState.inputData.endDate,
                onStartChanged = { onEvent(TripAddEvent.ChangedValueWith(TripAddField.START, it)) },
                onEndChanged = { onEvent(TripAddEvent.ChangedValueWith(TripAddField.END, it)) }
            )

            TwoLineEditBar(
                name = "일수",
                value = dates,
                onValueChange = {},
                enabled = false
            )

            TwoLineSearchEditBar(
                name = "동행자",
                value = tripAddState.employeeState.employees
                    .filter { it.userId in tripAddState.inputData.attendeeIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = "Attendee" }
            )

            TwoLineSearchEditBar(
                name = "승인자",
                value = tripAddState.employeeState.employees
                    .filter { it.userId in tripAddState.inputData.approverIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = "Approver" },
                isRequired = true
            )

            TwoLineBigEditBar(
                name = "품의내용",
                value = tripAddState.inputData.content,
                onValueChange = { onEvent(TripAddEvent.ChangedValueWith(TripAddField.CONTENT, it)) },
                isRequired = true
            )
        }
    }
}

/* 일시 수정 아이템 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartEndDateTimeEditItem(
    startDateTime: String,
    endDateTime: String,
    onStartChanged: (String) -> Unit,
    onEndChanged: (String) -> Unit
) {
    var openStartDate by rememberSaveable { mutableStateOf(false) }
    var openEndDate by rememberSaveable { mutableStateOf(false) }
    var openStartTime by rememberSaveable { mutableStateOf(false) }
    var openEndTime by rememberSaveable { mutableStateOf(false) }

    if (openStartDate) {
        BasicDatePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartDate = false },
            onConfirm = { onStartChanged(it) }
        )
    }

    if (openEndDate) {
        BasicDatePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndDate = false },
            onConfirm = { onEndChanged(it) }
        )
    }

    if (openStartTime) {
        BasicTimePickerDialog(
            initialDateTime = startDateTime,
            onDismiss = { openStartTime = false },
            onConfirm = { onStartChanged(it) }
        )
    }

    if (openEndTime) {
        BasicTimePickerDialog(
            initialDateTime = endDateTime,
            onDismiss = { openEndTime = false },
            onConfirm = { onEndChanged(it) }
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
                            fontSize = 12.sp
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
                            fontSize = 12.sp
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
                            fontSize = 12.sp
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
                            fontSize = 12.sp
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

/* 동행자, 승인자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendeeApproverBottomSheet(
    tripAddState: TripAddState,
    field: TripSearchField,
    onEvent: (TripAddEvent) -> Unit,
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
            if (shouldLoad && !tripAddState.employeeState.paginationState.isLoading && tripAddState.employeeState.paginationState.currentPage < tripAddState.employeeState.paginationState.totalPage) {
                onEvent(TripAddEvent.LoadNextPage)
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
                    value = tripAddState.employeeState.searchText,
                    onValueChange = { onEvent(TripAddEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = {
                        if (tripAddState.employeeState.paginationState.currentPage <= tripAddState.employeeState.paginationState.totalPage) {
                            onEvent(TripAddEvent.ClickedSearchWith(field))
                        }
                    },
                    onClickInit = { onEvent(TripAddEvent.ClickedSearchInitWith(field)) }
                ),
                hint = "직원명"
            )

            if (tripAddState.employeeState.employees.isEmpty()) {
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
                    items(tripAddState.employeeState.employees) { item ->
                        val isChecked = if (field == TripSearchField.APPROVER) tripAddState.inputData.approverIds.any { it == item.userId } else tripAddState.inputData.attendeeIds.any { it == item.userId }

                        EmployeeItem(
                            info = item,
                            isChecked = isChecked,
                            onChecked = {
                                if (field == TripSearchField.APPROVER) {
                                    onEvent(
                                        TripAddEvent.SelectedApproverWith(
                                            it,
                                            item.userId
                                        )
                                    )
                                } else {
                                    onEvent(
                                        TripAddEvent.SelectedAttendeeWith(
                                            it,
                                            item.userId
                                        )
                                    )
                                }
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

/* 카드 사용 수정 카드 */
@Composable
private fun AddCardCard(tripAddState: TripAddState, onEvent: (TripAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 바텀 시트

    if (openSheet) {
        CardBottomSheet(
            tripAddState = tripAddState,
            onEvent = onEvent,
            onDismiss = {
                onEvent(TripAddEvent.ClickedSearchInitWith(TripSearchField.CARD))
                openSheet = false
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "법인카드",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { openSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "법인카드 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            if (tripAddState.cardState.cards.isEmpty()) {
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
                tripAddState.inputData.cardUsages.forEach { cardUsage ->
                    CardUsageItem(
                        cardInfo = cardUsage,
                        name = tripAddState.cardState.cards.find { it.id == cardUsage.id }?.name ?: "",
                        onChangeStart = { onEvent(TripAddEvent.ChangedCardDateWith(cardUsage.id, true, it)) },
                        onChangeEnd = { onEvent(TripAddEvent.ChangedCardDateWith(cardUsage.id, false, it)) }
                    )
                }
            }
        }
    }
}

/* 카드 사용 현황 목록 아이템 */
@Composable
fun CardUsageItem(cardInfo: TripDTO.CardUsagesInfo, name: String, onChangeStart: (String) -> Unit, onChangeEnd: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp).padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicOutlinedTextField(
                value = name,
                enabled = false,
                onValueChange = {}
            )

            Divider(modifier = Modifier.padding(top = 12.dp))

            StartEndDateTimeEditItem(
                startDateTime = cardInfo.startDate,
                endDateTime = cardInfo.endDate,
                onStartChanged = { onChangeStart(it) },
                onEndChanged = { onChangeEnd(it) }
            )
        }
    }
}

/* 카드 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardBottomSheet(
    tripAddState: TripAddState,
    onEvent: (TripAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val field = TripSearchField.CARD

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDownField(
                modifier = Modifier.fillMaxWidth(),
                options = listOf("전체", "카드명"),
                selected = tripAddState.cardState.type,
                onSelected = { onEvent(TripAddEvent.SelectedCardTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = tripAddState.cardState.searchText,
                    onValueChange = { onEvent(TripAddEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = { onEvent(TripAddEvent.ClickedSearchWith(field)) },
                    onClickInit = { onEvent(TripAddEvent.ClickedSearchInitWith(field)) }
                )
            )

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            if (tripAddState.cardState.cards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "카드 내역이 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tripAddState.cardState.cards) { cardInfo ->
                        val isChecked = tripAddState.inputData.cardUsages.any { it.id in cardInfo.id }

                        CardListItem(
                            cardInfo = cardInfo,
                            isChecked = isChecked,
                            onChecked = { onEvent(TripAddEvent.CheckedCardWith(it, cardInfo))}
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

/* 카드 정보 목록 아이템 */
@Composable
fun CardListItem(cardInfo: CardDTO.CardsInfo, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
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

            Text(
                text = cardInfo.name,
                fontSize = 15.sp
            )
        }
    }
}

/* 차량 사용 수정 카드 */
@Composable
private fun AddCarCard(tripAddState: TripAddState, onEvent: (TripAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 바텀 시트

    if (openSheet) {
        CarBottomSheet(
            tripAddState = tripAddState,
            onEvent = onEvent,
            onDismiss = {
                onEvent(TripAddEvent.ClickedSearchInitWith(TripSearchField.CAR))
                openSheet = false
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "법인차량",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { openSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "법인차량 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            if (tripAddState.carState.cars.isEmpty()) {
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
                tripAddState.inputData.carUsages.forEach { carUsage ->
                    CarUsageItem(
                        tripAddState = tripAddState,
                        carInfo = carUsage,
                        name = tripAddState.carState.cars.find { it.id == carUsage.id }?.name ?: "",
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

/* 차량 사용 현황 목록 아이템 */
@Composable
private fun CarUsageItem(tripAddState: TripAddState, carInfo: TripDTO.CarUsagesInfo, name: String, onEvent: (TripAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 운전자 선택 바텀 시트

    if (openSheet) {
        DriverBottomSheet(
            tripAddState = tripAddState,
            onEvent = onEvent,
            onClick = { onEvent(TripAddEvent.SelectedDriverWith(carInfo.id, it)) },
            onDismiss = { openSheet = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp).padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicOutlinedTextField(
                value = name,
                enabled = false,
                onValueChange = {}
            )

            Divider(modifier = Modifier.padding(top = 12.dp))

            TwoLineSearchEditBar(
                name = "운전자",
                value = tripAddState.employeeState.employees.find { it.userId == carInfo.driverId }?.name ?: "",
                onClick = { openSheet = true },
                isRequired = true,
                enabled = false
            )

            StartEndDateTimeEditItem(
                startDateTime = carInfo.startDate,
                endDateTime = carInfo.endDate,
                onStartChanged = { onEvent(TripAddEvent.ChangedCarDateWith(carInfo.id, true, it))
                },
                onEndChanged = { onEvent(TripAddEvent.ChangedCarDateWith(carInfo.id, false, it)) }
            )
        }
    }
}

/* 차량 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarBottomSheet(
    tripAddState: TripAddState,
    onEvent: (TripAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val field = TripSearchField.CAR

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDownField(
                modifier = Modifier.fillMaxWidth(),
                options = listOf("전체", "차량명", "차량번호"),
                selected = tripAddState.carState.type,
                onSelected = { onEvent(TripAddEvent.SelectedCarTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = tripAddState.carState.searchText,
                    onValueChange = { onEvent(TripAddEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = { onEvent(TripAddEvent.ClickedSearchWith(field)) },
                    onClickInit = { onEvent(TripAddEvent.ClickedSearchInitWith(field)) }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (tripAddState.carState.cars.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "차량 내역이 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tripAddState.carState.cars) { carInfo ->
                        val isChecked = tripAddState.inputData.carUsages.any { it.id in carInfo.id }

                        CarListItem(
                            carInfo = carInfo,
                            isChecked = isChecked,
                            onChecked = { onEvent(TripAddEvent.CheckedCarWith(it, carInfo))}
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

/* 차량 정보 목록 아이템 */
@Composable
fun CarListItem(carInfo: CarDTO.CarsInfo, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
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

            Text(
                text = carInfo.name,
                fontSize = 15.sp
            )
        }
    }
}

/* 운전자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverBottomSheet(
    tripAddState: TripAddState,
    onEvent: (TripAddEvent) -> Unit,
    onClick: (String) -> Unit,
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
            if (shouldLoad && !tripAddState.employeeState.paginationState.isLoading && tripAddState.employeeState.paginationState.currentPage < tripAddState.employeeState.paginationState.totalPage) {
                onEvent(TripAddEvent.LoadNextPage)
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
                    value = tripAddState.employeeState.searchText,
                    onValueChange = { onEvent(TripAddEvent.ChangedSearchValueWith(TripSearchField.DRIVER, it)) },
                    onClickSearch = {
                        if (tripAddState.employeeState.paginationState.currentPage <= tripAddState.employeeState.paginationState.totalPage) {
                            onEvent(TripAddEvent.ClickedSearchWith(TripSearchField.DRIVER)) }
                    },
                    onClickInit = { onEvent(TripAddEvent.ClickedSearchInitWith(TripSearchField.DRIVER)) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                if (tripAddState.employeeState.employees.isEmpty()) {
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
                    items(tripAddState.employeeState.employees) { employeeInfo ->
                        ManagerInfoItem(
                            managerInfo = employeeInfo,
                            onClick = {
                                onClick(employeeInfo.userId)
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