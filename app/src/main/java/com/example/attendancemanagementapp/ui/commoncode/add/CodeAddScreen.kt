package com.example.attendancemanagementapp.ui.commoncode.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.BigEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.RadioEditBar
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.components.search.CodeSearchState
import com.example.attendancemanagementapp.ui.components.search.SearchCommonCodeDialog
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 공통코드 등록 화면 */
@Composable
fun CodeAddScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val onEvent = codeViewModel::onAddEvent
    val codeAddState by codeViewModel.codeAddState.collectAsState()
    val codeListState by codeViewModel.codeManageState.collectAsState()

    var openDialog by remember { mutableStateOf(false) }    // 공통코드 검색 디알로그 열림 상태

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !codeListState.paginationState.isLoading && codeListState.paginationState.currentPage < codeListState.paginationState.totalPage) {
                codeViewModel.getCodes()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(CodeAddEvent.Init)  // 화면 사라질 때 입력한 내용 초기화
        }
    }

    if (openDialog) {
        SearchCommonCodeDialog(
            listState = listState,
            isLoading = codeListState.paginationState.isLoading,
            codeSearchState = CodeSearchState(
                searchState = SearchState(
                    value = codeListState.searchText,
                    onValueChange = { onEvent(CodeAddEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(CodeAddEvent.ClickedSearch)
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { onEvent(CodeAddEvent.ClickedInitSearch) }
                ),
                selectedCategory = codeListState.selectedCategory,
                categories = SearchType.entries,
                onClickCategory = { onEvent(CodeAddEvent.ChangedCategoryWith(it)) }
            ),
            commonCodes = codeListState.codes,
            onDismiss = {
                openDialog = false
                onEvent(CodeAddEvent.InitSearch) // 검색 관련 초기화
            },
            onClickItem = { onEvent(CodeAddEvent.SelectedUpperCodeWith(it.upperCode.orEmpty(), it.upperCodeName.orEmpty())) }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CodeAddCard(
                codeAddState = codeAddState,
                onEvent = onEvent,
                onClickOpenDialog = { openDialog = true }
            )
        }
    }
}

/* 공통카드 등록 카드 */
@Composable
private fun CodeAddCard(codeAddState: CodeAddState, onEvent: (CodeAddEvent) -> Unit, onClickOpenDialog: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SearchEditBar(name = "상위코드", value = codeAddState.inputData.upperCode ?: "", onClick = { onClickOpenDialog() } )

            EditBar(name = "상위코드명", value = codeAddState.inputData.upperCodeName ?: "", enabled = false)

            EditBar(
                name = "코드",
                value = codeAddState.inputData.code,
                onValueChange = { onEvent(CodeAddEvent.ChangedValueWith(CodeAddField.CODE, it)) },
                isRequired = true
            )
            EditBar(
                name = "코드명",
                value = codeAddState.inputData.codeName,
                onValueChange = { onEvent(CodeAddEvent.ChangedValueWith(CodeAddField.CODENAME, it)) },
                isRequired = true
            )
            EditBar(
                name = "코드 설정값",
                value = codeAddState.inputData.codeValue ?: "",
                onValueChange = { onEvent(CodeAddEvent.ChangedValueWith(CodeAddField.CODEVALUE, it)) }
            )
            RadioEditBar(name = "사용여부", selected = "사용", isRequired = true)
            BigEditBar(
                name = "설명",
                value = codeAddState.inputData.description ?: "",
                onValueChange = { onEvent(CodeAddEvent.ChangedValueWith(CodeAddField.DESCRIPTION, it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicLongButton(name = "등록", onClick = { onEvent(CodeAddEvent.ClickedAdd) })
        }
    }
}