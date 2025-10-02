package com.example.attendancemanagementapp.ui.hr.department.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.util.rememberOnce

/* 부서 관리 화면 */
@Composable
fun DepartmentManageScreen(navController: NavController, departmentViewModel: DepartmentViewModel) {
    val departmentManageUiState by departmentViewModel.departmentManageUiState.collectAsState()

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "부서 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("departmentDetail") }) { }
        }
    }
}