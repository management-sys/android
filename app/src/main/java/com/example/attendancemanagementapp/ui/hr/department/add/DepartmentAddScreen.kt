package com.example.attendancemanagementapp.ui.hr.department.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentField
import com.example.attendancemanagementapp.ui.theme.DarkBlue
import com.example.attendancemanagementapp.util.rememberOnce

/* 부서 등록 화면 */
@Composable
fun DepartmentAddScreen(navController: NavController, departmentViewModel: DepartmentViewModel) {
    val onEvent = departmentViewModel::onAddEvent
    val departmentAddState by departmentViewModel.departmentAddState.collectAsState()

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "부서 등록",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            DepartmentAddCard(
                departmentAddState = departmentAddState,
                onClickAdd = {
                    departmentViewModel.addDepartment()
                },
                onFieldChange = { field, input -> onEvent(DepartmentAddEvent.ChangedValueWith(field, input)) }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(DepartmentAddEvent.Init)
        }
    }
}

/* 부서 등록 카드 */
@Composable
private fun DepartmentAddCard(departmentAddState: DepartmentAddState, onClickAdd: () -> Unit, onFieldChange: (DepartmentField, String) -> Unit) {
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
                modifier = Modifier.fillMaxWidth(),
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
            }

            Column {
                Spacer(modifier = Modifier.height(10.dp))

                EditBar(
                    name = "상위부서",
                    value = departmentAddState.upperName,
                    onValueChange = {},
                    enabled = false
                )

                EditBar(
                    name = "부서명",
                    value = departmentAddState.inputData.name,
                    onValueChange = { onFieldChange(DepartmentField.NAME, it) },
                    isRequired = true
                )

                EditBar(
                    name = "부서설명",
                    value = departmentAddState.inputData.description ?: "",
                    onValueChange = { onFieldChange(DepartmentField.DESCRIPTION, it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BasicButton(
                        name = "등록",
                        onClick = { onClickAdd() }
                    )
                }
            }
        }
    }
}
