package com.example.attendancemanagementapp.ui.asset.card.usage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.attendancemanagementapp.ui.asset.card.CardViewModel
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/* 카드 사용현황 화면 */
@Composable
fun CardUsageScreen(navController: NavController, cardViewModel: CardViewModel) {
    val onEvent = cardViewModel::onUsageEvent
    val cardUsageState by cardViewModel.cardUsageState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("예약현황", "사용이력")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onEvent(CardUsageEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "카드 사용현황",
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
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 예약현황 정보
                                DropDownField(
                                    modifier = Modifier.fillMaxWidth(),
                                    options = listOf("전체", "카드명", "신청자"),
                                    selected = cardUsageState.reservationState.type,
                                    onSelected = { onEvent(CardUsageEvent.SelectedTypeWith(CardUsageField.RESERVATION, it)) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                SearchBar(
                                    searchState = SearchState(
                                        value = cardUsageState.reservationState.searchText,
                                        onValueChange = { onEvent(CardUsageEvent.ChangedSearchTextWith(CardUsageField.RESERVATION, it)) },
                                        onClickSearch = {
                                            if (cardUsageState.reservationState.paginationState.currentPage <= cardUsageState.reservationState.paginationState.totalPage) {
                                                onEvent(CardUsageEvent.ClickedSearchWith(CardUsageField.RESERVATION))
                                            } },
                                        onClickInit = { onEvent(CardUsageEvent.ClickedInitSearchTextWith(CardUsageField.RESERVATION)) }
                                    )
                                )

                                Spacer(Modifier.height(15.dp))

                                ReservationInfoCard(
                                    reservationState = cardUsageState.reservationState,
                                    onEvent = onEvent
                                )
                            }

                            1 -> {  // 사용이력 정보
                                DropDownField(
                                    modifier = Modifier.fillMaxWidth(),
                                    options = listOf("전체", "차량명", "차량번호", "운행자"),
                                    selected = cardUsageState.usageState.type,
                                    onSelected = { onEvent(CardUsageEvent.SelectedTypeWith(CardUsageField.USAGE, it)) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                SearchBar(
                                    searchState = SearchState(
                                        value = cardUsageState.usageState.searchText,
                                        onValueChange = { onEvent(CardUsageEvent.ChangedSearchTextWith(CardUsageField.USAGE, it)) },
                                        onClickSearch = {
                                            if (cardUsageState.usageState.paginationState.currentPage <= cardUsageState.usageState.paginationState.totalPage) {
                                                onEvent(CardUsageEvent.ClickedSearchWith(CardUsageField.USAGE))
                                            } },
                                        onClickInit = { onEvent(CardUsageEvent.ClickedInitSearchTextWith(CardUsageField.USAGE)) }
                                    )
                                )

                                Spacer(Modifier.height(15.dp))

                                UsageInfoCard(
                                    usageState = cardUsageState.usageState,
                                    onEvent = onEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 예약현황 정보 카드 */
@Composable
private fun ReservationInfoCard(reservationState: CardUsageSearchState, onEvent: (CardUsageEvent) -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !reservationState.paginationState.isLoading && reservationState.paginationState.currentPage < reservationState.paginationState.totalPage) {
                onEvent(CardUsageEvent.LoadNextPage(CardUsageField.RESERVATION))
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (reservationState.histories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "예약 내역이 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(reservationState.histories) { historyInfo ->
                        CardUsageItem(
                            cardUsageInfo = historyInfo
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

/* 사용이력 정보 카드 */
@Composable
private fun UsageInfoCard(usageState: CardUsageSearchState, onEvent: (CardUsageEvent) -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !usageState.paginationState.isLoading && usageState.paginationState.currentPage < usageState.paginationState.totalPage) {
                onEvent(CardUsageEvent.LoadNextPage(CardUsageField.USAGE))
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (usageState.histories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "사용 이력이 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(usageState.histories) { historyInfo ->
                        CardUsageItem(
                            cardUsageInfo = historyInfo
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

/* 예약현황/사용이력 목록 아이템 */
@Composable
private fun CardUsageItem(cardUsageInfo: CardDTO.CardUsageInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        TwoInfoBar(cardUsageInfo.name, "", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), thickness = 1.dp, color = DividerDefaults.color.copy(alpha = 0.8f))

        TwoInfoBar(cardUsageInfo.departmentName, cardUsageInfo.applicantName, fontSize = 15.sp)
        TwoInfoBar("${cardUsageInfo.startDate} ~ ${cardUsageInfo.endDate}", "", color = TextGray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(14.dp))
    }
}