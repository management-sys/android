package com.example.attendancemanagementapp.ui.attendance.vacation.detail

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.util.formatDateYYYYMMDDHHmm
import com.example.attendancemanagementapp.util.rememberOnce
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/* 휴가 상세 화면 */
@Composable
fun VacationDetailScreen(navController: NavController, vacationViewModel: VacationViewModel) {
    val onEvent = vacationViewModel::onDetailEvent
    val vacationDetailState by vacationViewModel.vacationDetailState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    var openDelete by remember { mutableStateOf(false) }
    var openCancel by remember { mutableStateOf(false) }

    if (openDelete) {
        BasicDialog(
            title = "휴가 신청을 삭제하시겠습니까?",
            onDismiss = { openDelete = false },
            onClickConfirm = {
                onEvent(VacationDetailEvent.ClickedDelete)
                openDelete = false
            }
        )
    }

    if (openCancel) {
        BasicDialog(
            title = "휴가 신청을 취소하시겠습니까?",
            onDismiss = { openCancel = false },
            onClickConfirm = {
                onEvent(VacationDetailEvent.ClickedCancel)
                openCancel = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "휴가신청서",
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
            VacationInfoCard(
                vacationInfo = vacationDetailState.vacationInfo
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    BasicButton(
                        name = "신청서 다운로드",
                        wrapContent = true,
                        onClick = {  }
                    )
                }

                Row {
                    BasicButton(
                        name = "수정",
                        onClick = {  }
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

/* 휴가 정보 출력 카드 */
@Composable
private fun VacationInfoCard(vacationInfo: VacationDTO.GetVacationResponse) {
    val startDate = if (vacationInfo.startDate.isNotBlank()) LocalDate.parse(vacationInfo.startDate.take(10)) else LocalDate.now()
    val endDate = if (vacationInfo.endDate.isNotBlank()) LocalDate.parse(vacationInfo.endDate.take(10)) else LocalDate.now()
    val date = ChronoUnit.DAYS.between(startDate, endDate) + 1

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
            TowLineInfoBar(name = "유형", value = vacationInfo.type)
            TowLineInfoBar(name = "신청자", value = vacationInfo.userName)
            TowLineInfoBar(name = "부서", value = vacationInfo.departmentName)
            TowLineInfoBar(name = "직급", value = vacationInfo.grade)
            TowLineInfoBar(name = "기간", value = "${formatDateYYYYMMDDHHmm(vacationInfo.startDate)} ~ ${formatDateYYYYMMDDHHmm(vacationInfo.endDate)}\n(${date}일)")
            TowLineInfoBar(name = "세부사항", value = vacationInfo.detail.ifBlank { "-" })
            TowLineInfoBar(name = "신청일", value = formatDateYYYYMMDDHHmm(vacationInfo.appliedDate))
            TowLineInfoBar(name = "상태", value = vacationInfo.status)
        }
    }
}
