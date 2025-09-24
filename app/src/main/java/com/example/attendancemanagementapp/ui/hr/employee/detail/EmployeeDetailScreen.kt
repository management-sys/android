package com.example.attendancemanagementapp.ui.hr.employee.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTextButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.util.rememberOnce

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
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            Spacer(modifier = Modifier.height(30.dp))
            InfoBar(name = "아이디", value = employeeDetailUiState.employeeInfo.id)
            InfoBar(name = "권한", value = employeeDetailUiState.employeeInfo.authors.joinToString(", "))
            InfoBar(name = "이름", value = employeeDetailUiState.employeeInfo.name)
            InfoBar(name = "부서", value = employeeDetailUiState.employeeInfo.department)
            InfoBar(name = "직급", value = employeeDetailUiState.employeeInfo.grade)
            InfoBar(name = "직책", value = employeeDetailUiState.employeeInfo.title ?: "")
            InfoBar(name = "연락처", value = employeeDetailUiState.employeeInfo.phone ?: "")
            InfoBar(name = "생년월일", value = employeeDetailUiState.employeeInfo.birthDate ?: "")
            InfoBar(name = "입사일", value = employeeDetailUiState.employeeInfo.hireDate)
            InfoBar(name = "연봉", value = "${employeeDetailUiState.employeeInfo.salary}")    // TODO: 출력 형식 확인 필요

            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicTextButton(
                    name = "비밀번호 초기화",
                    onClick = { openChangeDialog = true }
                )

                BasicButton(
                    name = "수정",
                    onClick = { navController.navigate("employeeEdit") }
                )
            }
        }
    }
}