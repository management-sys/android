package com.example.attendancemanagementapp.ui.hr.employee.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.hr.HrViewModel
import com.example.attendancemanagementapp.util.rememberOnce

@Preview
@Composable
private fun Preview_EmployeeEditScreen() {
    val navController = rememberNavController()
    val hrViewModel: HrViewModel = viewModel()
    EmployeeEditScreen(navController, hrViewModel)
}

/* 직원 수정 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(navController: NavController, hrViewModel: HrViewModel) {
    Scaffold(
        topBar = {
            BasicTopBar(
                title = "직원 수정",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

///* 다중 선택 가능 칩 */
//@Composable
//fun MultiSelectChips(
//    options: List<String>,
//    selected: Set<String>,
//    onSelectionChange: (Set<String>) -> Unit
//) {
//    LazyRow(
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        options.forEach { option ->
//            val isSelected = option in selected
//            FilterChip(
//                selected = isSelected,
//                onClick = { onSelectionChange() },
//                label = {
//                    Text(
//                        text = option,
//                        fontSize = 14.sp
//                    )
//                },
//                colors = FilterChipDefaults.filterChipColors(
//                    containerColor = Color.White,
//                    selectedContainerColor = LightBlue,
//                    labelColor = Color.Black,
//                    selectedLabelColor = MiddleBlue
//                ),
//                border = FilterChipDefaults.filterChipBorder(
//                    enabled = true,
//                    selected = selected,
//                    borderWidth = 0.5.dp,
//                    borderColor = DarkGray,
//                    selectedBorderColor = MiddleBlue
//                )
//            )
//        }
//    }
//}