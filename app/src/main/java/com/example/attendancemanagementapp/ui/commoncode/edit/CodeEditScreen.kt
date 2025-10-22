package com.example.attendancemanagementapp.ui.commoncode.edit

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
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.BigEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.RadioEditBar
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.components.search.SearchCommonCodeDialog
import com.example.attendancemanagementapp.ui.components.search.CodeSearchState
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 공통코드 수정 화면 */
@Composable
fun CodeEditScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val onEvent = codeViewModel::onEditEvent
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeEditState by codeViewModel.codeEditState.collectAsState()
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
            if (shouldLoad && !codeListState.isLoading && codeListState.currentPage < codeListState.totalPage) {
                codeViewModel.getCodes()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(CodeEditEvent.Init) // 화면 사라질 때 입력한 내용 초기화
        }
    }

    if (openDialog) {
        SearchCommonCodeDialog(
            listState = listState,
            isLoading = codeListState.isLoading,
            codeSearchState = CodeSearchState(
                searchState = SearchState(
                    value = codeListState.searchText,
                    onValueChange = { onEvent(CodeEditEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(CodeEditEvent.ClickedSearch)
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { onEvent(CodeEditEvent.ClickedInitSearch) }
                ),
                selectedCategory = codeListState.selectedCategory,
                categories = SearchType.entries,
                onClickCategory = { onEvent(CodeEditEvent.ChangedCategoryWith(it)) }
            ),
            commonCodes = codeListState.codes,
            onDismiss = {
                openDialog = false
                onEvent(CodeEditEvent.InitSearch)
            },
            onClickItem = { onEvent(CodeEditEvent.SelectedUpperCodeWith(it.upperCode.orEmpty(), it.upperCodeName.orEmpty())) }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 수정",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CodeEditCard(
                codeEditState = codeEditState,
                onEvent = onEvent,
                onClickOpenDialog = { openDialog = true }
            )
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
            SearchEditBar(name = "상위코드", value = codeEditState.inputData.upperCode ?: "", onClick = { onClickOpenDialog() } )

            EditBar(name = "상위코드명", value = codeEditState.inputData.upperCodeName ?: "", enabled = false)

            EditBar(name = "코드", value = codeEditState.inputData.code, enabled = false, isRequired = true)
            EditBar(
                name = "코드명",
                value = codeEditState.inputData.codeName,
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.CODENAME, it)) },
                isRequired = true
            )
            EditBar(
                name = "코드 설정값",
                value = codeEditState.inputData.codeValue ?: "",
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.CODEVALUE, it)) }
            )
            RadioEditBar(name = "사용여부", selected = "사용", isRequired = true)
            BigEditBar(
                name = "설명",
                value = codeEditState.inputData.description ?: "",
                onValueChange = { onEvent(CodeEditEvent.ChangedValueWith(CodeEditField.DESCRIPTION, it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicLongButton(name = "수정", onClick = { onEvent(CodeEditEvent.ClickedEdit) })
        }
    }
}