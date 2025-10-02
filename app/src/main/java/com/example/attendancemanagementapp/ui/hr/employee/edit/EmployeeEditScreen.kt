package com.example.attendancemanagementapp.ui.hr.employee.edit

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.DividerDefaults
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
import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DateEditBar
import com.example.attendancemanagementapp.ui.components.DateEditDeleteBar
import com.example.attendancemanagementapp.ui.components.DropdownEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.PhoneEditBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.ui.hr.EmployeeEditField
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.ui.hr.SalaryField
import com.example.attendancemanagementapp.ui.hr.UiEffect
import com.example.attendancemanagementapp.ui.hr.UiEvent
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.util.rememberOnce
import java.util.LinkedHashSet

/* 직원 수정 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(navController: NavController, hrViewModel: HrViewModel) {
    val onEvent = hrViewModel::onEvent
    val employeeEditUiState by hrViewModel.employeeEditUiState.collectAsState()
    
    var openDeptDialog by remember { mutableStateOf(false) }    // 부서 선택 팝업창 열림 상태
    var openAuthDialog by remember { mutableStateOf(false) }    // 권한 선택 팝업창 열림 상태

    if (openDeptDialog) {
        DepartmentDialog(
            employeeEditUiState = employeeEditUiState,
            onDismiss = { openDeptDialog = false },
            onEvent = onEvent
        )
    }

    if (openAuthDialog) {
        AuthDialog(
            employeeEditUiState = employeeEditUiState,
            onDismiss = { openAuthDialog = false },
            onEvent = onEvent
        )
    }

    LaunchedEffect(Unit) {
        onEvent(UiEvent.InitEditData)
    }

    LaunchedEffect(Unit) {
        hrViewModel.uiEffects.collect { effect ->
            when (effect) {
                UiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }
    
    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 수정",
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
                employeeEditUiState = employeeEditUiState,
                onEvent = onEvent,
                onOpenAuth = { openAuthDialog = true },
                onOpenDept = { openDeptDialog = true }
            )
            SalaryEditCard(
                salaries = employeeEditUiState.inputData.salaries,
                onEvent = onEvent
            )
            BasicLongButton(
                name = "수정",
                onClick = { onEvent(UiEvent.ClickUpdate) }
            )
        }
    }
}

/* 직원 상세 정보 수정 카드 */
@Composable
fun EmployeeEditCard(employeeEditUiState: EmployeeEditUiState, onEvent: (UiEvent) -> Unit, onOpenAuth: () -> Unit, onOpenDept: () -> Unit) {
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
                value = employeeEditUiState.inputData.id,
                onValueChange = {},
                enabled = false,
                isRequired = true
            )
            SearchEditBar(
                name = "권한",
                value = employeeEditUiState.selectAuthor.joinToString("/") { it.name },
                onClick = { onOpenAuth() },
                isRequired = true,
                enabled = true,
                icon = Icons.Default.AddCircle
            )
            EditBar(
                name = "이름",
                value = employeeEditUiState.inputData.name,
                onValueChange = { onEvent(UiEvent.ChangedValue(EmployeeEditField.NAME, it)) },
                isRequired = true
            )
            SearchEditBar(
                name = "부서",
                value = employeeEditUiState.inputData.department,
                onClick = { onOpenDept() },
                isRequired = true,
                enabled = true
            )
            DropdownEditBar(
                name = "직급",
                options = employeeEditUiState.dropDownMenu.gradeMenu,
                selected = employeeEditUiState.inputData.grade,
                onSelected = { onEvent(UiEvent.ChangedValue(EmployeeEditField.GRADE, it)) },
                isRequired = true
            )
            DropdownEditBar(
                name = "직책",
                options = employeeEditUiState.dropDownMenu.titleMenu,
                selected = employeeEditUiState.inputData.title ?: "직책",
                onSelected = { onEvent(UiEvent.ChangedValue(EmployeeEditField.TITLE, it)) }
            )
            PhoneEditBar(
                name = "연락처",
                value = employeeEditUiState.inputData.phone ?: "",
                onValueChange = { onEvent(UiEvent.ChangedValue(EmployeeEditField.PHONE, it)) }
            )
            DateEditDeleteBar(
                name = "생년월일",
                value = employeeEditUiState.inputData.birthDate ?: "",
                onClick = { onEvent(UiEvent.ClickInitBrth) },
                onValueChange = { onEvent(UiEvent.ChangedValue(EmployeeEditField.BIRTHDATE, it)) }
            )
            DateEditBar(
                name = "입사일",
                value = employeeEditUiState.inputData.hireDate,
                onValueChange = { onEvent(UiEvent.ChangedValue(EmployeeEditField.HIREDATE, it)) },
                isRequired = true
            )
        }
    }
}

/* 연봉 정보 수정 카드 */
@Composable
private fun SalaryEditCard(salaries: List<HrDTO.SalaryInfo>, onEvent: (UiEvent) -> Unit) {
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
                    onClick = { onEvent(UiEvent.ClickAddSalary) }
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
                                onEvent(UiEvent.ChangedSalary(SalaryField.YEAR, it, idx)
                                )
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
                                onEvent(UiEvent.ChangedSalary(SalaryField.AMOUNT, it, idx)
                                )
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
                            onClick = { onEvent(UiEvent.ClickDeleteSalary(idx)) },
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
    employeeEditUiState: EmployeeEditUiState,
    onDismiss: () -> Unit = {},
    onEvent: (UiEvent) -> Unit
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
                    value = employeeEditUiState.searchText,
                    onValueChange = { onEvent(UiEvent.SearchChanged(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(UiEvent.ClickSearch)
//                        keyboardController?.hide()
//                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { UiEvent.ClickInitSearch }
                ),
                hint = "부서명"
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employeeEditUiState.dropDownMenu.departmentMenu) { item ->
                    DepartmentInfoItem(
                        name = item.name,
                        head = "부서장",   // TODO: 부서장 이름 출력 (부서 목록 받을 때 부서장도 받기? 아니면 부서 검색에서만 받기?)
                        onClick = {
                            onEvent(UiEvent.SelectDepartment(item.name, item.id))
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

/* 부서 목록 아이템 */
@Composable
private fun DepartmentInfoItem(name: String, head: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(name, head)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/* 권한 선택 디알로그 */
@Composable
private fun AuthDialog(
    employeeEditUiState: EmployeeEditUiState,
    onDismiss: () -> Unit = {},
    onEvent: (UiEvent) -> Unit
) {
    val myAuthorName = employeeEditUiState.inputData.authors    // 내가 가진 권한 이름 리스트
    val allAuthor = employeeEditUiState.authors                 // 전체 권한 목록
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
                items(employeeEditUiState.authors) { item ->
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
                        onEvent(UiEvent.ClickSelectAuth(selected))
                        onDismiss()
                    }
                )
            }
        }
    }
}

/* 권한 목록 아이템 */
@Composable
private fun AuthItem(
    info: AuthorDTO.GetAuthorsResponse,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicCheckbox(
                isChecked = isChecked,
                onChecked = { onChecked(it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = info.code,
                    fontSize = 15.sp
                )

                Text(
                    text = info.name,
                    fontSize = 15.sp
                )
            }
        }
    }
}