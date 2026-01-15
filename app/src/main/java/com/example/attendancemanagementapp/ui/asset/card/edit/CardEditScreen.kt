package com.example.attendancemanagementapp.ui.asset.card.edit

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.asset.card.CardViewModel
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 카드정보 수정 화면 */
@Composable
fun CardEditScreen(navController: NavController, cardViewModel: CardViewModel) {
    val onEvent = cardViewModel::onEditEvent
    val cardEditState by cardViewModel.cardEditState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "카드정보 수정",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardEditCard(
                cardEditState = cardEditState,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(30.dp))

            BasicLongButton(
                name = "수정",
                onClick = { onEvent(CardEditEvent.ClickedUpdate) }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/* 카드정보 수정 카드 */
@Composable
private fun CardEditCard(cardEditState: CardEditState, onEvent: (CardEditEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        ManagerBottomSheet(
            cardEditState = cardEditState,
            onEvent = onEvent,
            onDismiss = { openSheet = false}
        )
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
            TwoLineEditBar(
                name = "카드명",
                value = cardEditState.inputData.name,
                onValueChange = { onEvent(CardEditEvent.ChangedValueWith(CardEditField.NAME, it)) },
                isRequired = true,
                hint = "카드명을 입력해주세요."
            )

            TwoLineSearchEditBar(
                name = "담당자",
                value = cardEditState.managerName,
                onClick = { openSheet = true },
                isRequired = true
            )

            TwoLineEditBar(
                name = "비고",
                value = cardEditState.inputData.remark,
                onValueChange = { onEvent(CardEditEvent.ChangedValueWith(CardEditField.REMARK, it)) },
                hint = "비고를 입력해주세요."
            )

            TwoLineDropdownEditBar(
                name = "상태",
                isRequired = true,
                options = listOf("정상", "재발급", "폐기"),
                selected = cardEditState.inputData.status,
                onSelected = { onEvent(CardEditEvent.ChangedValueWith(CardEditField.STATUS, it)) }
            )
        }
    }
}

/* 담당자 선택 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManagerBottomSheet(
    cardEditState: CardEditState,
    onEvent: (CardEditEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !cardEditState.employeeState.paginationState.isLoading && cardEditState.employeeState.paginationState.currentPage < cardEditState.employeeState.paginationState.totalPage) {
                onEvent(CardEditEvent.LoadNextPage)
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                searchState = SearchState(
                    value = cardEditState.employeeState.searchText,
                    onValueChange = { onEvent(CardEditEvent.ChangedSearchValueWith(it)) },
                    onClickSearch = {
                        if (cardEditState.employeeState.paginationState.currentPage <= cardEditState.employeeState.paginationState.totalPage) {
                            onEvent(CardEditEvent.ClickedSearch) }
                    },
                    onClickInit = { onEvent(CardEditEvent.ClickedSearchInit) }
                ),
                hint = "직원명"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                if (cardEditState.employeeState.employees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(top = 30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "조회된 결과가 없습니다",
                                color = TextGray,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    items(cardEditState.employeeState.employees) { employeeInfo ->
                        ManagerInfoItem(
                            managerInfo = employeeInfo,
                            onClick = {
                                onEvent(CardEditEvent.SelectedManagerWith(employeeInfo))
                                onDismiss()
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}

/* 담당자 목록 아이템 */
@Composable
fun ManagerInfoItem(managerInfo: EmployeeDTO.ManageEmployeesInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(managerInfo.name, "")
        Spacer(modifier = Modifier.height(12.dp))
    }
}
