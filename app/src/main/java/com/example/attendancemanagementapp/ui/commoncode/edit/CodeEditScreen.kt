package com.example.attendancemanagementapp.ui.commoncode.edit

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel.CodeTarget
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineRadioEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.CodeSearchState
import com.example.attendancemanagementapp.ui.components.search.SearchCommonCodeDialog
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 공통코드 수정 화면 */
@Composable
fun CodeEditScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리

    val onEvent = codeViewModel::onEditEvent
    val codeEditState by codeViewModel.codeEditState.collectAsState()

    var openDialog by remember { mutableStateOf(false) }    // 공통코드 검색 디알로그 열림 상태

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !codeEditState.codeState.paginationState.isLoading && codeEditState.codeState.paginationState.currentPage < codeEditState.codeState.paginationState.totalPage) {
                codeViewModel.getCodes(CodeTarget.EDIT)
            }
        }
    }

    if (openDialog) {
        SearchCommonCodeDialog(
            listState = listState,
            isLoading = codeEditState.codeState.paginationState.isLoading,
            codeSearchState = CodeSearchState(
                searchState = SearchState(
                    value = codeEditState.codeState.searchText,
                    onValueChange = { onEvent(CodeEditEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        if (codeEditState.codeState.paginationState.currentPage <= codeEditState.codeState.paginationState.totalPage) {
                            onEvent(CodeEditEvent.ClickedSearch)
                        }
                    },
                    onClickInit = { onEvent(CodeEditEvent.ClickedInitSearch) }
                ),
                selectedCategory = codeEditState.codeState.selectedCategory,
                categories = SearchType.entries,
                onClickCategory = { onEvent(CodeEditEvent.ChangedCategoryWith(it)) }
            ),
            commonCodes = codeEditState.codeState.codes,
            onDismiss = {
                openDialog = false
            },
            onClickItem = { onEvent(CodeEditEvent.SelectedUpperCodeWith(it.upperCode.orEmpty(), it.upperCodeName.orEmpty())) }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "공통코드 수정",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CodeEditCard(
                codeEditState = codeEditState,
                onEvent = onEvent,
                onClickOpenDialog = {
                    onEvent(CodeEditEvent.InitSearch) // 검색 관련 초기화
                    openDialog = true
                }
            )

            Box(
                modifier = Modifier.height(40.dp)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(CodeEditEvent.Init) // 화면 사라질 때 입력한 내용 초기화
        }
    }
}

/* 공통카드 수정 카드 */
@Composable
private fun CodeEditCard(codeEditState: CodeEditState, onEvent: (CodeEditEvent) -> Unit, onClickOpenDialog: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineSearchEditBar(
                name = "상위코드",
                value = codeEditState.inputData.upperCode ?: "",
                onClick = { onClickOpenDialog() }
            )

            TwoLineEditBar(
                name = "상위코드명",
                value = codeEditState.inputData.upperCodeName ?: "",
                enabled = false
            )

            TwoLineEditBar(
                name = "코드",
                value = codeEditState.inputData.code,
                enabled = false,
                isRequired = true
            )

            TwoLineEditBar(
                name = "코드명",
                value = codeEditState.inputData.codeName,
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.CODENAME, it)) },
                isRequired = true
            )

            TwoLineEditBar(
                name = "코드 설정값",
                value = codeEditState.inputData.codeValue ?: "",
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.CODEVALUE, it)) }
            )

            TwoLineRadioEditBar(
                name = "사용여부",
                selected = "사용",
                isRequired = true
            )

            TwoLineBigEditBar(
                name = "설명",
                value = codeEditState.inputData.description ?: "",
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.DESCRIPTION, it)) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            BasicLongButton(
                name = "수정",
                onClick = { onEvent(CodeEditEvent.ClickedEdit) }
            )
        }
    }
}