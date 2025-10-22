package com.example.attendancemanagementapp.ui.hr.employee.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DepthDropDownField
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeTarget
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.ui.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.ui.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 직원 관리 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManageScreen(navController: NavController, employeeViewModel: EmployeeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val onEvent = employeeViewModel::onManageEvent
    val employeeManageUiState by employeeViewModel.employeeManageState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !employeeManageUiState.isLoading && employeeManageUiState.currentPage < employeeManageUiState.totalPage) {
                employeeViewModel.getManageEmployees()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(EmployeeManageEvent.Init)
        }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(
                onClick = { navController.navigate("employeeAdd") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DepthDropDownField( // 부서 선택 드롭다운
                    options = employeeManageUiState.dropDownMenu.departmentMenu,
                    selected = employeeManageUiState.dropDownState.department,
                    onSelected = { onEvent(EmployeeManageEvent.SelectedDropDownWith(DropDownField.DEPARTMENT, it)) }
                )
                DropDownField(  // 직급 선택 드롭다운
                    modifier = Modifier.width(110.dp),
                    options = employeeManageUiState.dropDownMenu.gradeMenu,
                    selected = employeeManageUiState.dropDownState.grade,
                    onSelected = { onEvent(EmployeeManageEvent.SelectedDropDownWith(DropDownField.GRADE, it)) }
                )
                DropDownField(  // 직책 선택 드롭다운
                    modifier = Modifier.width(110.dp),
                    options = employeeManageUiState.dropDownMenu.titleMenu,
                    selected = employeeManageUiState.dropDownState.title,
                    onSelected = { onEvent(EmployeeManageEvent.SelectedDropDownWith(DropDownField.TITLE, it)) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            SearchBar(
                searchState = SearchState(
                    value = employeeManageUiState.searchText,
                    onValueChange = { onEvent(EmployeeManageEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(EmployeeManageEvent.ClickedSearch)
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { onEvent(EmployeeManageEvent.ClickedInitSearch) }
                ),
                hint = "직원명"
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                employeeManageUiState.employees.forEach { employeeInfo ->
                    item {
                        EmployeeInfoItem(
                            employeeInfo = employeeInfo,
                            deptGradeTitle = formatDeptGradeTitle(employeeInfo.department, employeeInfo.grade, employeeInfo.title),
                            onClick = {
                                onEvent(EmployeeManageEvent.SelectedEmployeeWith(EmployeeTarget.MANAGE, employeeInfo.userId))
                                navController.navigate("employeeDetail")
                            }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(70.dp))
                }
            }

        }
    }
}

/* 직원 목록 아이템 */
@Composable
private fun EmployeeInfoItem(employeeInfo: EmployeeDTO.ManageEmployeesInfo, deptGradeTitle: String, onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(1.dp),
            onClick = onClick
        ) {
            val textColor = if (employeeInfo.isUse == "Y") Color.Black else TextGray

            Spacer(modifier = Modifier.height(12.dp))
            TwoInfoBar(deptGradeTitle, employeeInfo.name, textColor)
            TwoInfoBar(employeeInfo.hireDate, employeeInfo.loginId, textColor)
            Spacer(modifier = Modifier.height(14.dp))
        }
}
