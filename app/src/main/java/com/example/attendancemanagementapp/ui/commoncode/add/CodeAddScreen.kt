package com.example.attendancemanagementapp.ui.commoncode.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.BigEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.RadioEditBar
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.commoncode.CodeInfoField
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.SearchField
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.search.CommonCodeDialog
import com.example.attendancemanagementapp.ui.components.search.SearchUiState

@Preview
@Composable
private fun Preview_CodeAddScreen() {
    val navController = rememberNavController()
    val codeViewModel: CodeViewModel = viewModel()
    CodeAddScreen(navController, codeViewModel)
}

/* 공통코드 등록 화면 */
@Composable
fun CodeAddScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeAddUiState by codeViewModel.codeAddUiState.collectAsState()
    val codeListUiState by codeViewModel.codeListUiState.collectAsState()

    val categories = listOf("전체", "상위코드", "상위코드 이름", "코드", "코드 이름") // 검색 카테고리 칩

    var openDialog by remember { mutableStateOf(false) }    // 공통코드 검색 디알로그 열림 상태

    if (openDialog) {
        CommonCodeDialog(
            searchUiState = SearchUiState(
                value = codeListUiState.searchText.value,
                onValueChange = { codeViewModel.onSearchFieldChange(field = SearchField.SEARCHTEXT, input = it) },
                onClickSearch = {
                    // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                    codeViewModel.getFilteredCode()
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                },
                selected = codeListUiState.selectedFilter.value,
                categories = categories,
                onClickCategory = { codeViewModel.onSearchFieldChange(field = SearchField.FILTER, input = it) }
            ),
            commonCodes = codeListUiState.codes,
            onDismiss = { openDialog = false },
            onClickItem = { codeViewModel.onUpperCodeChange(it) }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            codeViewModel.initCodeAddUiState() // 화면 사라질 때 입력한 내용 초기화
        }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 등록",
                onClickNavIcon = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                SearchEditBar(name = "상위코드", value = codeAddUiState.inputData.upperCode, onClick = { openDialog = true } )

                EditBar(name = "상위코드명", value = codeAddUiState.inputData.upperCodeName, enabled = false)

                EditBar(name = "코드", value = codeAddUiState.inputData.code, isRequired = true)
                EditBar(
                    name = "코드명",
                    value = codeAddUiState.inputData.codeName,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.CODENAME, input = it) },
                    isRequired = true
                )
                EditBar(
                    name = "코드 설정값",
                    value = codeAddUiState.inputData.codeValue,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.CODEVALUE, input = it) }
                )
                RadioEditBar(name = "사용여부", selected = "사용", isRequired = true)
                BigEditBar(
                    name = "설명",
                    value = codeAddUiState.inputData.description,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.DESCRIPTION, input = it) }
                )
            }


            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicLongButton(name = "등록", onClick = { /* TODO: 공통코드 추가 기능 */ })
            }
        }
    }
}