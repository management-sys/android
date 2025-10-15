package com.example.attendancemanagementapp.ui.hr.employee.add

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.base.CollectUiEffect
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DateEditBar
import com.example.attendancemanagementapp.ui.components.DateEditDeleteBar
import com.example.attendancemanagementapp.ui.components.DropdownEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.PhoneEditBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.hr.employee.edit.AuthItem
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.util.rememberOnce

/* 직원 등록 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddScreen(navController: NavController, employeeViewModel: EmployeeViewModel) {
    val onEvent = employeeViewModel::onAddEvent
    val employeeAddState by employeeViewModel.employeeAddState.collectAsState()

    var openDeptDialog by remember { mutableStateOf(false) }    // 부서 선택 팝업창 열림 상태
    var openAuthDialog by remember { mutableStateOf(false) }    // 권한 선택 팝업창 열림 상태

    if (openDeptDialog) {
        DepartmentDialog(
            employeeAddState = employeeAddState,
            onDismiss = { openDeptDialog = false },
            onEvent = onEvent
        )
    }

    if (openAuthDialog) {
        AuthDialog(
            employeeAddState = employeeAddState,
            onDismiss = { openAuthDialog = false },
            onEvent = onEvent
        )
    }

    LaunchedEffect(Unit) {
        onEvent(EmployeeAddEvent.Init)
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 26.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            EmployeeEditCard(
                employeeAddState = employeeAddState,
                onEvent = onEvent,
                onOpenAuth = { openAuthDialog = true },
                onOpenDept = { openDeptDialog = true }
            )
            SalaryEditCard(
                salaries = employeeAddState.inputData.salaries,
                onEvent = onEvent
            )
            BasicLongButton(
                name = "등록",
                onClick = { onEvent(EmployeeAddEvent.ClickedAdd) }
            )
        }
    }
}

/* 직원 상세 정보 수정 카드 */
@Composable
private fun EmployeeEditCard(employeeAddState: EmployeeAddState, onEvent: (EmployeeAddEvent) -> Unit, onOpenAuth: () -> Unit, onOpenDept: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileImage() // TODO: 이미지 수정 기능
            Spacer(modifier = Modifier.height(20.dp))
            EditBar(
                name = "아이디",
                value = employeeAddState.inputData.loginId,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.LOGINID, it)) },
                isRequired = true,
                hint = "아이디를 입력해주세요."
            )
            SearchEditBar(
                name = "권한",
                value = employeeAddState.selectAuthor.joinToString("/") { it.name },
                onClick = { onOpenAuth() },
                isRequired = true,
                enabled = true,
                icon = Icons.Default.AddCircle
            )
            EditBar(
                name = "이름",
                value = employeeAddState.inputData.name,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.NAME, it)) },
                isRequired = true,
                hint = "이름을 입력해주세요."
            )
            SearchEditBar(
                name = "부서",
                value = employeeAddState.inputData.department,
                onClick = { onOpenDept() },
                isRequired = true,
                enabled = true
            )
            DropdownEditBar(
                name = "직급",
                options = employeeAddState.dropDownMenu.gradeMenu,
                selected = employeeAddState.inputData.grade,
                onSelected = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.GRADE, it)) },
                isRequired = true
            )
            DropdownEditBar(
                name = "직책",
                options = employeeAddState.dropDownMenu.titleMenu,
                selected = employeeAddState.inputData.title ?: "직책",
                onSelected = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.TITLE, it)) }
            )
            PhoneEditBar(
                name = "연락처",
                value = employeeAddState.inputData.phone ?: "",
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.PHONE, it)) }
            )
            DateEditDeleteBar(
                name = "생년월일",
                value = employeeAddState.inputData.birthDate ?: "",
                onClick = { onEvent(EmployeeAddEvent.ClickedInitBirthDate) },
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.BIRTHDATE, it)) }
            )
            DateEditBar(
                name = "입사일",
                value = employeeAddState.inputData.hireDate,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.HIREDATE, it)) },
                isRequired = true
            )
        }
    }
}

/* 연봉 정보 수정 카드 */
@Composable
private fun SalaryEditCard(salaries: List<EmployeeDTO.SalaryInfo>, onEvent: (EmployeeAddEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "연봉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onEvent(EmployeeAddEvent.ClickedAddSalary) }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "연봉 아이템 추가 버튼"
                    )
                }
            }

            salaries.forEachIndexed { idx, salary ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "•   ",
                        fontSize = 16.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(0.3f),
                            value = salary.year,
                            onValueChange = {
                                onEvent(EmployeeAddEvent.ChangedSalaryWith(SalaryField.YEAR, it, idx))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = DarkGray,
                                focusedBorderColor = DarkGray,
                                disabledContainerColor = DisableGray,
                                disabledBorderColor = DarkGray,
                                disabledTextColor = DarkGray
                            ),
                            placeholder = {
                                Text(
                                    text = "년도",
                                    fontSize = 16.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            modifier = Modifier.weight(0.6f),
                            value = if (salary.amount == 0) "" else "${salary.amount}",
                            onValueChange = {
                                onEvent(EmployeeAddEvent.ChangedSalaryWith(SalaryField.AMOUNT, it, idx))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = DarkGray,
                                focusedBorderColor = DarkGray,
                                disabledContainerColor = DisableGray,
                                disabledBorderColor = DarkGray,
                                disabledTextColor = DarkGray
                            ),
                            placeholder = {
                                Text(
                                    text = "금액",
                                    fontSize = 16.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        IconButton(
                            onClick = { onEvent(EmployeeAddEvent.ClickedDeleteSalaryWith(idx)) },
                            modifier = Modifier.weight(0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "연봉 아이템 삭제 버튼"
                            )
                        }
                    }
                }
            }
        }
    }
}

/* 부서 선택 디알로그 */
@Composable
private fun DepartmentDialog(
    employeeAddState: EmployeeAddState,
    onDismiss: () -> Unit = {},
    onEvent: (EmployeeAddEvent) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 30.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
        ) {
            SearchBar(
                searchUiState = SearchUiState(
                    value = employeeAddState.searchText,
                    onValueChange = { onEvent(EmployeeAddEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(EmployeeAddEvent.ClickedSearch)
//                        keyboardController?.hide()
//                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { EmployeeAddEvent.ClickedInitSearch }
                ),
                hint = "부서명"
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employeeAddState.dropDownMenu.departmentMenu) { item ->
                    DepartmentInfoItem(
                        name = item.name,
                        head = "부서장",   // TODO: 부서장 이름 출력 필요 (부서 목록 받을 때 부서장도 받기? 아니면 부서 검색에서만 받기?)
                        onClick = {
                            onEvent(EmployeeAddEvent.SelectedDepartmentWith(item.name, item.id))
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

/* 권한 선택 디알로그 */
@Composable
private fun AuthDialog( // TODO: 체크한 권한이 체크표시가 안되어있음, 부서 목록 안 뜸
    employeeAddState: EmployeeAddState,
    onDismiss: () -> Unit = {},
    onEvent: (EmployeeAddEvent) -> Unit
) {
    val myAuthorName = employeeAddState.inputData.authors    // 내가 가진 권한 목록
    val allAuthor = employeeAddState.authors                 // 전체 권한 목록
    var selected by remember { mutableStateOf(                  // 내가 가진 이름에 해당하는 권한들만 저장된 set
        allAuthor.filter { it.name in myAuthorName.toHashSet() }
            .toCollection(LinkedHashSet()))
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 30.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employeeAddState.authors) { item ->
                    val isChecked = item in selected
                    AuthItem(
                        info = item,
                        isChecked = isChecked,
                        onChecked = { checked ->
                            selected = LinkedHashSet(selected).apply {
                                if (checked) add(item) else remove(item)
                            }
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicButton(
                    name = "확인",
                    onClick = {
                        onEvent(EmployeeAddEvent.ClickedEditAuthWith(selected))
                        onDismiss()
                    }
                )
            }
        }
    }
}