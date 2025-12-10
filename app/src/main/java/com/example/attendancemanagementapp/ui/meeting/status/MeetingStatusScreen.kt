package com.example.attendancemanagementapp.ui.meeting.status

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDateEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 회의록 현황 화면 */
@Composable
fun MeetingStatusScreen(navController: NavController, meetingViewModel: MeetingViewModel) {
    val onEvent = meetingViewModel::onStatusEvent
    val meetingStatusState by meetingViewModel.meetingStatusState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(MeetingStatusEvent.Init)
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "회의록 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            MeetingListCard(
                meetingStatusState = meetingStatusState,
                onEvent = onEvent
            )
        }
    }
}

/* 회의록 목록 카드 */
@Composable
private fun MeetingListCard(meetingStatusState: MeetingStatusState, onEvent: (MeetingStatusEvent) -> Unit) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        MeetingSearchBottomSheet(
            meetingStatusState = meetingStatusState,
            onEvent = onEvent,
            onDismiss = { openSheet = false }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchBar(
                    modifier = Modifier.weight(0.85f),
                    searchState = SearchState(
                        value = meetingStatusState.searchText,
                        onValueChange = { onEvent(MeetingStatusEvent.ChangedSearchValue(it)) },
                        onClickSearch = {
                            // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                            if (meetingStatusState.paginationState.currentPage <= meetingStatusState.paginationState.totalPage) {
                                onEvent(MeetingStatusEvent.ClickedSearch)
                                keyboardController?.hide()
                                focusManager.clearFocus(force = true)
                            }
                        },
                        onClickInit = { onEvent(MeetingStatusEvent.ClickedInitSearch) }
                    ),
                    hint = "검색어를 입력하세요"
                )

                IconButton(
                    modifier = Modifier.weight(0.15f),
                    onClick = { openSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "검색 필터",
                        tint = if (meetingStatusState.startDate.isBlank() && meetingStatusState.endDate.isBlank() && meetingStatusState.type == "전체") DarkGray else MainBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            MeetingList(
                meetingStatusState = meetingStatusState,
                onEvent = onEvent
            )
        }
    }
}

/* 회의록 목록 출력 */
@Composable
private fun MeetingList(meetingStatusState: MeetingStatusState, onEvent: (MeetingStatusEvent) -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !meetingStatusState.paginationState.isLoading && meetingStatusState.paginationState.currentPage < meetingStatusState.paginationState.totalPage) {
                onEvent(MeetingStatusEvent.LoadNextPage)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (meetingStatusState.meetings.isEmpty()) {
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(meetingStatusState.meetings) { meetingInfo ->
                    MeetingInfoItem(
                        meetingInfo = meetingInfo,
                        onClick = { onEvent(MeetingStatusEvent.ClickedMeeting(meetingInfo.id)) }
                    )
                }
            }
        }
    }
}

/* 회의록 목록 아이템 */
@Composable
private fun MeetingInfoItem(meetingInfo: MeetingDTO.MeetingsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(meetingInfo.projectName, "", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        TwoInfoBar(meetingInfo.title, "", fontSize = 14.sp)
        TwoInfoBar("${"%,d".format(meetingInfo.totalExpense)}원", "", fontSize = 14.sp)
        TwoInfoBar("${meetingInfo.startDate} ~ ${meetingInfo.endDate}", "", color = TextGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
    }
}

/* 회의록 검색 필터 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingSearchBottomSheet(meetingStatusState: MeetingStatusState, onEvent: (MeetingStatusEvent) -> Unit, onDismiss: () -> Unit) {
    var tempStartDate by remember { mutableStateOf(meetingStatusState.startDate) }
    var tempEndDate by remember { mutableStateOf(meetingStatusState.endDate) }
    var tempType by remember { mutableStateOf(meetingStatusState.type) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDateEditBar(
                modifier = Modifier.fillMaxWidth(),
                name = "시작일",
                value = tempStartDate,
                onValueChange = { tempStartDate = it }
            )

            TwoLineDateEditBar(
                modifier = Modifier.fillMaxWidth(),
                name = "종료일",
                value = tempEndDate,
                onValueChange = { tempEndDate = it }
            )

            TwoLineDropdownEditBar(
                name = "검색 키워드",
                options = listOf("전체", "프로젝트명", "회의명"),
                selected = tempType,
                onSelected = { tempType = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubButton(
                    name = "초기화",
                    onClick = {
                        onEvent(MeetingStatusEvent.ClickedInitFilter)
                        onDismiss()
                    }
                )

                BasicButton(
                    name = "적용",
                    onClick = {
                        onEvent(MeetingStatusEvent.ClickedUseFilter(tempStartDate, tempEndDate, tempType))
                        onDismiss()
                    }
                )
            }
        }
    }
}
