package com.example.attendancemanagementapp.navigation

sealed class RootRoute(val route: String) {
    // 결재관리, 근태관리, 업무관리, 재무관리, 자산관리 추가
    data object HR: RootRoute("hr")         // 인사관리
    data object SYSTEM: RootRoute("system") // 시스템관리
}

/* 인사관리 그래프 */
sealed class HrRoute(val route: String) {
    data object EmployeeManage : HrRoute("hr/employee/manage")
    data object EmployeeSearch : HrRoute("hr/employee/search")
    data object EmployeeDetail : HrRoute("hr/employee/detail")
    data object EmployeeEdit : HrRoute("hr/employee/edit")
    data object EmployeeAdd : HrRoute("hr/employee/add")
    // 부서 관련 추가
}

/* 시스템관리 그래프 */
sealed class SystemRoute(val route: String) {
    data object CodeManage : SystemRoute("system/code/manage")
    data object CodeDetail : SystemRoute("system/code/detail")
    data object CodeEdit : SystemRoute("system/code/edit")
    data object CodeAdd : SystemRoute("system/code/add")
}