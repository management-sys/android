package com.example.attendancemanagementapp.ui.hr.employee.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTextButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.util.rememberOnce
import java.util.Locale

/* 직원 상세 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(navController: NavController, employeeViewModel: EmployeeViewModel) {
    val onEvent = employeeViewModel::onDetailEvent

    var openDeleteDialog by remember { mutableStateOf(false) }    // 탈퇴 확인 디알로그 열림 상태
    var openChangeDialog by remember { mutableStateOf(false) }    // 비밀번호 초기화 확인 디알로그 열림 상태

    val employeeDetailState by employeeViewModel.employeeDetailState.collectAsState()

    if (openDeleteDialog) {
        if (employeeDetailState.employeeInfo.isUse == "Y") {
            BasicDialog(
                title = "사용자를 탈퇴시키겠습니까?",
                text = "이 작업은 되돌릴 수 없습니다.",
                onDismiss = {
                    onEvent(EmployeeDetailEvent.ClickedDismissDeactivate)
                    openDeleteDialog = false
                },
                onClickConfirm = {
                    onEvent(EmployeeDetailEvent.ClickedDeactivate)
                }
            )
        }
        else {
            BasicDialog(
                title = "사용자를 복구시키겠습니까?",
                text = "이 작업은 되돌릴 수 없습니다.",
                onDismiss = {
                    onEvent(EmployeeDetailEvent.ClickedDismissActivate)
                    openDeleteDialog = false
                },
                onClickConfirm = {
                    onEvent(EmployeeDetailEvent.ClickedActivate)
                }
            )
        }
    }

    if (openChangeDialog) {
        BasicDialog(
            title = "비밀번호를 초기화하시겠습니까?",
            onDismiss = { openChangeDialog = false },
            onClickConfirm = { onEvent(EmployeeDetailEvent.ClickedResetPassword) }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 상세",
                actIcon = if (employeeDetailState.employeeInfo.isUse == "Y") Icons.Default.Delete else Icons.Default.RestoreFromTrash
                ,
                actTint = if (employeeDetailState.employeeInfo.isUse == "Y") Color.Red else MainBlue,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDeleteDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 26.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            EmployeeInfoCard(employeeDetailState)
            SalaryInfoCard(employeeDetailState.employeeInfo.salaries)
            UpdateInitButtons(
                isUse = employeeDetailState.employeeInfo.isUse,
                onClickUpdate = { navController.navigate("employeeEdit") },
                onClickInitPw = { openChangeDialog = true }
            )
        }
    }
}

/* 직원 상세 정보 출력 카드 */
@Composable
private fun EmployeeInfoCard(employeeDetailState: EmployeeDetailState) {
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
            ProfileImage()
            Spacer(modifier = Modifier.height(20.dp))
            InfoBar(name = "아이디", value = employeeDetailState.employeeInfo.loginId)
            InfoBar(name = "권한", value = employeeDetailState.employeeInfo.authors.joinToString(", "))
            InfoBar(name = "이름", value = employeeDetailState.employeeInfo.name)
            InfoBar(name = "부서", value = employeeDetailState.employeeInfo.department)
            InfoBar(name = "직급", value = employeeDetailState.employeeInfo.grade)
            InfoBar(name = "직책", value = employeeDetailState.employeeInfo.title ?: "")
            InfoBar(name = "연락처", value = employeeDetailState.employeeInfo.phone ?: "")
            InfoBar(name = "생년월일", value = employeeDetailState.employeeInfo.birthDate ?: "")
            InfoBar(name = "입사일", value = employeeDetailState.employeeInfo.hireDate)
        }
    }
}

/* 연봉 정보 출력 카드 */
@Composable
private fun SalaryInfoCard(salaries: List<EmployeeDTO.SalaryInfo>) {
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "연봉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            for (salary in salaries) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "•   ${salary.year}년",
                        fontSize = 16.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%,d", salary.amount),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

/* 수정 버튼, 비밀번호 초기화 버튼 */
@Composable
private fun UpdateInitButtons(isUse: String, onClickUpdate: () -> Unit, onClickInitPw: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextButton(
            name = "비밀번호 초기화",
            onClick = { onClickInitPw() }
        )

        if (isUse == "Y") {
            BasicButton(
                name = "수정",
                onClick = { onClickUpdate() }
            )
        }
    }
}