package com.example.attendancemanagementapp.ui.commoncode.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.attendancemanagementapp.ui.commoncode.CodeInfoField
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.Target
import com.example.attendancemanagementapp.ui.components.search.CommonCodeDialog
import com.example.attendancemanagementapp.ui.components.search.CodeSearchUiState
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 공통코드 수정 화면 */
@Composable
fun CodeEditScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeEditUiState by codeViewModel.codeEditUiState.collectAsState()
    val codeListUiState by codeViewModel.codeManageUiState.collectAsState()

    var openDialog by remember { mutableStateOf(false) }    // 공통코드 검색 디알로그 열림 상태

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !codeListUiState.isLoading && codeListUiState.currentPage < codeListUiState.totalPage) {
                codeViewModel.getCodes()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            codeViewModel.initCodeEditUiState() // 화면 사라질 때 입력한 내용 초기화
        }
    }

    if (openDialog) {
        CommonCodeDialog(
            listState = listState,
            isLoading = codeListUiState.isLoading,
            codeSearchUiState = CodeSearchUiState(
                searchUiState = SearchUiState(
                    value = codeListUiState.searchText,
                    onValueChange = { codeViewModel.onSearchTextChange(it) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        codeViewModel.getCodes()
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = {
                        codeViewModel.onSearchTextChange("")
                        codeViewModel.getCodes()
                    }
                ),
                selectedCategory = codeListUiState.selectedCategory,
                categories = SearchType.entries,
                onClickCategory = { codeViewModel.onSearchTypeChange(it) }
            ),
            commonCodes = codeListUiState.codes,
            onDismiss = {
                openDialog = false
                codeViewModel.initSearchState() // 검색 관련 초기화
            },
            onClickItem = { codeViewModel.onUpperCodeChange(Target.EDIT, it) }
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
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SearchEditBar(name = "상위코드", value = codeEditUiState.inputData.upperCode ?: "", onClick = { openDialog = true } )

                EditBar(name = "상위코드명", value = codeEditUiState.inputData.upperCodeName ?: "", enabled = false)

                EditBar(name = "코드", value = codeEditUiState.inputData.code, enabled = false, isRequired = true)
                EditBar(
                    name = "코드명",
                    value = codeEditUiState.inputData.codeName,
                    onValueChange = { codeViewModel.onFieldChange(target = Target.EDIT, field = CodeInfoField.CODENAME, input = it) },
                    isRequired = true
                )
                EditBar(
                    name = "코드 설정값",
                    value = codeEditUiState.inputData.codeValue ?: "",
                    onValueChange = { codeViewModel.onFieldChange(target = Target.EDIT, field = CodeInfoField.CODEVALUE, input = it) }
                )
                RadioEditBar(name = "사용여부", selected = "사용", isRequired = true)
                BigEditBar(
                    name = "설명",
                    value = codeEditUiState.inputData.description ?: "",
                    onValueChange = { codeViewModel.onFieldChange(target = Target.EDIT, field = CodeInfoField.DESCRIPTION, input = it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicLongButton(name = "수정", onClick = {
                    codeViewModel.updateCode(isSuccess = {
                        codeViewModel.initSearchState()
                        navController.popBackStack()
                    })
                })
            }
        }
    }
}