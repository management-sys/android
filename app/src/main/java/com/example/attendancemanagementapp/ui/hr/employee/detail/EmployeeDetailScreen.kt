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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTextButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.hr.employee.HrViewModel
import com.example.attendancemanagementapp.ui.util.rememberOnce
import java.util.Locale

@Preview
@Composable
private fun Preview_EmployeeDetailScreen() {
    val navController = rememberNavController()
    val hrViewModel: HrViewModel = viewModel()
    EmployeeDetailScreen(navController, hrViewModel)
}

/* 직원 상세 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(navController: NavController, hrViewModel: HrViewModel) {
    var openDeleteDialog by remember { mutableStateOf(false) }    // 탈퇴 확인 디알로그 열림 상태
    var openChangeDialog by remember { mutableStateOf(false) }    // 비밀번호 초기화 확인 디알로그 열림 상태

    val employeeDetailUiState by hrViewModel.employeeDetailUiState.collectAsState()

    if (openDeleteDialog) {
        BasicDialog(
            title = "사용자를 탈퇴시키겠습니까?",
            text = "이 작업은 되돌릴 수 없습니다.",
            onDismiss = {
                openDeleteDialog = false
            },
            onClickConfirm = {
                /* TODO: [기능] 직원 탈퇴 */
            }
        )
    }

    if (openChangeDialog) {
        BasicDialog(
            title = "비밀번호를 초기화하시겠습니까?",
            onDismiss = {
                openChangeDialog = false
            },
            onClickConfirm = {
                /* TODO: [기능] 비밀번호 초기화 */
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
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
            EmployeeInfoCard(employeeDetailUiState)
            SalaryInfoCard(employeeDetailUiState.employeeInfo.salaries)
            UpdateInitButtons(
                onClickUpdate = { navController.navigate("employeeEdit") },
                onClickInitPw = { openChangeDialog = true }
            )
        }
    }
}

/* 직원 상세 정보 출력 카드 */
@Composable
private fun EmployeeInfoCard(employeeDetailUiState: EmployeeDetailUiState) {
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
            InfoBar(name = "아이디", value = employeeDetailUiState.employeeInfo.id)
            InfoBar(name = "권한", value = employeeDetailUiState.employeeInfo.authors.joinToString(", "))
            InfoBar(name = "이름", value = employeeDetailUiState.employeeInfo.name)
            InfoBar(name = "부서", value = employeeDetailUiState.employeeInfo.department)
            InfoBar(name = "직급", value = employeeDetailUiState.employeeInfo.grade)
            InfoBar(name = "직책", value = employeeDetailUiState.employeeInfo.title ?: "")
            InfoBar(name = "연락처", value = employeeDetailUiState.employeeInfo.phone ?: "")
            InfoBar(name = "생년월일", value = employeeDetailUiState.employeeInfo.birthDate ?: "")
            InfoBar(name = "입사일", value = employeeDetailUiState.employeeInfo.hireDate)
        }
    }
}

/* 연봉 정보 출력 카드 */
@Composable
private fun SalaryInfoCard(salaries: List<HrDTO.SalaryInfo>) {
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
private fun UpdateInitButtons(onClickUpdate: () -> Unit, onClickInitPw: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextButton(
            name = "비밀번호 초기화",
            onClick = { onClickInitPw() }
        )

        BasicButton(
            name = "수정",
            onClick = { onClickUpdate() }
        )
    }
}