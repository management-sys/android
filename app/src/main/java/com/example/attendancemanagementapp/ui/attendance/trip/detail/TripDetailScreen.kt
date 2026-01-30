package com.example.attendancemanagementapp.ui.attendance.trip.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.attendancemanagementapp.ui.attendance.report.ReportViewModel
import com.example.attendancemanagementapp.ui.attendance.report.add.ReportAddEvent
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.launch

/* 출장 상세 화면 */
@Composable
fun TripDetailScreen(navController: NavController, tripViewModel: TripViewModel, reportViewModel: ReportViewModel) {
    val onEvent = tripViewModel::onDetailEvent
    val tripDetailState by tripViewModel.tripDetailState.collectAsState()
    val reportDetailState by reportViewModel.reportDetailState.collectAsState()
    val hasReport = tripDetailState.tripInfo.hasReport == "Y"

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = if (hasReport) listOf("출장 품의서", "출장 복명서") else listOf("출장 품의서")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var openDelete by remember { mutableStateOf(false) }
    var openCancel by remember { mutableStateOf(false) }

    if (openDelete) {
        BasicDialog(
            title = when (pagerState.currentPage) {
                0 -> "출장 품의서를 삭제하시겠습니까?"
                1 -> "출장 복명서를 삭제하시겠습니까?"
                else -> ""
            },
            onDismiss = { openDelete = false },
            onClickConfirm = {
                when (pagerState.currentPage) {
                    0 -> onEvent(TripDetailEvent.ClickedDelete)
                    1 -> reportViewModel.deleteTripReport()
                }
                openDelete = false
            }
        )
    }

    if (openCancel) {
        BasicDialog(
            title = when (pagerState.currentPage) {
                0 -> "출장 품의서를 취소하시겠습니까?"
                1 -> "출장 복명서를 취소하시겠습니까?"
                else -> ""
            },
            onDismiss = { openCancel = false },
            onClickConfirm = {
                when (pagerState.currentPage) {
                    0 -> onEvent(TripDetailEvent.ClickedCancel)
                    1 -> reportViewModel.cancelTripReport()
                }
                openCancel = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDelete = true }
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
                            0 -> {  // 출장 품의서
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    ApproverInfoCard(
                                        approverName = tripDetailState.tripInfo.approverName,
                                        status = tripDetailState.tripInfo.status
                                    )

                                    TripInfoCard(
                                        tripInfo = tripDetailState.tripInfo
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box {
                                            BasicButton(
                                                name = "품의서 다운로드",
                                                wrapContent = true,
                                                onClick = { onEvent(TripDetailEvent.ClickedDownload) }
                                            )
                                        }

                                        Row {
                                            if (tripDetailState.tripInfo.status != "승인") {
                                                BasicButton(
                                                    name = if (tripDetailState.tripInfo.rejection.isNullOrBlank()) "수정" else "재신청",
                                                    wrapContent = true,
                                                    onClick = { onEvent(TripDetailEvent.ClickedUpdate) }
                                                )

                                                Spacer(modifier = Modifier.width(10.dp))
                                            }

                                            SubButton(
                                                name = "취소",
                                                onClick = { openCancel = true }
                                            )

                                            // TODO: 출장 승인 기능 생기면 주석 해제 (승인 상태인 경우 수정 버튼 비출력, 복명서 작성 버튼 출력)
                                            // 승인되었으면 복명서 작성 버튼 활성화
                                            //                    if (tripDetailState.tripInfo.status == "승인") {
                                            Spacer(modifier = Modifier.width(10.dp))

                                            BasicButton(
                                                name = "복명서 작성",
                                                wrapContent = true,
                                                onClick = {
                                                    reportViewModel.onAddEvent(
                                                        ReportAddEvent.InitWith(
                                                            tripDetailState.tripInfo
                                                        )
                                                    )
                                                    onEvent(TripDetailEvent.ClickedAddReport)
                                                }
                                            )
                                            //                    }
                                        }
                                    }
                                }
                            }
                            1 -> {  // 출장 복명서
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    ApproverInfoCard(
                                        approverName = reportDetailState.reportInfo.approverName,
                                        status = reportDetailState.reportInfo.status
                                    )

                                    TripReportInfoCard(
                                        reportInfo = reportDetailState.reportInfo
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box {
                                            BasicButton(
                                                name = "복명서 다운로드",
                                                wrapContent = true,
                                                onClick = {}
                                            )
                                        }

                                        if (tripDetailState.tripInfo.status != "승인") {
                                            BasicButton(
                                                name = if (tripDetailState.tripInfo.rejection.isNullOrBlank()) "복명서 수정" else "복명서 재신청",
                                                wrapContent = true,
                                                onClick = {}
                                            )
                                        }

                                        SubButton(
                                            name = "복명서 취소",
                                            wrapContent = true,
                                            onClick = { openCancel = true }
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
}

/* 승인자 정보 출력 카드 */
@Composable
private fun ApproverInfoCard(approverName: String, status: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).border(width = 0.5.dp, color = LightGray)
        ) {
            Box(
                modifier = Modifier.weight(0.4f).fillMaxHeight().background(color = LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "승인자",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            VerticalDivider(thickness = 0.5.dp, color = LightGray)

            Column(
                modifier = Modifier.weight(0.6f).height(IntrinsicSize.Min)
            ) {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth().background(color = LightBlue),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = approverName,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Divider(thickness = 0.5.dp, color = LightGray)

                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (status == "승인" || status == "반려") status else "",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
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
            TowLineInfoBar(name = "이름", value = tripInfo.userName)
            TowLineInfoBar(name = "부서", value = tripInfo.departmentName)
            TowLineInfoBar(name = "직급", value = tripInfo.grade)
            TowLineInfoBar(name = "출장지", value = tripInfo.place)
            TowLineInfoBar(name = "출장목적", value = tripInfo.purpose)
            TowLineInfoBar(name = "출장기간", value = "${tripInfo.startDate.take(16)} ~ ${tripInfo.endDate.take(16)}\n(${tripInfo.period})")
            TowLineInfoBar(name = "동행자", value = if (tripInfo.attendees.isEmpty()) "-" else tripInfo.attendees.joinToString(", ") { it.name } )
            TowLineInfoBar(name = "품의내용", value = tripInfo.content)
            TowLineInfoBar(name = "법인카드", value = if (tripInfo.cards.isEmpty()) "-" else tripInfo.cards.joinToString(", ") { it.name })
            TowLineInfoBar(name = "법인차량", value = if (tripInfo.cars.isEmpty()) "-" else tripInfo.cars.joinToString(", ") { it.name })
            TowLineInfoBar(name = "품의 신청일", value = tripInfo.appliedDate.take(16))

            if (tripInfo.status == "반려") {
                TowLineInfoBar(name = "반려사유", value = tripInfo.rejection ?: "-")
            }
        }
    }
}

/* 출장 복명서 정보 출력 카드 */
@Composable
private fun TripReportInfoCard(reportInfo: TripDTO.GetTripReportResponse) {
    val tripExpensesStr = reportInfo.tripExpenses.joinToString(",") {
        "${it.type}결재 (${it.buyerNm})${it.category}${"%,d".format(it.amount)}원"
    }

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
            TowLineInfoBar(name = "복명내용", value = reportInfo.content)
            TowLineInfoBar(name = "여비계산", value = tripExpensesStr)
            TowLineInfoBar(name = "복명 신청일", value = reportInfo.appliedDate)
        }
    }
}
