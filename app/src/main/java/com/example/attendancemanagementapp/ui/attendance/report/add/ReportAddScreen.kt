package com.example.attendancemanagementapp.ui.attendance.report.add

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
import androidx.compose.material.icons.filled.Search
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
import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.asset.car.edit.ManagerInfoItem
import com.example.attendancemanagementapp.ui.attendance.report.ReportViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
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

/* 출장 복명서 등록 화면 */
@Composable
fun TripReportAddScreen(navController: NavController, reportViewModel: ReportViewModel) {
    val onEvent = reportViewModel::onAddEvent
    val reportAddState by reportViewModel.reportAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("기본정보", "입력정보", "여비계산")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장 복명서 등록",
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
                                TripInfoCard(
                                    tripInfo = reportAddState.tripInfo
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
                            1 -> {  // 입력 정보
                                AddReportCard(
                                    reportAddState = reportAddState,
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

                                    SubButton(
                                        name = "이전 승인자 불러오기",
                                        wrapContent = true,
                                        onClick = { onEvent(ReportAddEvent.ClickedGetPrevApprover) }
                                    )

                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                                    )
                                }
                            }
                            2 -> {  // 여비계산
                                AddTripExpenseCard(
                                    reportAddState = reportAddState,
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
                                        onClick = { onEvent(ReportAddEvent.ClickedAdd) }
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

/* 출장 정보 출력 카드 */
@Composable
private fun TripInfoCard(tripInfo: TripDTO.GetTripResponse) {
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
            TowLineInfoBar(name = "출장구분", value = tripInfo.type)
            TowLineInfoBar(name = "출장지", value = tripInfo.place)
            TowLineInfoBar(name = "출장목적", value = tripInfo.purpose)
            TowLineInfoBar(name = "출장기간", value = "${tripInfo.startDate.take(16)} ~ ${tripInfo.endDate.take(16)}\n(${tripInfo.period})")
            TowLineInfoBar(name = "동행자", value = if (tripInfo.attendees.isEmpty()) "-" else tripInfo.attendees.joinToString(", ") { it.name } )
            TowLineInfoBar(name = "법인카드", value = if (tripInfo.cards.isEmpty()) "-" else tripInfo.cards.joinToString(", ") { it.name })
            TowLineInfoBar(name = "법인차량", value = if (tripInfo.cars.isEmpty()) "-" else tripInfo.cars.joinToString(", ") { it.name })
        }
    }
}

/* 복명서 등록 카드 */
@Composable
private fun AddReportCard(reportAddState: ReportAddState, onEvent: (ReportAddEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        ApproverBottomSheet(
            reportAddState = reportAddState,
            onEvent = onEvent,
            onDismiss = {
                onEvent(ReportAddEvent.ClickedSearchInitWith(TripExpenseSearchField.APPROVER))
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
            TwoLineSearchEditBar(
                name = "승인자",
                value = reportAddState.employeeState.employees
                    .filter { it.userId in reportAddState.inputData.approverIds }
                    .joinToString(", ") { it.name },
                onClick = { openSheet = true },
                isRequired = true
            )

            TwoLineBigEditBar(
                name = "복명내용",
                value = reportAddState.inputData.content,
                onValueChange = { onEvent(ReportAddEvent.ChangedContentWith(it)) },
                isRequired = true
            )

            // 여비계산

        }
    }
}

/* 여비계산 등록 카드 */
@Composable
private fun AddTripExpenseCard(reportAddState: ReportAddState, onEvent: (ReportAddEvent) -> Unit) {
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
                    text = "여비계산",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onEvent(ReportAddEvent.ClickedAddExpense) }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "투입인력 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            if (reportAddState.inputData.tripExpenses.isEmpty()) {
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
                reportAddState.inputData.tripExpenses.forEachIndexed { idx, tripExpense ->
                    TripExpenseItem(
                        reportAddState = reportAddState,
                        idx = idx,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

/* 여비계산 목록 아이템 */
@Composable
private fun TripExpenseItem(reportAddState: ReportAddState, idx: Int, onEvent: (ReportAddEvent) -> Unit) {
    val tripExpenseInfo = reportAddState.inputData.tripExpenses[idx]
    val name = when (tripExpenseInfo.type) {
        "개인" -> reportAddState.employeeState.employees.find { it.userId == tripExpenseInfo.id }?.name ?: ""
        "법인" -> reportAddState.cardState.cards.find { it.id == tripExpenseInfo.id }?.name ?: ""
        else -> ""
    }

    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        if (tripExpenseInfo.type == "개인") {
            PayerBottomSheet(
                reportAddState = reportAddState,
                idx = idx,
                onEvent = onEvent,
                onDismiss = {
                    onEvent(ReportAddEvent.ClickedSearchInitWith(TripExpenseSearchField.PAYER))
                    openSheet = false
                }
            )
        } else if (tripExpenseInfo.type == "법인") {
            CardBottomSheet(
                reportAddState = reportAddState,
                idx = idx,
                onEvent = onEvent,
                onDismiss = { openSheet = false }
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDownField(
                modifier = Modifier.fillMaxWidth(),
                options = listOf("개인", "법인"),
                selected = tripExpenseInfo.type,
                onSelected = { onEvent(ReportAddEvent.ChangedExpenseValueWith(TripExpenseField.TYPE, idx, it)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicOutlinedTextField(
                    modifier = Modifier.weight(0.95f),
                    value = name,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false
                )

                IconButton(
                    onClick = { openSheet = true },
                    enabled = tripExpenseInfo.type.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = MainBlue
                    )
                }
            }

            Divider(modifier = Modifier.fillMaxWidth().padding(top = 15.dp).padding(vertical = 5.dp))

            TwoLineEditBar(
                name = "사용내역",
                value = tripExpenseInfo.category,
                onValueChange = { onEvent(ReportAddEvent.ChangedExpenseValueWith(TripExpenseField.CATEGORY, idx, it)) }
            )

            TwoLineEditBar(
                name = "금액",
                value = tripExpenseInfo.amount.toString(),
                onValueChange = { onEvent(ReportAddEvent.ChangedExpenseValueWith(TripExpenseField.AMOUNT, idx, it)) },
                onlyNumber = true
            )
        }
    }
}

/* 승인자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproverBottomSheet(
    reportAddState: ReportAddState,
    onEvent: (ReportAddEvent) -> Unit,
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
            if (shouldLoad && !reportAddState.employeeState.paginationState.isLoading && reportAddState.employeeState.paginationState.currentPage < reportAddState.employeeState.paginationState.totalPage) {
                onEvent(ReportAddEvent.LoadNextPage)
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
                    value = reportAddState.employeeState.searchText,
                    onValueChange = { onEvent(ReportAddEvent.ChangedSearchValueWith(TripExpenseSearchField.APPROVER, it)) },
                    onClickSearch = {
                        if (reportAddState.employeeState.paginationState.currentPage <= reportAddState.employeeState.paginationState.totalPage) {
                            onEvent(ReportAddEvent.ClickedSearchWith(TripExpenseSearchField.APPROVER))
                        }
                    },
                    onClickInit = { onEvent(ReportAddEvent.ClickedSearchInitWith(TripExpenseSearchField.APPROVER)) }
                ),
                hint = "직원명"
            )

            if (reportAddState.employeeState.employees.isEmpty()) {
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
                    items(reportAddState.employeeState.employees) { item ->
                        val isChecked = reportAddState.inputData.approverIds.any { it == item.userId }

                        EmployeeItem(
                            info = item,
                            isChecked = isChecked,
                            onChecked = { onEvent(ReportAddEvent.SelectedApproverWith(it, item.userId)) }
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

/* 결제자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PayerBottomSheet(
    reportAddState: ReportAddState,
    idx: Int,
    onEvent: (ReportAddEvent) -> Unit,
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
            if (shouldLoad && !reportAddState.employeeState.paginationState.isLoading && reportAddState.employeeState.paginationState.currentPage < reportAddState.employeeState.paginationState.totalPage) {
                onEvent(ReportAddEvent.LoadNextPage)
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
                    value = reportAddState.employeeState.searchText,
                    onValueChange = { onEvent(ReportAddEvent.ChangedSearchValueWith(TripExpenseSearchField.PAYER, it)) },
                    onClickSearch = {
                        if (reportAddState.employeeState.paginationState.currentPage <= reportAddState.employeeState.paginationState.totalPage) {
                            onEvent(ReportAddEvent.ClickedSearchWith(TripExpenseSearchField.PAYER)) }
                    },
                    onClickInit = { onEvent(ReportAddEvent.ClickedSearchInitWith(TripExpenseSearchField.PAYER)) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                if (reportAddState.employeeState.employees.isEmpty()) {
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
                    items(reportAddState.employeeState.employees) { employeeInfo ->
                        ManagerInfoItem(
                            managerInfo = employeeInfo,
                            onClick = {
                                onEvent(ReportAddEvent.SelectedManagerWith(employeeInfo.userId, idx))
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

/* 카드 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardBottomSheet(
    reportAddState: ReportAddState,
    idx: Int,
    onEvent: (ReportAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val field = TripExpenseSearchField.CARD

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
                selected = reportAddState.cardState.type,
                onSelected = { onEvent(ReportAddEvent.SelectedCardTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = reportAddState.cardState.searchText,
                    onValueChange = { onEvent(ReportAddEvent.ChangedSearchValueWith(field, it)) },
                    onClickSearch = { onEvent(ReportAddEvent.ClickedSearchWith(field)) },
                    onClickInit = { onEvent(ReportAddEvent.ClickedSearchInitWith(field)) }
                )
            )

            Divider(modifier = Modifier.padding(vertical = 20.dp))

            if (reportAddState.cardState.cards.isEmpty()) {
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
                    items(reportAddState.cardState.cards) { cardInfo ->
                        // TODO: 체크박스 빼기, 추가한 여비계산 아이템 삭제 버튼 추가
                        CardListItem(
                            cardInfo = cardInfo,
                            onClick = {
                                onEvent(ReportAddEvent.SelectedCardWith(cardInfo, idx))
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

/* 카드 정보 목록 아이템 */
@Composable
private fun CardListItem(cardInfo: CardDTO.CardsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(cardInfo.name, "")
        Spacer(modifier = Modifier.height(12.dp))
    }
}
