package com.example.attendancemanagementapp.ui.hr.department.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Yellow
import com.example.attendancemanagementapp.ui.theme.DarkBlue
import com.example.attendancemanagementapp.ui.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.ui.util.rememberOnce

/* 부서 상세 화면 */
@Composable
fun DepartmentDetailScreen(navController: NavController, departmentViewModel: DepartmentViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val onEvent = departmentViewModel::onDetailEvent
    val departmentDetailState by departmentViewModel.departmentDetailState.collectAsState()

    var openDepartmentInfo by remember { mutableStateOf(true) }     // 부서 정보 카드 열림 여부
    var openDepartmentUserInfo by remember { mutableStateOf(true) } // 부서 사용자 정보 카드 열림 여부

    var openDeleteDialog by remember { mutableStateOf(false) }      // 삭제 확인 팝업창 열림 여부
    var openAddHeadDialog by remember { mutableStateOf(false) }     // 부서장 추가 팝업창 열림 여부
    var addIdName by remember { mutableStateOf("" to "") }          // 부서장으로 추가할 아이디, 이름

    var openBottomSheet by remember { mutableStateOf(false) }   // 직원 목록 바텀 시트 열림 상태

    if (openBottomSheet) {
        EmployeesBottomSheet(
            onEvent = onEvent,
            departmentDetailState = departmentDetailState,
            onClickSearch = {
                // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                onEvent(DepartmentDetailEvent.ClickedSearch)
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            },
            onDismiss = {
                openBottomSheet = false
                onEvent(DepartmentDetailEvent.InitAddEmployeeList)
            },
            onSaveAdd = {
                onEvent(DepartmentDetailEvent.ClickedSaveAddEmployee)
                openBottomSheet = false
            }
        )
    }

    if (openDeleteDialog) {
        BasicDialog(
            title = "${departmentDetailState.info.name} 부서를 삭제하시겠습니까?",
            onDismiss = { openDeleteDialog = false },
            onClickConfirm = { departmentViewModel.deleteDepartment() }
        )
    }

    if (openAddHeadDialog) {
        BasicDialog(
            title = "부서장을 추가하시겠습니까?",
            text = "이미 부서에는 부서장이 있습니다. (${departmentDetailState.selectedHead.joinToString(", ") { it.second }})",
            onDismiss = { openAddHeadDialog = false },
            onClickConfirm = { onEvent(DepartmentDetailEvent.SelectedHeadWith(false, addIdName)) }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "부서 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = {
                    if (departmentDetailState.users.isEmpty()) {  // 부서에 사용자가 없으면 삭제 가능
                        openDeleteDialog = true
                    }
                    else {
                        departmentViewModel.showSnackBar("부서를 삭제하기 전에 모든 사용자를 다른 부서로 이동시켜 주세요.")
//                        departmentViewModel.showSnackBar("${departmentDetailState.info.name} 부서에 ${departmentDetailState.users.size}명의 사용자가 있습니다. 부서를 삭제하기 전에 모든 사용자를 다른 부서로 이동시켜 주세요.")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            DepartmentInfoCard(
                departmentInfo = departmentDetailState.updateInfo,
                openDepartmentInfo = openDepartmentInfo,
                onClick = { openDepartmentInfo = !openDepartmentInfo },
                onClickUpdate = { departmentViewModel.updateDepartment() },
                onFieldChange = { field, input -> onEvent(DepartmentDetailEvent.ChangedValueWith(field, input)) }
            )
            DepartmentUserInfoCard(
                state = departmentDetailState,
                openDepartmentInfo = openDepartmentUserInfo,
                onClick = { openDepartmentUserInfo = !openDepartmentUserInfo },
                onChecked = { isChecked, id -> onEvent(DepartmentDetailEvent.SelectedSaveEmployeeWith(isChecked, id)) },
                onSelectHead = { isHead, idName ->
                    // 부서장이 이미 있는데 또 선택한 경우 팝업창 출력
                    if (!isHead && departmentDetailState.selectedHead.isNotEmpty()) {
                        addIdName = idName
                        openAddHeadDialog = true
                    }
                    else {
                        onEvent(DepartmentDetailEvent.SelectedHeadWith(isHead, idName))
                    }
                },
                onClickAdd = { openBottomSheet = true },
                onClickSave = { departmentViewModel.saveDepartmentUser() }
            )
        }
    }
}

/* 직원 추가 목록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeesBottomSheet(
    onEvent: (DepartmentDetailEvent) -> Unit,
    departmentDetailState: DepartmentDetailState,
    onClickSearch: () -> Unit,
    onDismiss: () -> Unit,
    onSaveAdd: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                searchState = SearchState(
                    value = departmentDetailState.searchText,
                    onValueChange = { onEvent(DepartmentDetailEvent.ChangedSearchWith(it)) },
                    onClickSearch = { onClickSearch() },
                    onClickInit = { onEvent(DepartmentDetailEvent.ClickedInitSearch) }
                ),
                hint = "직원명"
            )

            Spacer(Modifier.height(15.dp))
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 70.dp)
                ) {
                    departmentDetailState.employees.forEach { employeeInfo ->
                        item {
                            val isContain = departmentDetailState.users.any { it.id == employeeInfo.id }

                            EmployeeInfoItem(
                                name = employeeInfo.name,
                                deptGradeTitle = formatDeptGradeTitle(departmentDetailState.info.name, employeeInfo.grade, employeeInfo.title),
                                isContain = isContain,  // 사용자가 부서에 포함되어 있는지
                                isChecked = if (isContain) true else employeeInfo in departmentDetailState.selectedEmployees,
                                onChecked = { onEvent(DepartmentDetailEvent.SelectedAddEmployeeWith(it, employeeInfo)) }
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(70.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomEnd).padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    BasicButton(
                        name = "확인",
                        onClick = { onSaveAdd() }
                    )
                }
            }
        }
    }
}

/* 직원 추가 목록 아이템 */
@Composable
private fun EmployeeInfoItem(name: String, deptGradeTitle: String, isContain: Boolean, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 체크박스
            BasicCheckbox(
                enabled = !isContain,   // 포함 되어 있으면 비활성화
                isChecked = isChecked,
                onChecked = { onChecked(!it) }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).padding(end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(0.3f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = name,
                        fontSize = 16.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .horizontalScroll(rememberScrollState()),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = deptGradeTitle,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/* 부서 정보 카드 */
@Composable
fun DepartmentInfoCard(departmentInfo: DepartmentDTO.DepartmentInfo, openDepartmentInfo: Boolean, onClick: () -> Unit, onClickUpdate: () -> Unit, onFieldChange: (DepartmentField, String) -> Unit) {
    val rotation by animateFloatAsState(targetValue = if (openDepartmentInfo) 90f else 0f)

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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "원 + 체크 표시",
                    tint = DarkBlue,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "부서 정보",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "부서 정보 여닫기 버튼",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = openDepartmentInfo,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))

                    EditBar(
                        name = "상위부서",
                        value = departmentInfo.upperName ?: "",
                        onValueChange = {},
                        enabled = false
                    )

                    EditBar(
                        name = "부서명",
                        value = departmentInfo.name,
                        onValueChange = { onFieldChange(DepartmentField.NAME, it) },
                        isRequired = true
                    )

                    EditBar(
                        name = "부서설명",
                        value = departmentInfo.description ?: "",
                        onValueChange = { onFieldChange(DepartmentField.DESCRIPTION, it) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        BasicButton(
                            name = "수정",
                            onClick = { onClickUpdate() }
                        )
                    }
                }
            }
        }
    }
}

/* 부서 사용자 카드 */
@Composable
fun DepartmentUserInfoCard(
    state: DepartmentDetailState,
    openDepartmentInfo: Boolean,
    onClick: () -> Unit,
    onChecked: (Boolean, String) -> Unit,
    onSelectHead: (Boolean, Pair<String, String>) -> Unit,
    onClickAdd: () -> Unit,
    onClickSave: () -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (openDepartmentInfo) 90f else 0f)

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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "원 + 체크 표시",
                    tint = DarkBlue,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "부서 사용자",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "부서 사용자 정보 여닫기 버튼",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = openDepartmentInfo,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.users.isEmpty()) {
                        Row(
                            modifier = Modifier.padding(top = 10.dp, bottom = 70.dp)
                        ) {
                            Text(
                                text = "등록된 부서 사용자가 없습니다.",
                                fontSize = 15.sp,
                            )
                        }
                    }
                    else {
                        LazyColumn(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 70.dp)
                        ) {
                            items(state.users) { userInfo ->
                                val isChecked = userInfo.id in state.selectedSave               // 저장 목록 체크 여부
                                val isHead = state.selectedHead.any { it.first == userInfo.id } // 부서장 여부

                                DepartmentUserItem(
                                    userInfo = userInfo,
                                    departmentName = state.info.name,
                                    isChecked = isChecked,
                                    isHead = isHead,
                                    onChecked = { onChecked(it, userInfo.id) },
                                    onSelectHead = { onSelectHead(it, Pair(userInfo.id, userInfo.name)) }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().background(color = Color.White).align(Alignment.BottomEnd).padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SubButton(
                            name = "추가",
                            onClick = { onClickAdd() }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        BasicButton(
                            name = "저장",
                            onClick = { onClickSave() }
                        )
                    }
                }
            }
        }
    }
}

/* 부서 사용자 목록 아이템 */
@Composable
fun DepartmentUserItem(
    userInfo: DepartmentDTO.DepartmentUserInfo,
    departmentName: String,
    isChecked: Boolean, isHead: Boolean,
    onChecked: (Boolean) -> Unit,
    onSelectHead: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicCheckbox(
                isChecked = isChecked,
                onChecked = { onChecked(!it) }
            )

            IconButton(
                onClick = { onSelectHead(isHead) }
            ) {
                Icon(
                    imageVector = if (isHead) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "부서장 선택 아이콘",
                    tint = ApprovalInfoItem_Yellow
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = userInfo.name,
                fontSize = 15.sp
            )

            Text(
                text = formatDeptGradeTitle(departmentName, userInfo.grade, userInfo.title),
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}
