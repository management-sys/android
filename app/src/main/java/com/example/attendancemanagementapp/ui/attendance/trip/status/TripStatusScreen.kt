package com.example.attendancemanagementapp.ui.attendance.trip.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.attendancemanagementapp.ui.attendance.report.ReportViewModel
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 출장 현황 화면 */
@Composable
fun TripStatusScreen(navController: NavController, tripViewModel: TripViewModel, reportViewModel: ReportViewModel) {
    val onEvent = tripViewModel::onStatusEvent
    val tripStatusState by tripViewModel.tripStatusState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    LaunchedEffect(Unit) {
        onEvent(TripStatusEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            TripStatusCard(
                tripStatusState = tripStatusState,
                onEvent = onEvent,
                reportViewModel = reportViewModel
            )
        }
    }
}

/* 출장 현황 정보 카드 */
@Composable
private fun TripStatusCard(tripStatusState: TripStatusState, onEvent: (TripStatusEvent) -> Unit, reportViewModel: ReportViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !tripStatusState.paginationState.isLoading && tripStatusState.paginationState.currentPage < tripStatusState.paginationState.totalPage) {
                onEvent(TripStatusEvent.LoadNextPage)
            }
        }
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
            DropDownField(
                modifier = Modifier,
                options = tripStatusState.types,
                selected = tripStatusState.query.filter,
                onSelected = { onEvent(TripStatusEvent.SelectedFilterWith(it)) }
            )

            DropDownField(
                modifier = Modifier,
                options = listOf("전체") + tripStatusState.tripStatusInfo.years.map { "${it.year}년차" },
                selected = if (tripStatusState.query.year == null) "전체" else "${tripStatusState.query.year}년차",
                onSelected = { onEvent(TripStatusEvent.SelectedYearWith(if (it == "전체") null else it.dropLast(2).toInt())) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = if (tripStatusState.tripStatusInfo.years.isEmpty()) "" else tripStatusState.tripStatusInfo.years[tripStatusState.query.year ?: 0].startDate,
                    enabled = false,
                    onValueChange = {}
                )

                Text(
                    text = "~",
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                BasicOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = if (tripStatusState.tripStatusInfo.years.isEmpty()) "" else tripStatusState.tripStatusInfo.years[tripStatusState.query.year ?: 0].endDate,
                    enabled = false,
                    onValueChange = {}
                )
            }

            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), color = LightGray)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                state = listState
            ) {
                if (tripStatusState.tripStatusInfo.trips.isEmpty()) {
                    item {
                        Text(
                            text = "조회된 결과가 없습니다",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    items(tripStatusState.tripStatusInfo.trips) { tripInfo ->
                        TripInfoItem(
                            tripInfo = tripInfo,
                            onClick = {
                                onEvent(TripStatusEvent.ClickedTripWith(tripInfo.id))
                                reportViewModel.getTripReport(tripInfo.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

/* 출장 목록 아이템 */
@Composable
private fun TripInfoItem(tripInfo: TripDTO.TripsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(percent = 50),
                        )
                        .padding(vertical = 2.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = tripInfo.type,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${tripInfo.appliedDate} 신청",
                    fontSize = 15.sp
                )
            }

            Text(
                text = "${tripInfo.startDate} ~ ${tripInfo.endDate} (${tripInfo.period})",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )

            Divider(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), color = LightGray)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = tripInfo.status,
                    fontSize = 15.sp,fontWeight = FontWeight.SemiBold

                )
            }
        }
    }
}