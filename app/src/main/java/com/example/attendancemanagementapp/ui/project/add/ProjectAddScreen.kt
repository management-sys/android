package com.example.attendancemanagementapp.ui.project.add

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicCheckbox
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicOutlinedTextFieldColors
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.TwoLineBigEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar
import com.example.attendancemanagementapp.ui.components.TwoLineSearchEditBar
import com.example.attendancemanagementapp.ui.hr.employee.edit.DepartmentInfoItem
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeInfoItem
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.util.rememberOnce
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class BottomSheetType { NONE, DEPARTMENT, MANAGER, ASSIGNED_PERSONNEL }

/* 프로젝트 등록 화면 */
@Composable
fun ProjectAddScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onAddEvent

    val projectAddState by projectViewModel.projectAddState.collectAsState()

    var bottomSheetType by remember { mutableStateOf(BottomSheetType.NONE) }   // 바텀 시트 상태 (담당부서, 프로젝트 책임자, 투입인력)

    if (bottomSheetType != BottomSheetType.NONE) {
        ProjectBottomSheet(
            bottomSheetType = bottomSheetType,
            projectAddState = projectAddState,
            onEvent = onEvent,
            onDismiss = { bottomSheetType = BottomSheetType.NONE }
        )
    }

    LaunchedEffect(Unit) {
        onEvent(ProjectAddEvent.Init)
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "프로젝트 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            ProjectAddCard(
                projectAddState = projectAddState,
                onEvent = onEvent,
                onOpenDepartment = { bottomSheetType = BottomSheetType.DEPARTMENT },
                onOpenManager = { bottomSheetType = BottomSheetType.MANAGER },
                onOpenAssignedPersonnel = { bottomSheetType = BottomSheetType.ASSIGNED_PERSONNEL }
            )

            Box(
                modifier = Modifier.height(40.dp)
            )
        }
    }
}

/* 프로젝트 등록 카드 */
@Composable
fun ProjectAddCard(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onOpenDepartment: () -> Unit,
    onOpenManager: () -> Unit,
    onOpenAssignedPersonnel: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "구분",
                isRequired = true,
                options = listOf("용역", "내부", "국가과제"),   // TODO: 웹 확인 후 옵션 변경 필요
                selected = projectAddState.inputData.type,
                onSelected = { onEvent(ProjectAddEvent.SelectedTypeWith(it)) }
            )

            TwoLineEditBar(
                name = "프로젝트명",
                isRequired = true,
                value = projectAddState.inputData.projectName,
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PROJECT_NAME, it)) }
            )

            TwoLineEditBar(
                name = "주관기관",
                value = projectAddState.inputData.companyName ?: "",
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.COMPANY_NAME, it)) }
            )

            TwoLineSearchEditBar(
                name = "담당부서",
                isRequired = true,
                value = projectAddState.departmentName,
                onClick = { onOpenDepartment() }
            )

            TwoLineSearchEditBar(
                name = "프로젝트 책임자",
                isRequired = true,
                value = projectAddState.managerName,
                onClick = { onOpenManager() }
            )

            TwoLineEditBar(
                name = "사업비",
                value = projectAddState.inputData.businessExpense.toString(),
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_EXPENSE, it)) },
                onlyNumber = true
            )

            TwoLineEditBar(
                name = "회의비",
                value = projectAddState.inputData.meetingExpense.toString(),
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.MEETING_EXPENSE, it)) },
                onlyNumber = true
            )

            StartEndDateEditBar(
                name = "사업기간",
                startDate = projectAddState.inputData.businessStartDate,
                endDate = projectAddState.inputData.businessEndDate,
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.BUSINESS_END, it)) },
                isRequired = true
            )

            StartEndDateEditBar(
                name = "계획기간",
                startDate = projectAddState.inputData.planStartDate ?: "",
                endDate = projectAddState.inputData.planEndDate ?: "",
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PLAN_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.PLAN_END, it)) }
            )

            StartEndDateEditBar(
                name = "실제기간",
                startDate = projectAddState.inputData.realStartDate ?: "",
                endDate = projectAddState.inputData.realEndDate ?: "",
                onStartChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REAL_START, it)) },
                onEndChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REAL_END, it)) }
            )

            TwoLineSearchEditBar(
                name = "투입인력",
                value = if (projectAddState.checkedAssignedPersonnel.isEmpty()) "" else projectAddState.checkedAssignedPersonnel.joinToString("/") { it.name },
                onClick = { onOpenAssignedPersonnel() },
                isRequired = true,
                enabled = true,
                icon = Icons.Default.AddCircle
            )

            TwoLineBigEditBar(
                name = "비고",
                value = projectAddState.inputData.remark ?: "",
                onValueChange = { onEvent(ProjectAddEvent.ChangedValueWith(ProjectAddField.REMARK, it)) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            BasicLongButton(
                name = "등록",
                onClick = { onEvent(ProjectAddEvent.ClickedAdd) }
            )
        }
    }
}

/* 시작~끝 기간 선택 바 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartEndDateEditBar(
    name: String,
    startDate: String,
    endDate: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    isRequired: Boolean = false
) {
    var openStart by rememberSaveable { mutableStateOf(false) }
    var openEnd by rememberSaveable { mutableStateOf(false) }

    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA) }

    if (openStart) {
        val startMillis = remember(startDate) {
            runCatching { LocalDate.parse(startDate, fmt) }.getOrNull()
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        }

        val startPickerState = rememberDatePickerState(
            initialSelectedDateMillis = startMillis
        )

        DatePickerDialog(
            onDismissRequest = { openStart = false },
            confirmButton = {
                TextButton(onClick = {
                    startPickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC).toLocalDate()
                        onStartChange(picked.format(fmt))
                    }
                    openStart = false
                }) {
                    Text("확인", color = MainBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { openStart = false }) {
                    Text("취소", color = TextGray)
                }
            }
        ) {
            DatePicker(
                state = startPickerState,
                showModeToggle = true
            )
        }
    }

    if (openEnd) {
        val endMillis = remember(endDate) {
            runCatching { LocalDate.parse(endDate, fmt) }.getOrNull()
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        }

        val endPickerState = rememberDatePickerState(
            initialSelectedDateMillis = endMillis
        )

        DatePickerDialog(
            onDismissRequest = { openEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    endPickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC).toLocalDate()
                        onEndChange(picked.format(fmt))
                    }
                    openEnd = false
                }) {
                    Text("확인", color = MainBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { openEnd = false }) {
                    Text("취소", color = TextGray)
                }
            }
        ) {
            DatePicker(
                state = endPickerState,
                showModeToggle = true
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = startDate,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(5.dp),
                colors = BasicOutlinedTextFieldColors(),
                placeholder = {
                    Text(
                        text = startDate.ifBlank { "연도-월-일" },
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "캘린더 열기",
                        modifier =
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { openStart = true }
                    )
                }
            )

            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "~",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = endDate,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(5.dp),
                colors = BasicOutlinedTextFieldColors(),
                placeholder = {
                    Text(
                        text = endDate.ifBlank { "연도-월-일" },
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "캘린더 열기",
                        modifier =
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { openEnd = true }
                    )
                }
            )
        }
    }
}

/* 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectBottomSheet(
    bottomSheetType: BottomSheetType,
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        when (bottomSheetType) {
            BottomSheetType.DEPARTMENT -> {
                DepartmentBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent,
                    onDismiss = onDismiss
                )
            }
            BottomSheetType.MANAGER -> {
                ManagerBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent,
                    onDismiss = onDismiss
                )
            }
            BottomSheetType.ASSIGNED_PERSONNEL -> {
                AssignedPersonnelBottomSheetContent(
                    projectAddState = projectAddState,
                    onEvent = onEvent
                )
            }
            else -> {}
        }
    }
}

/* 담당부서 선택 바텀 시트 내용 */
@Composable
private fun DepartmentBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(projectAddState.departments) { item ->
                DepartmentInfoItem(
                    name = item.name,
                    head = item.headName ?: "",
                    onClick = {
                        onEvent(ProjectAddEvent.SelectedDepartmentWith(item))
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

/* 프로젝트 책임자 선택 바텀 시트 내용 */
@Composable
private fun ManagerBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(projectAddState.employees) { item ->
                EmployeeInfoItem(
                    name = item.name,
                    deptGradeTitle = formatDeptGradeTitle(item.department, item.grade, item.title),
                    onClick = {
                        onEvent(ProjectAddEvent.SelectedManagerWith(item))
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

/* 투입 인력 선택 바텀 시트 내용 */
@Composable
private fun AssignedPersonnelBottomSheetContent(
    projectAddState: ProjectAddState,
    onEvent: (ProjectAddEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(projectAddState.employees) { item ->
                val isChecked = item in projectAddState.checkedAssignedPersonnel
                EmployeeItem(
                    info = item,
                    isChecked = isChecked,
                    onChecked = { onEvent(ProjectAddEvent.CheckedAssignedPersonnelWith(it, item)) }
                )
            }

            item {
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}

/* 직원 목록 아이템 */
@Composable
private fun EmployeeItem(
    info: EmployeeDTO.EmployeesInfo,
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
                // TODO: 임시 출력
                Text(
                    text = info.name,
                    fontSize = 15.sp
                )

                Text(
                    text = info.department,
                    fontSize = 15.sp
                )
            }
        }
    }
}