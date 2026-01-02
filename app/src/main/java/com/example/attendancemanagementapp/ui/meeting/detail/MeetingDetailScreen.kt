package com.example.attendancemanagementapp.ui.meeting.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditEvent
import com.example.attendancemanagementapp.util.formatDateRangeWithTime
import com.example.attendancemanagementapp.util.rememberOnce

/* 회의록 상세 화면 */
@Composable
fun MeetingDetailScreen(navController: NavController, meetingViewModel: MeetingViewModel) {
    val onEvent = meetingViewModel::onDetailEvent
    val meetingDetailState by meetingViewModel.meetingDetailState.collectAsState()

    var openDeleteDialog by remember { mutableStateOf(false) }    // 회의록 삭제 확인 디알로그 열림 상태

    if (openDeleteDialog) {
        BasicDialog(
            title = "회의록을 삭제하시겠습니까?",
            text = "관련 데이터가 전부 삭제됩니다.",
            onDismiss = { openDeleteDialog = false },
            onClickConfirm = {
                onEvent(MeetingDetailEvent.ClickedDelete)
                openDeleteDialog = false
            },
            onClickDismiss = {
                openDeleteDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "회의록 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDeleteDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            MeetingInfoCard(meetingInfo = meetingDetailState.info)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                BasicButton(
                    name = "수정",
                    onClick = {
                        meetingViewModel.onEditEvent(MeetingEditEvent.InitWith(meetingDetailState.info))
                        navController.navigate("meetingEdit")
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}
/* 회의록 정보 출력 카드 */
@Composable
private fun MeetingInfoCard(meetingInfo: MeetingDTO.GetMeetingResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            TowLineInfoBar(name = "프로젝트명", value = meetingInfo.projectName)
            TowLineInfoBar(name = "회의록 제목", value = meetingInfo.title)
            TowLineInfoBar(name = "일시", value = formatDateRangeWithTime(meetingInfo.startDate, meetingInfo.endDate))
            TowLineInfoBar(name = "장소", value = meetingInfo.place)
            ViewAttendeesItem(attendeesInfo = meetingInfo.attendees)
            TowLineInfoBar(name = "회의내용", value = meetingInfo.content)
            TowLineInfoBar(name = "비고", value = if (meetingInfo.remark.isNullOrBlank()) "-" else meetingInfo.remark)
            ViewExpensesItem(expensesInfo = meetingInfo.expenses)
        }
    }
}

/* 참석자 정보 출력 아이템 */
@Composable
private fun ViewAttendeesItem(attendeesInfo: List<MeetingDTO.AttendeesInfo>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "참석자",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        attendeesInfo.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { attendee ->
                    AttendeesItem(
                        attendeeInfo = attendee,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 만약 개수가 홀수면 빈 공간 하나 채워주기
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/* 참석자 목록 아이템 */
@Composable
private fun AttendeesItem(attendeeInfo: MeetingDTO.AttendeesInfo, modifier: Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = attendeeInfo.department ?: "",
                fontSize = 13.sp
            )
            Text(
                text = attendeeInfo.grade ?: "",
                fontSize = 13.sp
            )
            Text(
                text = attendeeInfo.name ?: "",
                fontSize = 13.sp
            )
        }
    }
}

/* 회의비 정보 출력 아이템 */
@Composable
private fun ViewExpensesItem(expensesInfo: List<MeetingDTO.ExpensesInfo>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "회의비",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        expensesInfo.forEach { expense ->
            ExpensesItem(expenseInfo = expense)
        }
    }
}

/* 회의비 목록 아이템 */
@Composable
private fun ExpensesItem(expenseInfo: MeetingDTO.ExpensesInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(0.9f).padding(end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expenseInfo.type,
                    fontSize = 13.sp
                )
                Text(
                    text = "${"%,d".format(expenseInfo.amount)}원",
                    fontSize = 13.sp
                )
            }

            IconButton(
                modifier = Modifier.weight(0.1f).size(20.dp),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "영수증"
                )
            }
        }
    }
}