package com.example.attendancemanagementapp.ui.hr.employee.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO.CareerInfo
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DateEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDateEditDeleteBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLinePhoneEditBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoLineDateEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.hr.employee.edit.AuthItem
import com.example.attendancemanagementapp.ui.hr.employee.edit.CareerField
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.hr.employee.edit.SalaryField
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddEvent
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.calculateCareerPeriod
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/* 직원 등록 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddScreen(navController: NavController, employeeViewModel: EmployeeViewModel) {
    val onEvent = employeeViewModel::onAddEvent
    val employeeAddState by employeeViewModel.employeeAddState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    val tabs = listOf("기본정보", "경력정보", "연봉정보")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

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
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "직원 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
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
                            0 -> {  // 기본 정보
                                EmployeeEditCard(
                                    employeeAddState = employeeAddState,
                                    onEvent = onEvent,
                                    onOpenAuth = { openAuthDialog = true },
                                    onOpenDept = { openDeptDialog = true }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )
                                }
                            }
                            1 -> {  // 경력 정보
                                CareerEditCard(
                                    careers = employeeAddState.inputData.careers,
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
                                    )

                                    BasicButton(
                                        name = "다음",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                                    )
                                }
                            }
                            2 -> {  // 연봉 정보
                                SalaryEditCard(
                                    salaries = employeeAddState.inputData.salaries,
                                    onEvent = onEvent
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    SubButton(
                                        name = "이전",
                                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                                    )

                                    BasicButton(
                                        name = "추가",
                                        onClick = { onEvent(EmployeeAddEvent.ClickedAdd) }
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.height(40.dp)
                        )
                    }
                }
            }
        }
    }
}

/* 직원 상세 정보 수정 카드 */
@Composable
private fun EmployeeEditCard(employeeAddState: EmployeeAddState, onEvent: (EmployeeAddEvent) -> Unit, onOpenAuth: () -> Unit, onOpenDept: () -> Unit) {
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

            TwoLineEditBar(
                name = "아이디",
                value = employeeAddState.inputData.loginId,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.LOGINID, it)) },
                isRequired = true
            )

            TwoLineSearchEditBar(
                name = "권한",
                value = if (employeeAddState.selectAuthor.joinToString("/") { it.name } == "") employeeAddState.inputData.authors.joinToString(", ") else employeeAddState.selectAuthor.joinToString("/") { it.name },
                onClick = { onOpenAuth() },
                isRequired = true,
                enabled = true,
                icon = Icons.Default.AddCircle
            )

            TwoLineEditBar(
                name = "이름",
                value = employeeAddState.inputData.name,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.NAME, it)) },
                isRequired = true
            )

            TwoLineSearchEditBar(
                name = "부서",
                value = employeeAddState.inputData.department,
                onClick = { onOpenDept() },
                isRequired = true,
                enabled = true
            )

            TwoLineDropdownEditBar(
                name = "직급",
                options = employeeAddState.dropDownMenu.gradeMenu,
                selected = employeeAddState.inputData.grade,
                onSelected = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.GRADE, it)) },
                isRequired = true
            )

            TwoLineDropdownEditBar(
                name = "직책",
                options = employeeAddState.dropDownMenu.titleMenu,
                selected = employeeAddState.inputData.title ?: "직책",
                onSelected = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.TITLE, it)) }
            )

            TwoLinePhoneEditBar(
                name = "연락처",
                value = employeeAddState.inputData.phone ?: "",
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.PHONE, it)) }
            )

            TwoLineDateEditDeleteBar(
                name = "생년월일",
                value = employeeAddState.inputData.birthDate ?: "",
                onClick = { onEvent(EmployeeAddEvent.ClickedInitBirthDate) },
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.BIRTHDATE, it)) }
            )

            TwoLineDateEditBar(
                name = "입사일",
                value = employeeAddState.inputData.hireDate,
                onValueChange = { onEvent(EmployeeAddEvent.ChangedValueWith(EmployeeAddField.HIREDATE, it)) },
                isRequired = true
            )
        }
    }
}

/* 경력 정보 수정 카드 */
@Composable
private fun CareerEditCard(careers: List<CareerInfo>, onEvent: (EmployeeAddEvent) -> Unit) {
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
                    onClick = { onEvent(EmployeeAddEvent.ClickedAddCareer) }
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
private fun CareerInfoEditItem(info: CareerInfo, idx: Int, onEvent: (EmployeeAddEvent) -> Unit) {
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
                onValueChange = { onEvent(EmployeeAddEvent.ChangedCareerWith(CareerField.NAME, it, idx)) },
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
                }
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
                    onValueChange = { onEvent(EmployeeAddEvent.ChangedCareerWith(CareerField.HIREDATE, it, idx)) }
                )

                DateEditBar(
                    name = "퇴사일",
                    value = info.resignDate ?: "",
                    onValueChange = { onEvent(EmployeeAddEvent.ChangedCareerWith(CareerField.RESIGNDATE, it, idx)) }
                )

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

                    if (!info.resignDate.isNullOrBlank() || info.id == null) {
                        IconButton(
                            onClick = { onEvent(EmployeeAddEvent.ClickedDeleteCareerWith(idx)) },
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
}

/* 연봉 정보 수정 카드 */
@Composable
private fun SalaryEditCard(salaries: List<EmployeeDTO.SalaryInfo>, onEvent: (EmployeeAddEvent) -> Unit) {
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
                    onClick = { onEvent(EmployeeAddEvent.ClickedAddSalary) }
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
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !employeeAddState.paginationState.isLoading && employeeAddState.paginationState.currentPage < employeeAddState.paginationState.totalPage) {
                onEvent(EmployeeAddEvent.LoadNextPage)
            }
        }
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
            SearchBar(
                searchState = SearchState(
                    value = employeeAddState.searchText,
                    onValueChange = { onEvent(EmployeeAddEvent.ChangedSearchWith(it)) },
                    onClickSearch = {
                        if (employeeAddState.paginationState.currentPage <= employeeAddState.paginationState.totalPage) {
                            onEvent(EmployeeAddEvent.ClickedSearch)
                        }
                    },
                    onClickInit = { onEvent(EmployeeAddEvent.ClickedInitSearch) }
                ),
                hint = "부서명"
            )

            if (employeeAddState.dropDownMenu.departmentMenu.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "조회된 결과가 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                Spacer(Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    items(employeeAddState.dropDownMenu.departmentMenu) { item ->
                        DepartmentInfoItem(
                            name = item.name,
                            head = item.headName ?: "",
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
}

/* 권한 선택 디알로그 */
@Composable
private fun AuthDialog(
    employeeAddState: EmployeeAddState,
    onDismiss: () -> Unit = {},
    onEvent: (EmployeeAddEvent) -> Unit
) {
    val myAuthorName = employeeAddState.inputData.authors    // 내가 가진 권한 목록
    val allAuthor = employeeAddState.authors                 // 전체 권한 목록
    var selected by remember { mutableStateOf(               // 내가 가진 이름에 해당하는 권한들만 저장된 set
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