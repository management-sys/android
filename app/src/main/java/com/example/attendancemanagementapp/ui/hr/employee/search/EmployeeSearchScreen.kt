package com.example.attendancemanagementapp.ui.hr.employee.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.ui.hr.employee.HrViewModel
import com.example.attendancemanagementapp.ui.hr.employee.HrTarget
import com.example.attendancemanagementapp.ui.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.ui.util.rememberOnce

/* 직원 검색 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSearchScreen(navController: NavController, hrViewModel: HrViewModel) {
    val onEvent = hrViewModel::onSearchEvent
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val employeeSearchUiState by hrViewModel.employeeSearchUiState.collectAsState()

    var openBottomSheet by remember { mutableStateOf(false) }   // 직원 정보 바텀 시트 열림 상태

    if (openBottomSheet) {
        EmployeeInfoBottomSheet(
            employeeInfo = employeeSearchUiState.employeeInfo,
            onDismiss = { openBottomSheet = false }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(EmployeeSearchEvent.ClickedInitSearch)
        }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 검색",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                searchUiState = SearchUiState(
                    value = employeeSearchUiState.searchText,
                    onValueChange = { onEvent(EmployeeSearchEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(EmployeeSearchEvent.ClickedSearch)
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { onEvent(EmployeeSearchEvent.ClickedInitSearch) }
                ),
                hint = "직원명"
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                employeeSearchUiState.employees.forEach { employeeInfo ->
                    item {
                        EmployeeInfoItem(
                            name = employeeInfo.name,
                            deptGradeTitle = formatDeptGradeTitle(employeeInfo.department, employeeInfo.grade, employeeInfo.title),
                            onClick = {
                                onEvent(EmployeeSearchEvent.SelectedEmployeeWith(HrTarget.SEARCH, employeeInfo.id))
                                openBottomSheet = true
                            }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }

        }
    }
}

/* 직원 목록 아이템 */
@Composable
private fun EmployeeInfoItem(name: String, deptGradeTitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(name, deptGradeTitle)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/* 직원 정보 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeInfoBottomSheet(employeeInfo: HrDTO.EmployeeInfo, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            Spacer(modifier = Modifier.height(30.dp))
            InfoBar(name = "아이디", value = employeeInfo.id)
            InfoBar(name = "이름", value = employeeInfo.name)
            InfoBar(name = "부서", value = employeeInfo.department)
            InfoBar(name = "직급", value = employeeInfo.grade)
            InfoBar(name = "직책", value = employeeInfo.title ?: "")
            InfoBar(name = "연락처", value = employeeInfo.phone ?: "")
            InfoBar(name = "생년월일", value = employeeInfo.birthDate ?: "")
            InfoBar(name = "입사일", value = employeeInfo.hireDate)
        }
    }
}