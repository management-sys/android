package com.example.attendancemanagementapp.ui.hr.employee.manage

import com.example.attendancemanagementapp.data.dto.HrDTO

data class EmployeeManageState(
    val employees: List<HrDTO.ManageEmployeesInfo> = emptyList(),   // 직원 관리 목록
    val searchText: String = "",                                    // 검색어
    val dropDownState: DropDownState = DropDownState(         // 선택한 드롭다운 필드 값
        department = "부서",
        grade = "직급",
        title = "직책"
    ),
    val dropDownMenu: DropDownMenu = DropDownMenu(),                // 드롭다운 메뉴
    val currentPage: Int = 0,                                       // 현재 페이지 번호
    val totalPage: Int = Int.MAX_VALUE,                             // 총 페이지 개수
    val isLoading: Boolean = false                                  // 로딩 중 여부
)

data class DropDownState(
    val department: String, // 부서
    val grade: String,      // 직급
    val title: String       // 직책
)

data class DropDownMenu(
    val departmentMenu: List<HrDTO.DepartmentsInfo> = listOf(HrDTO.DepartmentsInfo(name = "부서", depth = 0)),  // 부서 드롭다운 메뉴
    val gradeMenu: List<String> = listOf("직급", "대표", "사장", "부장", "차장", "과장", "대리", "사원", "인턴"),     // 직급 드롭다운 메뉴
    val titleMenu: List<String> = listOf("직책", "파트장", "본부장", "실장", "팀장")                                // 직책 드롭다운 메뉴
)