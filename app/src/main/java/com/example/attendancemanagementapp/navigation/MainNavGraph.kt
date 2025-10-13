package com.example.attendancemanagementapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.code.CodeViewModel
import com.example.attendancemanagementapp.ui.code.add.CodeAddScreen
import com.example.attendancemanagementapp.ui.code.detail.CodeDetailScreen
import com.example.attendancemanagementapp.ui.code.edit.CodeEditScreen
import com.example.attendancemanagementapp.ui.code.manage.CodeManageScreen
import com.example.attendancemanagementapp.ui.components.BasicDrawer
import com.example.attendancemanagementapp.ui.home.HomeScreen
import com.example.attendancemanagementapp.ui.home.attendance.AttendanceViewModel
import com.example.attendancemanagementapp.ui.home.calendar.CalendarViewModel
import com.example.attendancemanagementapp.ui.hr.employee.HrViewModel
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailScreen
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageScreen
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddScreen
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailScreen
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditScreen
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageScreen
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController()) {
    var allowOpen by remember { mutableStateOf(false) }  // 코드로 열 때만 true
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
        confirmStateChange = { target ->
            when (target) {
                DrawerValue.Open   -> allowOpen      // 스와이프/사용자 동작으로 열기 차단
                DrawerValue.Closed -> true           // 스크림 탭/스와이프로 닫기 허용
            }
        }
    )

    val scope = rememberCoroutineScope()
    
    val codeViewModel: CodeViewModel = hiltViewModel()
    val hrViewModel: HrViewModel = hiltViewModel()
    val departmentViewModel: DepartmentViewModel = hiltViewModel()
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val attendanceViewModel: AttendanceViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(codeViewModel, hrViewModel, departmentViewModel, attendanceViewModel) {
        merge(
            codeViewModel.snackbar,
            hrViewModel.snackbar,
            departmentViewModel.snackbar,
            attendanceViewModel.snackbar
        ).collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }

    BasicDrawer(
        drawerState = drawerState,
        onItemClick = { route ->
            navController.navigate(route)
            scope.launch { drawerState.close() }
        },
        onLogoutClick = { /* TODO: [기능] 로그아웃 */ }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
    //            startDestination = RootRoute.HR.route,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home")  { HomeScreen(navController, calendarViewModel, attendanceViewModel) { scope.launch { drawerState.open() } } }

                composable("codeManage") { CodeManageScreen(navController, codeViewModel) } // 공통코드 관리 화면
                composable("codeDetail") { CodeDetailScreen(navController, codeViewModel) } // 공통코드 상세 화면
                composable("codeEdit") { CodeEditScreen(navController, codeViewModel) }     // 공통코드 수정 화면
                composable("codeAdd") { CodeAddScreen(navController, codeViewModel) }       // 공통코드 등록 화면

                composable("employeeManage") { EmployeeManageScreen(navController, hrViewModel) }   // 직원 관리 화면
                composable("employeeSearch") { EmployeeSearchScreen(navController, hrViewModel) }   // 직원 검색 화면
                composable("employeeDetail") { EmployeeDetailScreen(navController, hrViewModel) }   // 직원 상세 화면
                composable("employeeEdit") { EmployeeEditScreen(navController, hrViewModel) }       // 직원 수정 화면
                composable("employeeAdd") { EmployeeAddScreen(navController, hrViewModel) }         // 직원 등록 화면

                composable("departmentManage") { DepartmentManageScreen(navController, departmentViewModel) }   // 부서 관리 화면
                composable("departmentDetail") { DepartmentDetailScreen(navController, departmentViewModel) }   // 부서 상세 화면
            }
        }
    }
}