package com.example.attendancemanagementapp.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.base.CollectUiEffect
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddScreen
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailScreen
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditScreen
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageScreen
import com.example.attendancemanagementapp.ui.components.BasicDrawer
import com.example.attendancemanagementapp.ui.home.HomeScreen
import com.example.attendancemanagementapp.ui.home.attendance.AttendanceViewModel
import com.example.attendancemanagementapp.ui.home.calendar.CalendarViewModel
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.hr.department.add.DepartmentAddScreen
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailScreen
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageScreen
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddScreen
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailScreen
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditScreen
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageScreen
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchScreen
import com.example.attendancemanagementapp.ui.login.LoginScreen
import com.example.attendancemanagementapp.ui.login.LoginViewModel
import com.example.attendancemanagementapp.ui.mypage.MyPageScreen
import com.example.attendancemanagementapp.ui.mypage.MyPageViewModel
import kotlinx.coroutines.launch

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 홈 화면에서만 드로어 표시
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val enableGesture = currentRoute == "home"

    val codeViewModel: CodeViewModel = hiltViewModel()
    val employeeViewModel: EmployeeViewModel = hiltViewModel()
    val departmentViewModel: DepartmentViewModel = hiltViewModel()
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val attendanceViewModel: AttendanceViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val myPageViewModel: MyPageViewModel = hiltViewModel()

    LaunchedEffect(enableGesture) {
        Log.d("드로어", "showDrawer: ${enableGesture}")
        Log.d("드로어", "drawerState: ${drawerState.currentValue}")
    }

    CollectUiEffect(
        navController = navController,
        codeViewModel.uiEffect,
        employeeViewModel.uiEffect,
        departmentViewModel.uiEffect,
        calendarViewModel.uiEffect,
        attendanceViewModel.uiEffect
    )

    BasicDrawer(
        drawerState = drawerState,
        enableGesture = enableGesture,
        onItemClick = { route ->
            navController.navigate(route)
            scope.launch { drawerState.close() }
        },
        onLogoutClick = {
            scope.launch { drawerState.close() }
            /* TODO: [기능] 로그아웃 */
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) {
        Scaffold()
        { paddingValues ->
            NavHost(
                navController = navController,
    //            startDestination = RootRoute.HR.route,
                startDestination = "login",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") { LoginScreen(navController, loginViewModel) }      // 로그인 화면
                composable("mypage") { MyPageScreen(navController, myPageViewModel) }   // 마이페이지 화면
                composable("home") { HomeScreen(navController, calendarViewModel, attendanceViewModel) { scope.launch { drawerState.open() } } }    // 홈 화면

                composable("codeAdd") { CodeAddScreen(navController, codeViewModel) }       // 공통코드 등록 화면
                composable("codeDetail") { CodeDetailScreen(navController, codeViewModel) } // 공통코드 상세 화면
                composable("codeEdit") { CodeEditScreen(navController, codeViewModel) }     // 공통코드 수정 화면
                composable("codeManage") { CodeManageScreen(navController, codeViewModel) } // 공통코드 관리 화면

                composable("employeeAdd") { EmployeeAddScreen(navController, employeeViewModel) }         // 직원 등록 화면
                composable("employeeDetail") { EmployeeDetailScreen(navController, employeeViewModel) }   // 직원 상세 화면
                composable("employeeEdit") { EmployeeEditScreen(navController, employeeViewModel) }       // 직원 수정 화면
                composable("employeeManage") { EmployeeManageScreen(navController, employeeViewModel) }   // 직원 관리 화면
                composable("employeeSearch") { EmployeeSearchScreen(navController, employeeViewModel) }   // 직원 검색 화면

                composable("departmentAdd") { DepartmentAddScreen(navController, departmentViewModel) }         // 부서 등록 화면
                composable("departmentDetail") { DepartmentDetailScreen(navController, departmentViewModel) }   // 부서 상세 화면
                composable("departmentManage") { DepartmentManageScreen(navController, departmentViewModel) }   // 부서 관리 화면
            }
        }
    }
}