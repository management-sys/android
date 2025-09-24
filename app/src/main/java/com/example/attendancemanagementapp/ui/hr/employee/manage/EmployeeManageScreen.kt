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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.ui.hr.DropDownMenu
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.ui.hr.Target
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 직원 관리 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManageScreen(navController: NavController, hrViewModel: HrViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val employeeManageUiState by hrViewModel.employeeManageUiState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !employeeManageUiState.isLoading && employeeManageUiState.currentPage < employeeManageUiState.totalPage) {
                hrViewModel.getManageEmployees()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            hrViewModel.initSearchState(Target.MANAGE)
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
                DepthDropDownField(
                    options = employeeManageUiState.dropDownMenu.departmentMenu,
                    selected = employeeManageUiState.dropDownUiState.department,
                    onSelected = { hrViewModel.onSelectDropDown(DropDownMenu.DEPARTMENT, it) }
                )
                DropDownField(
                    options = employeeManageUiState.dropDownMenu.gradeMenu,
                    selected = employeeManageUiState.dropDownUiState.grade,
                    onSelected = { hrViewModel.onSelectDropDown(DropDownMenu.GRADE, it) }
                )
                DropDownField(
                    options = employeeManageUiState.dropDownMenu.titleMenu,
                    selected = employeeManageUiState.dropDownUiState.title,
                    onSelected = { hrViewModel.onSelectDropDown(DropDownMenu.TITLE, it) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            SearchBar(
                searchUiState = SearchUiState(
                    value = employeeManageUiState.searchText,
                    onValueChange = { hrViewModel.onSearchTextChange(Target.MANAGE, it) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        hrViewModel.getManageEmployees()
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { hrViewModel.initSearchState(Target.MANAGE) }
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
                            deptGradeTitle = hrViewModel.formatDeptGradeTitle(employeeInfo.department, employeeInfo.grade, employeeInfo.title),
                            onClick = {
                                hrViewModel.getEmployeeDetail(Target.MANAGE, employeeInfo.userId)
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
private fun EmployeeInfoItem(employeeInfo: HrDTO.ManageEmployeesInfo, deptGradeTitle: String, onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick = onClick
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TwoInfoBar(deptGradeTitle, employeeInfo.name)
            TwoInfoBar(employeeInfo.hireDate, employeeInfo.userId)
            Spacer(modifier = Modifier.height(14.dp))
        }
}

/* 드롭다운 필드 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownField(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }  // 열림 여부

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().width(110.dp),
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = DarkGray,
                focusedBorderColor = DarkGray,
                disabledContainerColor = DisableGray,
                disabledBorderColor = DarkGray,
                disabledTextColor = DarkGray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* 계층 드롭다운 필드 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepthDropDownField(options: List<HrDTO.DepartmentsInfo>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }  // 열림 여부

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().width(110.dp),
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = DarkGray,
                focusedBorderColor = DarkGray,
                disabledContainerColor = DisableGray,
                disabledBorderColor = DarkGray,
                disabledTextColor = DarkGray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(modifier = Modifier.fillMaxWidth().padding(start = (option.depth * 10).dp)) {
                            Text(
                                text = option.name,
                                fontSize = 14.sp
                            )
                        }
                    },
                    onClick = {
                        onSelected(option.name)
                        expanded = false
                    }
                )
            }
        }
    }
}