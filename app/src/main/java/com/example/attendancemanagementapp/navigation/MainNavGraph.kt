package com.example.attendancemanagementapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.ui.asset.car.CarViewModel
import com.example.attendancemanagementapp.ui.asset.car.add.CarAddScreen
import com.example.attendancemanagementapp.ui.asset.car.detail.CarDetailScreen
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditScreen
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageScreen
import com.example.attendancemanagementapp.ui.asset.car.usage.CarUsageScreen
import com.example.attendancemanagementapp.ui.asset.card.CardViewModel
import com.example.attendancemanagementapp.ui.asset.card.add.CardAddScreen
import com.example.attendancemanagementapp.ui.asset.card.detail.CardDetailScreen
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditScreen
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageScreen
import com.example.attendancemanagementapp.ui.asset.card.usage.CardUsageScreen
import com.example.attendancemanagementapp.ui.attendance.report.ReportViewModel
import com.example.attendancemanagementapp.ui.attendance.report.add.TripReportAddScreen
import com.example.attendancemanagementapp.ui.attendance.trip.TripViewModel
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddScreen
import com.example.attendancemanagementapp.ui.attendance.trip.add.TripAddState
import com.example.attendancemanagementapp.ui.attendance.trip.detail.TripDetailScreen
import com.example.attendancemanagementapp.ui.attendance.trip.edit.TripEditScreen
import com.example.attendancemanagementapp.ui.attendance.trip.status.TripStatusScreen
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.attendance.vacation.add.VacationAddScreen
import com.example.attendancemanagementapp.ui.attendance.vacation.detail.VacationDetailScreen
import com.example.attendancemanagementapp.ui.attendance.vacation.edit.VacationEditScreen
import com.example.attendancemanagementapp.ui.attendance.vacation.status.VacationStatusScreen
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
import com.example.attendancemanagementapp.ui.meeting.MeetingViewModel
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddScreen
import com.example.attendancemanagementapp.ui.meeting.detail.MeetingDetailScreen
import com.example.attendancemanagementapp.ui.meeting.edit.MeetingEditScreen
import com.example.attendancemanagementapp.ui.meeting.status.MeetingStatusScreen
import com.example.attendancemanagementapp.ui.mypage.MyPageScreen
import com.example.attendancemanagementapp.ui.mypage.MyPageViewModel
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.project.add.ProjectAddScreen
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailScreen
import com.example.attendancemanagementapp.ui.project.edit.ProjectEditScreen
import com.example.attendancemanagementapp.ui.project.personnel.ProjectPersonnelScreen
import com.example.attendancemanagementapp.ui.project.personnelDetail.ProjectPersonnelDetailScreen
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController(), tokenDataStore: TokenDataStore) {
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
    val projectViewModel: ProjectViewModel = hiltViewModel()
    val meetingViewModel: MeetingViewModel = hiltViewModel()
    val vacationViewModel: VacationViewModel = hiltViewModel()
    val carViewModel: CarViewModel = hiltViewModel()
    val cardViewModel: CardViewModel = hiltViewModel()
    val tripViewModel: TripViewModel = hiltViewModel()
    val reportViewModel: ReportViewModel = hiltViewModel()

    val logoutFlag by tokenDataStore.logoutFlagFlow.collectAsState(initial = false)

    LaunchedEffect(logoutFlag) {
        if (logoutFlag) {
            navController.navigate("login") { popUpTo(0) }
            tokenDataStore.setLogoutFlag(false)
        }
    }

    CollectUiEffect(
        navController = navController,
        codeViewModel.uiEffect,
        employeeViewModel.uiEffect,
        departmentViewModel.uiEffect,
        calendarViewModel.uiEffect,
        attendanceViewModel.uiEffect,
        loginViewModel.uiEffect,
        myPageViewModel.uiEffect,
        tokenDataStore.uiEffect,
        projectViewModel.uiEffect,
        meetingViewModel.uiEffect,
        vacationViewModel.uiEffect,
        carViewModel.uiEffect,
        cardViewModel.uiEffect,
        tripViewModel.uiEffect,
        reportViewModel.uiEffect
    )

    BasicDrawer(
        drawerState = drawerState,
        enableGesture = enableGesture,
        onItemClick = { route ->
            navController.navigate(route)
            scope.launch { drawerState.close() }
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

                composable("projectAdd") { ProjectAddScreen(navController, projectViewModel) }                          // 프로젝트 등록 화면
                composable("projectDetail") { ProjectDetailScreen(navController, projectViewModel, meetingViewModel) }  // 프로젝트 상세 화면
                composable("projectEdit") { ProjectEditScreen(navController, projectViewModel) }                        // 프로젝트 수정 화면
                composable("projectPersonnel") { ProjectPersonnelScreen(navController, projectViewModel) }              // 프로젝트 투입 현황 화면
                composable("projectPersonnelDetail") { ProjectPersonnelDetailScreen(navController, projectViewModel) }  // 프로젝트 투입 현황 상세 화면
                composable("projectStatus") { ProjectStatusScreen(navController, projectViewModel) }                    // 프로젝트 현황 화면

                composable("meetingAdd") { MeetingAddScreen(navController, meetingViewModel) }          // 회의록 등록 화면
                composable("meetingDetail") { MeetingDetailScreen(navController, meetingViewModel) }    // 회의록 상세 화면
                composable("meetingEdit") { MeetingEditScreen(navController, meetingViewModel) }        // 회의록 수정 화면
                composable("meetingStatus") { MeetingStatusScreen(navController, meetingViewModel) }    // 회의록 현황 화면

                composable("vacationAdd") { VacationAddScreen(navController, vacationViewModel) }       // 휴가 신청 화면
                composable("vacationDetail") { VacationDetailScreen(navController, vacationViewModel) } // 휴가 상세 화면
                composable("vacationEdit") { VacationEditScreen(navController, vacationViewModel) }     // 휴가 수정 화면
                composable("vacationStatus") { VacationStatusScreen(navController, vacationViewModel) } // 휴가 현황 화면

                composable("carAdd") { CarAddScreen(navController, carViewModel) }          // 차량정보 등록 화면
                composable("carDetail") { CarDetailScreen(navController, carViewModel) }    // 차량정보 상세 화면
                composable("carEdit") { CarEditScreen(navController, carViewModel) }        // 차량정보 수정 화면
                composable("carManage") { CarManageScreen(navController, carViewModel) }    // 차량정보 관리 화면
                composable("carUsage") { CarUsageScreen(navController, carViewModel) }      // 차량 사용현황 화면

                composable("cardAdd") { CardAddScreen(navController, cardViewModel) }       // 카드정보 등록 화면
                composable("cardDetail") { CardDetailScreen(navController, cardViewModel) } // 카드정보 상세 화면
                composable("cardEdit") { CardEditScreen(navController, cardViewModel) }     // 카드정보 수정 화면
                composable("cardManage") { CardManageScreen(navController, cardViewModel) } // 카드정보 관리 화면
                composable("cardUsage") { CardUsageScreen(navController, cardViewModel) }   // 카드 사용현황 화면

                composable("tripAdd") { TripAddScreen(navController, tripViewModel) }                           // 출장 신청 화면
                composable("tripDetail") { TripDetailScreen(navController, tripViewModel, reportViewModel) }    // 출장 상세 화면
                composable("tripEdit") { TripEditScreen(navController, tripViewModel) }                         // 출장 수정 화면
                composable("tripStatus") { TripStatusScreen(navController, tripViewModel) }                     // 출장 현황 화면

                composable("reportAdd") { TripReportAddScreen(navController, reportViewModel) } // 출장 복명서 등록 화면
            }
        }
    }
}