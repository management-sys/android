package com.example.attendancemanagementapp.ui.hr.department.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.util.rememberOnce

/* 부서 관리 화면 */
@Composable
fun DepartmentManageScreen(navController: NavController, departmentViewModel: DepartmentViewModel) {
    val onEvent = departmentViewModel::onManageEvent

    val departmentManageState by departmentViewModel.departmentManageState.collectAsState()

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
            // TODO: 드래그 가능한 목록으로 변경해야함
            LazyColumn {
                items(departmentManageState.departments) { department ->
                    DepartmentItem(
                        departmentInfo = department,
                        onClick = { id ->
                            onEvent(DepartmentManageEvent.SelectedDepartmentWith(id))
                            navController.navigate("departmentDetail")
                        }
                    )
                }
            }
        }
    }
}

/* 부서 목록 아이템(임시) */
@Composable
private fun DepartmentItem(departmentInfo: HrDTO.DepartmentsInfo, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = { onClick(departmentInfo.id) }
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = departmentInfo.name
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}
