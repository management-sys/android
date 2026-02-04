package com.example.attendancemanagementapp.ui.approver.request

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.param.ApplicationType
import com.example.attendancemanagementapp.data.param.ApprovalType
import com.example.attendancemanagementapp.ui.approver.ApproverViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Yellow
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 결재 요청 목록 화면 */
@Composable
fun ApproverRequestScreen(navController: NavController, approverViewModel: ApproverViewModel) {
    val onEvent = approverViewModel::onRequestEvent
    val approverRequestState by approverViewModel.approveRequestState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    var openFilter by remember { mutableStateOf(false) }
    if (openFilter) {
        ApproverRequestFilterBottomSheet(
            approverRequestState = approverRequestState,
            onEvent = onEvent,
            onDismiss = { openFilter = false }
        )
    }

    var openAdd by remember { mutableStateOf(false)}
    if (openAdd) {
        ApproverAddDialog(
            onDismiss = { openAdd = false },
            onClickTrip = {
                openAdd = false
                onEvent(ApproverRequestEvent.ClickedAddTrip)
            },
            onClickVacation = {
                openAdd = false
                onEvent(ApproverRequestEvent.ClickedAddVacation)
            }
        )
    }

    LaunchedEffect(Unit) {
        onEvent(ApproverRequestEvent.InitFirst)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "결재 요청",
                actIcon = Icons.Default.FilterAlt,
                actTint = if (approverRequestState.filter.hasFilter()) MainBlue else DarkGray,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openFilter = true }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(
                onClick = { openAdd = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            ApproverRequestCard(
                approverRequestState = approverRequestState,
                onEvent = onEvent
            )
        }
    }
}

/* 결재 요청 목록 카드 */
@Composable
private fun ApproverRequestCard(approverRequestState: ApproverRequestState, onEvent: (ApproverRequestEvent) -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !approverRequestState.paginationState.isLoading && approverRequestState.paginationState.currentPage < approverRequestState.paginationState.totalPage) {
                onEvent(ApproverRequestEvent.LoadNextPage)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        if (approverRequestState.approverRequest.approvers.isEmpty()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(vertical = 14.dp, horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                state = listState
            ) {
                items(items = approverRequestState.approverRequest.approvers) { approverInfo ->
                    ApproverRequestListItem(
                        approverInfo = approverInfo
                    )
                }

                item {
                    Spacer(Modifier.height(15.dp))
                }
            }
        }
    }
}

/* 결재 요청 목록 아이템 */
@Composable
private fun ApproverRequestListItem(approverInfo: ApproverDTO.ApproversInfo,) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        ApproverTypeStatusBar(type = "${approverInfo.approvalType} - ${approverInfo.applicationType}", status = approverInfo.status)
        TwoInfoBar("${approverInfo.startDate} ~ ${approverInfo.endDate} (${approverInfo.period})", "", fontSize = 14.sp)
        TwoInfoBar(approverInfo.appliedDate, "", fontSize = 14.sp, color = TextGray)
        Spacer(modifier = Modifier.height(14.dp))
    }
}

/* 결재 구분, 상태 출력 바 */
@Composable
private fun ApproverTypeStatusBar(type: String, status: String) {
    val statusColor = when (status) {
        "승인" -> ApprovalInfoItem_Green
        "결재대기" -> ApprovalInfoItem_Yellow
        "반려" -> ApprovalInfoItem_Red
        "취소" -> ApprovalInfoItem_Gray
        else -> Color.Black
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = type,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = status,
            color = Color.White,
            modifier = Modifier
                .background(color = statusColor, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 15.dp, vertical = 3.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* 결재 요청 검색 필터 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproverRequestFilterBottomSheet(approverRequestState: ApproverRequestState, onEvent: (ApproverRequestEvent) -> Unit, onDismiss: () -> Unit) {
    var filter by remember { mutableStateOf(approverRequestState.filter) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        val yearList = listOf("전체") + (2016..2025).map { it.toString() }
        val monthList = listOf("전체") + (1..12).map { "${it}월" }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "연도",
                options = yearList,
                selected = if (filter.year == null) "전체" else filter.year.toString(),
                onSelected = { filter = filter.copy(year = if (it == "전체") null else it.toInt()) }
            )

            TwoLineDropdownEditBar(
                name = "월",
                options = monthList,
                selected = if (filter.month == null) "전체" else "${filter.month}월",
                onSelected = { filter = filter.copy(month = if (it == "전체") null else it.replace("월", "").toInt()) }
            )

            TwoLineDropdownEditBar(
                name = "결재 구분",
                options = ApprovalType.entries.map { it.label },
                selected = filter.approvalType.label,
                onSelected = { filter = filter.copy(approvalType = ApprovalType.fromLabel(it)) }
            )

            TwoLineDropdownEditBar(
                name = "상태 구분",
                options = ApplicationType.entries.map { it.label },
                selected = filter.applicationType.label,
                onSelected = { filter = filter.copy(applicationType = ApplicationType.fromLabel(it)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubButton(
                    name = "초기화",
                    onClick = {
                        onEvent(ApproverRequestEvent.ClickedInitFilter)
                        onDismiss()
                    }
                )

                BasicButton(
                    name = "적용",
                    onClick = {
                        onEvent(ApproverRequestEvent.ClickedUseFilter(filter = filter))
                        onDismiss()
                    }
                )
            }
        }
    }
}

/* 결재 신청 디알로그 */
@Composable
private fun ApproverAddDialog(
    onDismiss: () -> Unit,
    onClickTrip: () -> Unit,
    onClickVacation: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = "신청 종류 선택",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            BasicButton(
                name = "출장신청",
                wrapContent = true,
                onClick = onClickTrip
            )
        },
        dismissButton = {
            SubButton(
                name = "휴가신청",
                wrapContent = true,
                onClick = onClickVacation
            )
        },
        containerColor = Color.White
    )
}
