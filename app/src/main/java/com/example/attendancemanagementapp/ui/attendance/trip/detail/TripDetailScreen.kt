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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.attendance.trip.edit.TripEditEvent
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.util.formatDateYYYYMMDDHHmm
import com.example.attendancemanagementapp.util.rememberOnce

/* 출장 상세 화면 */
@Composable
fun TripDetailScreen(navController: NavController, tripViewModel: TripViewModel) {
    val onEvent = tripViewModel::onDetailEvent
    val tripDetailState by tripViewModel.tripDetailState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리
    val context = LocalContext.current

    var openDelete by remember { mutableStateOf(false) }
    var openCancel by remember { mutableStateOf(false) }

    if (openDelete) {
        BasicDialog(
            title = "출장품의서를 삭제하시겠습니까?",
            onDismiss = { openDelete = false },
            onClickConfirm = {
                onEvent(TripDetailEvent.ClickedDelete)
                openDelete = false
            }
        )
    }

    if (openCancel) {
        BasicDialog(
            title = "출장품의서를 취소하시겠습니까?",
            onDismiss = { openCancel = false },
            onClickConfirm = {
                onEvent(TripDetailEvent.ClickedCancel)
                openCancel = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "출장품의서",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDelete = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 26.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ApproverInfoCard(
                tripInfo = tripDetailState.tripInfo
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
                        onClick = { onEvent(TripDetailEvent.ClickedDownloadWith(context))}
                    )
                }

                Row {
                    BasicButton(
                        name = if (tripDetailState.tripInfo.rejection.isNullOrBlank()) "수정" else "재신청",
                        wrapContent = true,
                        onClick = { onEvent(TripDetailEvent.ClickedUpdate) }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    SubButton(
                        name = "취소",
                        onClick = { openCancel = true }
                    )
                }
            }
        }
    }
}

/* 승인자 정보 출력 카드 */
@Composable
private fun ApproverInfoCard(tripInfo: TripDTO.GetTripResponse) {
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
                        text = tripInfo.approverName,
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
                        text = if (tripInfo.status == "승인" || tripInfo.status == "반려") tripInfo.status else "",
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