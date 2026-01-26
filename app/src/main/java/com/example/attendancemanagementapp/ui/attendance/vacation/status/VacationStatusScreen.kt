package com.example.attendancemanagementapp.ui.attendance.vacation.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.data.param.VacationsSearchType
import com.example.attendancemanagementapp.ui.attendance.trip.status.TripStatusEvent
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextField
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Blue
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.ui.theme.Vacation_Orange
import com.example.attendancemanagementapp.ui.theme.Vacation_PurPle
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 휴가 현황 화면 */
@Composable
fun VacationStatusScreen(navController: NavController, vacationViewModel: VacationViewModel) {
    val onEvent = vacationViewModel::onStatusEvent
    val vacationStatusState by vacationViewModel.vacationStatusState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    LaunchedEffect(Unit) {
        onEvent(VacationStatusEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "휴가 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            VacationStatusCard(
                vacationStatusState = vacationStatusState,
                onEvent = onEvent
            )
        }
    }
}

/* 휴가 목록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacationsBottomSheet(vacationStatusState: VacationStatusState, onEvent: (VacationStatusEvent) -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !vacationStatusState.paginationState.isLoading && vacationStatusState.paginationState.currentPage < vacationStatusState.paginationState.totalPage) {
                onEvent(VacationStatusEvent.LoadNextPage)
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (vacationStatusState.vacationStatusInfo.vacations.isEmpty()) {
                item {
                    Text(
                        text = "조회된 결과가 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            } else {
                items(vacationStatusState.vacationStatusInfo.vacations) { vacationInfo ->
                    VacationInfoItem(
                        vacationInfo = vacationInfo,
                        onClick = {
                            onDismiss()
                            onEvent(VacationStatusEvent.ClickedVacationWith(vacationInfo.id))
                        }
                    )
                }
            }
        }
    }
}

/* 휴가 목록 아이템 */
@Composable
private fun VacationInfoItem(vacationInfo: VacationDTO.VacationsInfo, onClick: () -> Unit) {
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
                        text = vacationInfo.type,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${vacationInfo.appliedDate} 신청",
                    fontSize = 15.sp
                )
            }

            Text(
                text = "${vacationInfo.startDate} ~ ${vacationInfo.endDate} (${vacationInfo.period})",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )

            Divider(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), color = LightGray)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = vacationInfo.status,
                    fontSize = 15.sp,fontWeight = FontWeight.SemiBold

                )
            }
        }
    }
}

/* 휴가 현황 정보 카드 */
@Composable
private fun VacationStatusCard(vacationStatusState: VacationStatusState, onEvent: (VacationStatusEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        VacationsBottomSheet(
            vacationStatusState = vacationStatusState,
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
            DropDownField(
                modifier = Modifier,
                options = listOf("전체") + vacationStatusState.vacationStatusInfo.years.map { "${it.year}년차" },
                selected = if (vacationStatusState.query.year == null) "전체" else "${vacationStatusState.query.year}년차",
                onSelected = { onEvent(VacationStatusEvent.SelectedYearWith(if (it == "전체") null else it.dropLast(2).toInt())) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = if (vacationStatusState.vacationStatusInfo.years.isEmpty()) "" else vacationStatusState.vacationStatusInfo.years[vacationStatusState.query.year ?: 0].startDate,
                    enabled = false,
                    onValueChange = {}
                )

                Text(
                    text = "~",
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                BasicOutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = if (vacationStatusState.vacationStatusInfo.years.isEmpty()) "" else vacationStatusState.vacationStatusInfo.years[vacationStatusState.query.year ?: 0].endDate,
                    enabled = false,
                    onValueChange = {}
                )
            }

            Divider(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = LightGray)

            Text(
                text = "목록을 조회할 휴가 종류를 클릭하세요",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                VacationTypeItem(
                    modifier = Modifier,
                    name = "전체",
                    count = vacationStatusState.vacationStatusInfo.totalCarryover,
                    backColor = AttendanceInfoItem_Gray,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.TOTAL))
                        openSheet = true
                    }
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "마이너스 이월",
                    count = vacationStatusState.vacationStatusInfo.minusCarryover,
                    backColor = AttendanceInfoItem_Gray,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.MINUS))
                        openSheet = true
                    }
                )
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "특별휴가",
                    count = vacationStatusState.vacationStatusInfo.specialCarryover,
                    backColor = AttendanceInfoItem_Gray,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.SPECIAL))
                        openSheet = true
                    }
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "사용",
                    count = vacationStatusState.vacationStatusInfo.usedCarryover,
                    backColor = MainBlue,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.USED))
                        openSheet = true
                    }
                )
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "미사용",
                    count = vacationStatusState.vacationStatusInfo.unusedCarryover,
                    backColor = AttendanceInfoItem_Blue,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.UNUSED))
                        openSheet = true
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "공가",
                    count = vacationStatusState.vacationStatusInfo.officalCarryover,
                    backColor = Vacation_PurPle,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.OFFICIAL))
                        openSheet = true
                    }
                )
                VacationTypeItem(
                    modifier = Modifier.weight(1f),
                    name = "병가",
                    count = vacationStatusState.vacationStatusInfo.sickCarryover,
                    backColor = Vacation_Orange,
                    onClick = {
                        onEvent(VacationStatusEvent.ClickedVacationTypeWith(VacationsSearchType.SICK))
                        openSheet = true
                    }
                )
            }
        }
    }
}

/* 휴가 종류 목록 아이템 */
@Composable
private fun RowScope.VacationTypeItem(modifier: Modifier, name: String, count: String, backColor: Color, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(color = backColor.copy(alpha = 0.05f))
            .clickable(onClick = { onClick() }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color = backColor)
                .padding(vertical = 5.dp)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}