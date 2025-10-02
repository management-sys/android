package com.example.attendancemanagementapp.ui.hr.employee.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.util.rememberOnce

/* 직원 등록 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddScreen(navController: NavController, hrViewModel: HrViewModel) {
    Scaffold(
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

        }
    }
}