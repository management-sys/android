package com.example.attendancemanagementapp.ui.asset.car.detail

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.ui.asset.car.CarViewModel
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditEvent
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageEvent
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.launch

/* 차량 상세 화면 */
@Composable
fun CarDetailScreen(navController: NavController, carViewModel: CarViewModel) {
    val onEvent = carViewModel::onDetailEvent
    val carDetailState by carViewModel.carDetailState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("차량 정보", "예약현황")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var openDelete by remember { mutableStateOf(false) }

    if (openDelete) {
        BasicDialog(
            title = "차량을 삭제하시겠습니까?",
            onDismiss = { openDelete = false },
            onClickConfirm = {
                onEvent(CarDetailEvent.ClickedDelete)
                openDelete = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "차량 상세",
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
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 차량 정보
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    CarInfoCard(
                                        carInfo = carDetailState.carInfo
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        BasicButton(
                                            name = "수정",
                                            onClick = {
                                                carViewModel.onEditEvent(CarEditEvent.InitWith(carDetailState.carInfo))
                                                onEvent(CarDetailEvent.ClickedUpdate)
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }

                            1 -> {  // 예약현황
                                CarReservationInfoCard(
                                    carInfo = carDetailState.carInfo
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 차량 정보 출력 카드 */
@Composable
private fun CarInfoCard(carInfo: CarDTO.GetCarResponse) {
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
            TowLineInfoBar(name = "차종", value = carInfo.type)
            TowLineInfoBar(name = "차량번호", value = carInfo.number)
            TowLineInfoBar(name = "연료종류", value = carInfo.fuelType)
            TowLineInfoBar(name = "소유형태", value = carInfo.ownership)
            TowLineInfoBar(name = "차량명", value = carInfo.name)
            TowLineInfoBar(name = "담당자", value = carInfo.managerName)
            TowLineInfoBar(name = "비고", value = carInfo.remark)
            TowLineInfoBar(name = "상태", value = carInfo.status)
        }
    }
}

/* 차량 예약현황 정보 출력 카드 */
@Composable
private fun CarReservationInfoCard(carInfo: CarDTO.GetCarResponse) {
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
            if (carInfo.history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "예약 내용이 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(carInfo.history) { historyInfo ->
                        CarReservationItem(
                            historyInfo = historyInfo
                        )
                    }
                }
            }
        }
    }
}

/* 차량 예약현황 목록 아이템 */
@Composable
private fun CarReservationItem(historyInfo: CarDTO.HistoryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(historyInfo.driverName, historyInfo.status, fontSize = 15.sp)
        TwoInfoBar("${historyInfo.startDate} ~ ${historyInfo.endDate}", "", color = TextGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
    }
}