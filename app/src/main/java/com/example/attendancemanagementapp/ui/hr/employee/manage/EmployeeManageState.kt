package com.example.attendancemanagementapp.ui.hr.employee.manage

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class EmployeeManageState(
    val employees: List<EmployeeDTO.ManageEmployeesInfo> = emptyList(), // 직원 관리 목록
    val searchText: String = "",                                        // 검색어
    val dropDownState: DropDownState = DropDownState(                   // 선택한 드롭다운 필드 값
        department = "부서",
        grade = "직급",
        title = "직책"
    ),
    val dropDownMenu: DropDownMenu = DropDownMenu(departmentMenu = listOf(DepartmentDTO.DepartmentsInfo(name = "부서"))), // 드롭다운 메뉴
    val paginationState: PaginationState = PaginationState()                                                             // 페이지네이션 상태
)

data class DropDownState(
    val department: String, // 부서
    val grade: String,      // 직급
    val title: String       // 직책
)

data class DropDownMenu(
    val departmentMenu: List<DepartmentDTO.DepartmentsInfo> = emptyList(),  // 부서 드롭다운 메뉴
    val gradeMenu: List<String> = emptyList(),                              // 직급 드롭다운 메뉴
    val titleMenu: List<String> = emptyList()                               // 직책 드롭다운 메뉴
)