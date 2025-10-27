package com.example.attendancemanagementapp.ui.hr.employee.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO.CareerInfo
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DateEditBar
import com.example.attendancemanagementapp.ui.components.DateEditDeleteBar
import com.example.attendancemanagementapp.ui.components.DropdownEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.PhoneEditBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.util.calculateCareerPeriod
import com.example.attendancemanagementapp.ui.util.rememberOnce
import kotlinx.coroutines.launch

/* 직원 수정 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(navController: NavController, employeeViewModel: EmployeeViewModel) {
    val onEvent = employeeViewModel::onEditEvent
    val employeeEditState by employeeViewModel.employeeEditState.collectAsState()
    val currentPage by employeeViewModel.currentPage.collectAsState()

    val tabs = listOf("기본정보", "연차정보", "경력정보", "연봉정보")
    val pagerState = rememberPagerState(initialPage = currentPage, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    var openDeptDialog by remember { mutableStateOf(false) }    // 부서 선택 팝업창 열림 상태
    var openAuthDialog by remember { mutableStateOf(false) }    // 권한 선택 팝업창 열림 상태

    if (openDeptDialog) {
        DepartmentDialog(
            employeeEditState = employeeEditState,
            onDismiss = { openDeptDialog = false },
            onEvent = onEvent
        )
    }

    if (openAuthDialog) {
        AuthDialog(
            employeeEditState = employeeEditState,
            onDismiss = { openAuthDialog = false },
            onEvent = onEvent
        )
    }

    LaunchedEffect(Unit) {
        onEvent(EmployeeEditEvent.Init)
    }
    
    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 수정",
                actIcon = Icons.Default.Edit,
                actTint = MainBlue,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = {
                    onEvent(EmployeeEditEvent.ChangedPage(pagerState.currentPage))
                    onEvent(EmployeeEditEvent.ClickedUpdate)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MainBlue
                    )
                }
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = pagerState.currentPage == idx,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                        },
                        text = { Text(title) },
                        selectedContentColor = MainBlue,
                        unselectedContentColor = Color.Black
                    )
                }
            }

            Box(Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 기본정보
                                EmployeeEditCard(
                                    employeeEditState = employeeEditState,
                                    onEvent = onEvent,
                                    onOpenAuth = { openAuthDialog = true },
                                    onOpenDept = { openDeptDialog = true }
                                )
                            }
                            1 -> {  // 연차정보
                                AnnualLeaveEditCard(
                                    employeeEditState = employeeEditState,
                                    onEvent = onEvent
                                )
                            }
                            2 -> {  // 경력정보
                                CareerEditCard(
                                    careers = employeeEditState.careerInfo,
//                                    careers = employeeEditState.inputData.careers,
                                    onEvent = onEvent
                                )
                            }
                            3 -> {  // 연봉정보
                                SalaryEditCard(
                                    salaries = employeeEditState.inputData.salaries,
                                    onEvent = onEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 직원 상세 정보 수정 카드 */
@Composable
private fun EmployeeEditCard(employeeEditState: EmployeeEditState, onEvent: (EmployeeEditEvent) -> Unit, onOpenAuth: () -> Unit, onOpenDept: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
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
                value = employeeEditState.inputData.loginId,
                onValueChange = {},
                enabled = false,
                isRequired = true
            )
            SearchEditBar(
                name = "권한",
                value = employeeEditState.selectAuthor.joinToString("/") { it.name },
                onClick = { onOpenAuth() },
                isRequired = true,
                enabled = true,
                icon = Icons.Default.AddCircle
            )
            EditBar(
                name = "이름",
                value = employeeEditState.inputData.name,
                onValueChange = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.NAME, it)) },
                isRequired = true
            )
            SearchEditBar(
                name = "부서",
                value = employeeEditState.inputData.department,
                onClick = { onOpenDept() },
                isRequired = true,
                enabled = true
            )
            DropdownEditBar(
                name = "직급",
                options = employeeEditState.dropDownMenu.gradeMenu,
                selected = employeeEditState.inputData.grade,
                onSelected = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.GRADE, it)) },
                isRequired = true
            )
            DropdownEditBar(
                name = "직책",
                options = employeeEditState.dropDownMenu.titleMenu,
                selected = employeeEditState.inputData.title ?: "직책",
                onSelected = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.TITLE, it)) }
            )
            PhoneEditBar(
                name = "연락처",
                value = employeeEditState.inputData.phone ?: "",
                onValueChange = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.PHONE, it)) }
            )
            DateEditDeleteBar(
                name = "생년월일",
                value = employeeEditState.inputData.birthDate ?: "",
                onClick = { onEvent(EmployeeEditEvent.ClickedInitBirthDate) },
                onValueChange = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.BIRTHDATE, it)) }
            )
            DateEditBar(
                name = "입사일",
                value = employeeEditState.inputData.hireDate,
                onValueChange = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.HIREDATE, it)) },
                isRequired = true
            )
        }
    }
}

/* 연차 정보 수정 카드 */
@Composable
private fun AnnualLeaveEditCard(employeeEditState: EmployeeEditState, onEvent: (EmployeeEditEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoBar(name = "연차", value = "${employeeEditState.annualLeaveInfo[0]}년차")
            InfoBar(name = "시작일", value = employeeEditState.annualLeaveInfo[1])
            InfoBar(name = "종료일", value = employeeEditState.annualLeaveInfo[2])
            EditBar(
                name = "연차 개수",
                value = employeeEditState.annualLeaveInfo[3],
                onValueChange = { onEvent(EmployeeEditEvent.ChangedValueWith(EmployeeEditField.ANNUAL_LEAVE, it))},
            )
            InfoBar(name = "이월 연차 개수", value = employeeEditState.annualLeaveInfo[4])
            InfoBar(name = "사용 연차 개수", value = employeeEditState.annualLeaveInfo[5])
        }
    }
}

/* 경력 정보 수정 카드 TODO: 수정 불가한 경력은 재직중이라서? */
@Composable
private fun CareerEditCard(careers: List<CareerInfo>, onEvent: (EmployeeEditEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "경력",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onEvent(EmployeeEditEvent.ClickedAddCareer) }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "경력 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            careers.forEachIndexed { idx, career ->
                CareerInfoEditItem(
                    info = career,
                    idx = idx,
                    onEvent = onEvent
                )

                if(idx < careers.size - 1) {
                    Divider(modifier = Modifier.padding(vertical = 5.dp))
                }
            }
        }
    }
}

/* 경력 수정 아이템 */
@Composable
private fun CareerInfoEditItem(info: CareerInfo, idx: Int, onEvent: (EmployeeEditEvent) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 90f else 0f)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp).padding(top = 13.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "회사명",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.35f)
            )

            OutlinedTextField(
                modifier = Modifier.weight(0.5f),
                value = info.name,
                onValueChange = { onEvent(EmployeeEditEvent.ChangedCareerWith(CareerField.NAME, it, idx)) },
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
                        text = "회사명",
                        fontSize = 16.sp
                    )
                },
                enabled = if (info.resignDate.isNullOrBlank() && info.id != null) false else true
            )

            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "경력 아이템 열림/닫힘 아이콘",
                    modifier = Modifier
                        .size(12.dp)
                        .rotate(rotation),
                    tint = DarkGray
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column() {
                DateEditBar(
                    name = "입사일",
                    value = info.hireDate,
                    onValueChange = { onEvent(EmployeeEditEvent.ChangedCareerWith(CareerField.HIREDATE, it, idx)) },
                    enabled = if (info.resignDate.isNullOrBlank() && info.id != null) false else true
                )

                if (!info.resignDate.isNullOrBlank() || info.id == null) {
                    DateEditBar(
                        name = "퇴사일",
                        value = info.resignDate ?: "",
                        onValueChange = { onEvent(EmployeeEditEvent.ChangedCareerWith(CareerField.RESIGNDATE, it, idx)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "기간",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.35f)
                    )

                    Text(
                        text = if (info.resignDate.isNullOrBlank() && info.id != null) "${calculateCareerPeriod(info.hireDate, info.resignDate)} (재직중)" else calculateCareerPeriod(info.hireDate, info.resignDate),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(0.5f)
                    )

                    IconButton(
                        onClick = { onEvent(EmployeeEditEvent.ClickedDeleteCareerWith(idx)) },
                        modifier = Modifier.weight(0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "경력 아이템 삭제 버튼"
                        )
                    }
                }
            }
        }
    }
}

/* 연봉 정보 수정 카드 */
@Composable
private fun SalaryEditCard(salaries: List<EmployeeDTO.SalaryInfo>, onEvent: (EmployeeEditEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "연봉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { onEvent(EmployeeEditEvent.ClickedAddSalary) }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "연봉 아이템 추가 버튼",
                        tint = MainBlue
                    )
                }
            }

            salaries.forEachIndexed { idx, salary ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
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
                                onEvent(EmployeeEditEvent.ChangedSalaryWith(SalaryField.YEAR, it, idx))
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
                                onEvent(EmployeeEditEvent.ChangedSalaryWith(SalaryField.AMOUNT, it, idx))
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
                            onClick = { onEvent(EmployeeEditEvent.ClickedDeleteSalaryWith(idx)) },
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
    employeeEditState: EmployeeEditState,
    onDismiss: () -> Unit = {},
    onEvent: (EmployeeEditEvent) -> Unit
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
                searchState = SearchState(
                    value = employeeEditState.searchText,
                    onValueChange = { onEvent(EmployeeEditEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        onEvent(EmployeeEditEvent.ClickedSearch)
//                        keyboardController?.hide()
//                        focusManager.clearFocus(force = true)
                    },
                    onClickInit = { EmployeeEditEvent.ClickedInitSearch }
                ),
                hint = "부서명"
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employeeEditState.dropDownMenu.departmentMenu) { item ->
                    DepartmentInfoItem(
                        name = item.name,
                        head = "부서장",   // TODO: 부서장 이름 출력 필요 (부서 목록 받을 때 부서장도 받기? 아니면 부서 검색에서만 받기?)
                        onClick = {
                            onEvent(EmployeeEditEvent.SelectedDepartmentWith(item.name, item.id))
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
fun DepartmentInfoItem(name: String, head: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
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
    employeeEditState: EmployeeEditState,
    onDismiss: () -> Unit = {},
    onEvent: (EmployeeEditEvent) -> Unit
) {
    val myAuthorName = employeeEditState.inputData.authors    // 내가 가진 권한 이름 리스트
    val allAuthor = employeeEditState.authors                 // 전체 권한 목록
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
                items(employeeEditState.authors) { item ->
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
                        onEvent(EmployeeEditEvent.ClickedEditAuthWith(selected))
                        onDismiss()
                    }
                )
            }
        }
    }
}

/* 권한 목록 아이템 */
@Composable
fun AuthItem(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
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