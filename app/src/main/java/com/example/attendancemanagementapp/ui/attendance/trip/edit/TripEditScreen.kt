package com.example.attendancemanagementapp.ui.attendance.trip.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.asset.car.edit.ManagerInfoItem
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.attendance.trip.add.CarListItem
import com.example.attendancemanagementapp.ui.attendance.trip.add.CardListItem
import com.example.attendancemanagementapp.ui.attendance.trip.add.CardUsageItem
import com.example.attendancemanagementapp.ui.attendance.trip.add.StartEndDateTimeEditItem
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddField
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripSearchField
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
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
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/* 출장 수정 화면 */
@Composable
fun TripEditScreen(navController: NavController, tripViewModel: TripViewModel) {
    val onEvent = tripViewModel::onEditEvent
    val tripEditState by tripViewModel.tripEditState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("기본정보", "카드정보", "차량정보")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장 품의서 수정",
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
                                EditTripCard(
                                    tripEditState = tripEditState,
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전 승인자 불러오기",
                                        wrapContent = true,
                                        onClick = { onEvent(TripEditEvent.ClickedGetPrevApprover) }
                                    )

                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )
                                }
                            }
                            1 -> {  // 카드 정보
                                EditCardCard(
                                    tripEditState = tripEditState,
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
                                EditCarCard(
                                    tripEditState = tripEditState,
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
                                        onClick = { onEvent(TripEditEvent.ClickedUpdate) }
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
private fun EditTripCard(tripEditState: TripEditState, onEvent: (TripEditEvent) -> Unit) {
    var openSheet by remember { mutableStateOf("") } // 바텀 시트

    if (openSheet.isNotBlank()) {
        when (openSheet) {
            "Attendee" -> {
                AttendeeApproverBottomSheet(
                    tripEditState = tripEditState,
                    field = TripSearchField.ATTENDEE,
                    onEvent = onEvent,
                    onDismiss = {
                        onEvent(TripEditEvent.ClickedSearchInit(TripSearchField.ATTENDEE))
                        openSheet = ""
                    }
                )
            }
            "Approver" -> {
                AttendeeApproverBottomSheet(
                    tripEditState = tripEditState,
                    field = TripSearchField.APPROVER,
                    onEvent = onEvent,
                    onDismiss = {
                        onEvent(TripEditEvent.ClickedSearchInit(TripSearchField.APPROVER))
                        openSheet = ""
                    }
                )
            }
        }
    }

    val dates = if (tripEditState.inputData.startDate == "" || tripEditState.inputData.endDate == "") "0" else {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val startDate = LocalDateTime.parse(tripEditState.inputData.startDate, formatter).toLocalDate()
        val endDate = LocalDateTime.parse(tripEditState.inputData.endDate, formatter).toLocalDate()

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
                options = tripEditState.tripTypeNames,
                selected = tripEditState.inputData.type,
                onSelected = { onEvent(TripEditEvent.SelectedTypeWith(it)) }
            )

            TwoLineEditBar(
                name = "출장지",
                isRequired = true,
                value = tripEditState.inputData.place,
                onValueChange = { onEvent(TripEditEvent.ChangedValueWith(TripAddField.PLACE, it)) }
            )

            TwoLineEditBar(
                name = "출장목적",
                isRequired = true,
                value = tripEditState.inputData.purpose,
                onValueChange = { onEvent(TripEditEvent.ChangedValueWith(TripAddField.PURPOSE, it)) }
            )

            StartEndDateTimeEditItem(
                startDateTime = tripEditState.inputData.startDate,
                endDateTime = tripEditState.inputData.endDate,
                onStartChanged = { onEvent(TripEditEvent.ChangedValueWith(TripAddField.START, it)) },
                onEndChanged = { onEvent(TripEditEvent.ChangedValueWith(TripAddField.END, it)) }
            )

            TwoLineEditBar(
                name = "일수",
                value = dates,
                onValueChange = {},
                enabled = false
            )

            TwoLineSearchEditBar(
                name = "동행자",
                value = tripEditState.employeeState.employees
                    .filter { it.userId in tripEditState.inputData.attendeeIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = "Attendee" }
            )

            TwoLineSearchEditBar(
                name = "승인자",
                value = tripEditState.employeeState.employees
                    .filter { it.userId in tripEditState.inputData.approverIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = "Approver" },
                isRequired = true
            )

            TwoLineBigEditBar(
                name = "품의내용",
                value = tripEditState.inputData.content,
                onValueChange = { onEvent(TripEditEvent.ChangedValueWith(TripAddField.CONTENT, it)) },
                isRequired = true
            )
        }
    }
}

/* 동행자, 승인자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendeeApproverBottomSheet(
    tripEditState: TripEditState,
    field: TripSearchField,
    onEvent: (TripEditEvent) -> Unit,
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
            if (shouldLoad && !tripEditState.employeeState.paginationState.isLoading && tripEditState.employeeState.paginationState.currentPage < tripEditState.employeeState.paginationState.totalPage) {
                onEvent(TripEditEvent.LoadNextPage)
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
                    value = tripEditState.employeeState.searchText,
                    onValueChange = { onEvent(TripEditEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = {
                        if (tripEditState.employeeState.paginationState.currentPage <= tripEditState.employeeState.paginationState.totalPage) {
                            onEvent(TripEditEvent.ClickedSearch(field))
                        }
                    },
                    onClickInit = { onEvent(TripEditEvent.ClickedSearchInit(field)) }
                ),
                hint = "직원명"
            )

            if (tripEditState.employeeState.employees.isEmpty()) {
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
                    items(tripEditState.employeeState.employees) { item ->
                        val isChecked = if (field == TripSearchField.APPROVER) tripEditState.inputData.approverIds.any { it == item.userId } else tripEditState.inputData.attendeeIds.any { it == item.userId }

                        EmployeeItem(
                            info = item,
                            isChecked = isChecked,
                            onChecked = {
                                if (field == TripSearchField.APPROVER) {
                                    onEvent(
                                        TripEditEvent.SelectedApproverWith(
                                            it,
                                            item.userId
                                        )
                                    )
                                } else {
                                    onEvent(
                                        TripEditEvent.SelectedAttendeeWith(
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
private fun EditCardCard(tripEditState: TripEditState, onEvent: (TripEditEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 바텀 시트

    if (openSheet) {
        CardBottomSheet(
            tripEditState = tripEditState,
            onEvent = onEvent,
            onDismiss = {
                onEvent(TripEditEvent.ClickedSearchInit(TripSearchField.CARD))
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

            if (tripEditState.cardState.cards.isEmpty()) {
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
                tripEditState.inputData.cardUsages.forEach { cardUsage ->
                    CardUsageItem(
                        cardInfo = cardUsage,
                        name = tripEditState.cardState.cards.find { it.id == cardUsage.id }?.name ?: "",
                        onChangeStart = { onEvent(TripEditEvent.ChangedCardDateWith(cardUsage.id, true, it)) },
                        onChangeEnd = { onEvent(TripEditEvent.ChangedCardDateWith(cardUsage.id, false, it)) }
                    )
                }
            }
        }
    }
}

/* 카드 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardBottomSheet(
    tripEditState: TripEditState,
    onEvent: (TripEditEvent) -> Unit,
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
                selected = tripEditState.cardState.type,
                onSelected = { onEvent(TripEditEvent.SelectedCardTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = tripEditState.cardState.searchText,
                    onValueChange = { onEvent(TripEditEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = { onEvent(TripEditEvent.ClickedSearch(field)) },
                    onClickInit = { onEvent(TripEditEvent.ClickedSearchInit(field)) }
                )
            )

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            if (tripEditState.cardState.cards.isEmpty()) {
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
                    items(tripEditState.cardState.cards) { cardInfo ->
                        val isChecked = tripEditState.inputData.cardUsages.any { it.id in cardInfo.id }

                        CardListItem(
                            cardInfo = cardInfo,
                            isChecked = isChecked,
                            onChecked = { onEvent(TripEditEvent.CheckedCardWith(it, cardInfo))}
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

/* 차량 사용 수정 카드 */
@Composable
private fun EditCarCard(tripEditState: TripEditState, onEvent: (TripEditEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 바텀 시트

    if (openSheet) {
        CarBottomSheet(
            tripEditState = tripEditState,
            onEvent = onEvent,
            onDismiss = {
                onEvent(TripEditEvent.ClickedSearchInit(TripSearchField.CAR))
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

            if (tripEditState.carState.cars.isEmpty()) {
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
                tripEditState.inputData.carUsages.forEach { carUsage ->
                    CarUsageItem(
                        tripEditState = tripEditState,
                        carInfo = carUsage,
                        name = tripEditState.carState.cars.find { it.id == carUsage.id }?.name ?: "",
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

/* 차량 사용 현황 목록 아이템 */
@Composable
private fun CarUsageItem(tripEditState: TripEditState, carInfo: TripDTO.CarUsagesInfo, name: String, onEvent: (TripEditEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) } // 운전자 선택 바텀 시트

    if (openSheet) {
        DriverBottomSheet(
            tripEditState = tripEditState,
            onEvent = onEvent,
            onClick = { onEvent(TripEditEvent.SelectedDriverWith(carInfo.id, it)) },
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
                value = tripEditState.employeeState.employees.find { it.userId == carInfo.driverId }?.name ?: "",
                onClick = { openSheet = true },
                isRequired = true,
                enabled = false
            )

            StartEndDateTimeEditItem(
                startDateTime = carInfo.startDate,
                endDateTime = carInfo.endDate,
                onStartChanged = { onEvent(TripEditEvent.ChangedCarDateWith(carInfo.id, true, it))
                },
                onEndChanged = { onEvent(TripEditEvent.ChangedCarDateWith(carInfo.id, false, it)) }
            )
        }
    }
}

/* 차량 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarBottomSheet(
    tripEditState: TripEditState,
    onEvent: (TripEditEvent) -> Unit,
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
                selected = tripEditState.carState.type,
                onSelected = { onEvent(TripEditEvent.SelectedCarTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = tripEditState.carState.searchText,
                    onValueChange = { onEvent(TripEditEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = { onEvent(TripEditEvent.ClickedSearch(field)) },
                    onClickInit = { onEvent(TripEditEvent.ClickedSearchInit(field)) }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (tripEditState.carState.cars.isEmpty()) {
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
                    items(tripEditState.carState.cars) { carInfo ->
                        val isChecked = tripEditState.inputData.carUsages.any { it.id in carInfo.id }

                        CarListItem(
                            carInfo = carInfo,
                            isChecked = isChecked,
                            onChecked = { onEvent(TripEditEvent.CheckedCarWith(it, carInfo))}
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

/* 운전자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverBottomSheet(
    tripEditState: TripEditState,
    onEvent: (TripEditEvent) -> Unit,
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
            if (shouldLoad && !tripEditState.employeeState.paginationState.isLoading && tripEditState.employeeState.paginationState.currentPage < tripEditState.employeeState.paginationState.totalPage) {
                onEvent(TripEditEvent.LoadNextPage)
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
                    value = tripEditState.employeeState.searchText,
                    onValueChange = { onEvent(TripEditEvent.ChangedSearchValueWith(TripSearchField.DRIVER, it)) },
                    onClickSearch = {
                        if (tripEditState.employeeState.paginationState.currentPage <= tripEditState.employeeState.paginationState.totalPage) {
                            onEvent(TripEditEvent.ClickedSearch(TripSearchField.DRIVER)) }
                    },
                    onClickInit = { onEvent(TripEditEvent.ClickedSearchInit(TripSearchField.DRIVER)) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                if (tripEditState.employeeState.employees.isEmpty()) {
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
                    items(tripEditState.employeeState.employees) { employeeInfo ->
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